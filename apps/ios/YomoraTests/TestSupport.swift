import Foundation
@testable import Yomora

actor StubAPIClient: APIClientProtocol {
    private let responses: [String: Data]
    private(set) var paths: [String] = []

    init(responses: [String: Data]) { self.responses = responses }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        paths.append(endpoint.path)
        guard let data = responses[endpoint.path] else { throw APIError.server(status: 404, message: endpoint.path) }
        return try JSONDecoder().decode(T.self, from: data)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { paths.append(endpoint.path) }
}

extension Encodable {
    var encoded: Data { try! JSONEncoder().encode(self) }
}

