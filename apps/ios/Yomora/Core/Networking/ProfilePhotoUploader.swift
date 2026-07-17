import Foundation

protocol ProfilePhotoUploading: Sendable {
    func upload(_ data: Data, to url: URL, contentType: String) async throws
}

struct ProfilePhotoUploader: ProfilePhotoUploading {
    private let session: URLSession

    init(session: URLSession = .shared) {
        self.session = session
    }

    func upload(_ data: Data, to url: URL, contentType: String) async throws {
        var request = URLRequest(url: url, timeoutInterval: 60)
        request.httpMethod = "PUT"
        request.setValue(contentType, forHTTPHeaderField: "Content-Type")
        request.setValue(String(data.count), forHTTPHeaderField: "Content-Length")
        let (_, response) = try await session.upload(for: request, from: data)
        guard let http = response as? HTTPURLResponse, (200..<300).contains(http.statusCode) else {
            throw APIError.server(status: (response as? HTTPURLResponse)?.statusCode ?? 0,
                                  message: "Não foi possível enviar a foto de perfil.")
        }
    }
}
