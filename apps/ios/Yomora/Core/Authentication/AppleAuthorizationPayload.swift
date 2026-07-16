import Foundation

struct AppleAuthorizationPayload: Encodable, Sendable, Equatable {
    let identityToken: String
    let authorizationCode: String
    let nonce: String
    let fullName: String?
}
