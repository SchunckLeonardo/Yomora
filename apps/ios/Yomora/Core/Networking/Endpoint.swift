import Foundation

struct Endpoint: Sendable, Equatable {
    enum Method: String, Sendable { case get = "GET", post = "POST", patch = "PATCH", put = "PUT", delete = "DELETE" }

    let path: String
    var method: Method = .get
    var query: [URLQueryItem] = []
    var body: Data?
    var authenticated = true

    static func json<T: Encodable>(_ value: T) throws -> Data {
        try JSONEncoder().encode(value)
    }
}

enum APIError: LocalizedError, Equatable {
    case invalidURL
    case unauthorized
    case server(status: Int, message: String)
    case invalidResponse

    var errorDescription: String? {
        switch self {
        case .invalidURL: "Endereço da API inválido."
        case .unauthorized: "Sua sessão expirou. Entre novamente."
        case let .server(_, message): message
        case .invalidResponse: "Não foi possível interpretar a resposta."
        }
    }
}

struct ProblemDetail: Decodable, Sendable {
    let detail: String?
    let title: String?
}

