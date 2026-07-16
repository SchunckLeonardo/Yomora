import CryptoKit
import Foundation
import Security

enum AppleSignInNonce {
    enum Error: Swift.Error { case randomGenerationFailed(OSStatus) }

    static func random(length: Int = 32) throws -> String {
        precondition(length > 0)
        let characters = Array("0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._")
        var result = ""
        result.reserveCapacity(length)

        while result.count < length {
            var bytes = [UInt8](repeating: 0, count: 16)
            let status = SecRandomCopyBytes(kSecRandomDefault, bytes.count, &bytes)
            guard status == errSecSuccess else { throw Error.randomGenerationFailed(status) }
            for byte in bytes where result.count < length {
                guard byte < characters.count * (256 / characters.count) else { continue }
                result.append(characters[Int(byte) % characters.count])
            }
        }
        return result
    }

    static func sha256(_ value: String) -> String {
        SHA256.hash(data: Data(value.utf8)).map { String(format: "%02x", $0) }.joined()
    }
}
