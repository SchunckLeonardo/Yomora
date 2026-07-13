import Foundation
import Security

protocol TokenStoring: Sendable {
    func load() async throws -> TokenPair?
    func save(_ tokens: TokenPair) async throws
    func clear() async throws
}

actor KeychainTokenStore: TokenStoring {
    private let service = "app.yomora.tokens"
    private let account = "session"

    func load() throws -> TokenPair? {
        var query = baseQuery
        query[kSecReturnData as String] = true
        query[kSecMatchLimit as String] = kSecMatchLimitOne
        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        if status == errSecItemNotFound { return nil }
        guard status == errSecSuccess, let data = result as? Data else { throw KeychainError(status) }
        return try JSONDecoder().decode(TokenPair.self, from: data)
    }

    func save(_ tokens: TokenPair) throws {
        let data = try JSONEncoder().encode(tokens)
        SecItemDelete(baseQuery as CFDictionary)
        var query = baseQuery
        query[kSecValueData as String] = data
        query[kSecAttrAccessible as String] = kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        let status = SecItemAdd(query as CFDictionary, nil)
        guard status == errSecSuccess else { throw KeychainError(status) }
    }

    func clear() throws {
        let status = SecItemDelete(baseQuery as CFDictionary)
        guard status == errSecSuccess || status == errSecItemNotFound else { throw KeychainError(status) }
    }

    private var baseQuery: [String: Any] {
        [kSecClass as String: kSecClassGenericPassword,
         kSecAttrService as String: service,
         kSecAttrAccount as String: account]
    }
}

struct KeychainError: Error, Equatable { let status: OSStatus; init(_ status: OSStatus) { self.status = status } }

actor InMemoryTokenStore: TokenStoring {
    private var tokens: TokenPair?
    init(tokens: TokenPair? = nil) { self.tokens = tokens }
    func load() -> TokenPair? { tokens }
    func save(_ tokens: TokenPair) { self.tokens = tokens }
    func clear() { tokens = nil }
}

