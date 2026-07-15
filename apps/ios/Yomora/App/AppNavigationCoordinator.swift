import Observation

@MainActor
@Observable
final class AppNavigationCoordinator {
    enum Destination: Equatable {
        case today
        case activeReadingSession
    }

    private(set) var requestId = 0
    private(set) var destination: Destination?

    func open(_ destination: Destination) {
        self.destination = destination
        requestId += 1
    }

    func consume(_ requestId: Int) {
        guard self.requestId == requestId else { return }
        destination = nil
    }
}
