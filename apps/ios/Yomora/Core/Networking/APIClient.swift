import Foundation

protocol APIClientProtocol: Sendable {
    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T
    func sendVoid(_ endpoint: Endpoint) async throws
}

actor APIClient: APIClientProtocol {
    private let baseURL: URL
    private let session: URLSession
    private let tokenStore: any TokenStoring
    private let decoder = JSONDecoder()

    init(baseURL: URL, session: URLSession = .shared, tokenStore: any TokenStoring) {
        self.baseURL = baseURL
        self.session = session
        self.tokenStore = tokenStore
    }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        let data = try await perform(endpoint, retryAfterRefresh: true)
        do { return try decoder.decode(T.self, from: data) }
        catch { throw APIError.invalidResponse }
    }

    func sendVoid(_ endpoint: Endpoint) async throws {
        _ = try await perform(endpoint, retryAfterRefresh: true)
    }

    private func perform(_ endpoint: Endpoint, retryAfterRefresh: Bool) async throws -> Data {
        var request = try makeRequest(endpoint)
        if endpoint.authenticated, let access = try await tokenStore.load()?.accessToken {
            request.setValue("Bearer \(access)", forHTTPHeaderField: "Authorization")
        }
        let (data, response) = try await session.data(for: request)
        guard let http = response as? HTTPURLResponse else { throw APIError.invalidResponse }
        if http.statusCode == 401, endpoint.authenticated, retryAfterRefresh {
            try await refresh()
            return try await perform(endpoint, retryAfterRefresh: false)
        }
        guard (200..<300).contains(http.statusCode) else {
            let detail = try? decoder.decode(ProblemDetail.self, from: data)
            throw APIError.server(status: http.statusCode, message: detail?.detail ?? "Falha na API (\(http.statusCode)).")
        }
        return data
    }

    private func refresh() async throws {
        guard let refreshToken = try await tokenStore.load()?.refreshToken else { throw APIError.unauthorized }
        struct Body: Encodable { let refreshToken: String }
        let endpoint = Endpoint(path: "/api/v1/auth/refresh", method: .post,
                                body: try Endpoint.json(Body(refreshToken: refreshToken)), authenticated: false)
        let data = try await perform(endpoint, retryAfterRefresh: false)
        let tokens = try decoder.decode(TokenPair.self, from: data)
        try await tokenStore.save(tokens)
    }

    private func makeRequest(_ endpoint: Endpoint) throws -> URLRequest {
        guard var components = URLComponents(url: baseURL.appending(path: endpoint.path), resolvingAgainstBaseURL: false)
        else { throw APIError.invalidURL }
        if !endpoint.query.isEmpty { components.queryItems = endpoint.query }
        guard let url = components.url else { throw APIError.invalidURL }
        var request = URLRequest(url: url, timeoutInterval: 15)
        request.httpMethod = endpoint.method.rawValue
        request.httpBody = endpoint.body
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        if endpoint.body != nil { request.setValue("application/json", forHTTPHeaderField: "Content-Type") }
        return request
    }
}

