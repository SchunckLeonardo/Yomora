import XCTest
@testable import Yomora

final class APIClientTests: XCTestCase {
    override func tearDown() {
        URLProtocolStub.handler = nil
        super.tearDown()
    }

    func testAuthorizedRequestUsesURLSessionAndDecodesJSON() async throws {
        let expected = ReadingGoal(dailyMinutes: 20, weeklyDays: 5, dailyPages: 10)
        URLProtocolStub.handler = { request in
            XCTAssertEqual(request.value(forHTTPHeaderField: "Authorization"), "Bearer access")
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil,
                                           headerFields: ["Content-Type": "application/json"])!
            return (response, expected.encoded)
        }
        let configuration = URLSessionConfiguration.ephemeral
        configuration.protocolClasses = [URLProtocolStub.self]
        let tokenStore = InMemoryTokenStore(tokens: TokenPair(accessToken: "access", refreshToken: "refresh", tokenType: "Bearer", expiresIn: 900))
        let client = APIClient(baseURL: URL(string: "https://api.yomora.test")!, session: URLSession(configuration: configuration), tokenStore: tokenStore)

        let value = try await client.send(Endpoint(path: "/api/v1/reading-goals"), as: ReadingGoal.self)

        XCTAssertEqual(value, expected)
    }

    func testUITestClientAllowsCommunityAccess() async throws {
        let client = UITestAPIClient()

        let access = try await client.send(
            Endpoint(path: "/api/v1/community/access"),
            as: CommunityAccess.self
        )

        XCTAssertEqual(
            access,
            CommunityAccess(allowed: true, suspendedUntil: nil, reason: nil)
        )
    }
}

private final class URLProtocolStub: URLProtocol, @unchecked Sendable {
    nonisolated(unsafe) static var handler: ((URLRequest) throws -> (HTTPURLResponse, Data))?
    override class func canInit(with request: URLRequest) -> Bool { true }
    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }
    override func startLoading() {
        do {
            guard let handler = Self.handler else { throw APIError.invalidResponse }
            let (response, data) = try handler(request)
            client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
            client?.urlProtocol(self, didLoad: data)
            client?.urlProtocolDidFinishLoading(self)
        } catch { client?.urlProtocol(self, didFailWithError: error) }
    }
    override func stopLoading() { }
}
