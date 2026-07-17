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
        let (responseData, response) = try await session.upload(for: request, from: data)
        guard let http = response as? HTTPURLResponse, (200..<300).contains(http.statusCode) else {
            let status = (response as? HTTPURLResponse)?.statusCode ?? 0
            throw APIError.server(
                status: status,
                message: Self.uploadFailureMessage(status: status, responseData: responseData)
            )
        }
    }

    private static func uploadFailureMessage(status: Int, responseData: Data) -> String {
        switch xmlValue(named: "Code", in: responseData) {
        case "SignatureDoesNotMatch":
            "A assinatura do upload foi recusada pela AWS. Verifique AWS_REGION e deixe AWS_S3_ENDPOINT vazio ao usar o Amazon S3."
        case "AccessDenied":
            "A AWS recusou o envio da foto. Verifique se a credencial possui a permissão s3:PutObject no bucket."
        case "ExpiredToken":
            "A credencial temporária da AWS expirou. Atualize as credenciais do servidor e tente novamente."
        case "InvalidAccessKeyId":
            "A chave de acesso configurada no servidor não foi reconhecida pela AWS."
        case "AuthorizationHeaderMalformed", "PermanentRedirect":
            "O bucket está em outra região. Corrija AWS_REGION no servidor e tente novamente."
        case let code?:
            "O Amazon S3 recusou o envio da foto (\(code), HTTP \(status))."
        case nil:
            "Não foi possível enviar a foto de perfil (HTTP \(status))."
        }
    }

    private static func xmlValue(named name: String, in data: Data) -> String? {
        guard let xml = String(data: data, encoding: .utf8) else { return nil }
        let openingTag = "<\(name)>"
        let closingTag = "</\(name)>"
        guard let openingRange = xml.range(of: openingTag),
              let closingRange = xml.range(of: closingTag, range: openingRange.upperBound..<xml.endIndex) else {
            return nil
        }
        return String(xml[openingRange.upperBound..<closingRange.lowerBound])
    }
}
