import Observation
import SwiftUI

@MainActor
@Observable
final class CommunityAccessViewModel {
    enum State: Equatable {
        case loading
        case allowed
        case suspended(CommunityAccess)
        case error(String)
    }

    private(set) var state: State = .loading
    private let api: any APIClientProtocol

    init(api: any APIClientProtocol) {
        self.api = api
    }

    func load() async {
        state = .loading
        do {
            let access = try await api.send(
                Endpoint(path: "/api/v1/community/access"),
                as: CommunityAccess.self
            )
            state = access.allowed ? .allowed : .suspended(access)
        } catch {
            state = .error(error.localizedDescription)
        }
    }
}

struct CommunityAccessView: View {
    let container: AppContainer
    let session: SessionStore
    @State private var model: CommunityAccessViewModel

    init(container: AppContainer, session: SessionStore) {
        self.container = container
        self.session = session
        _model = State(initialValue: CommunityAccessViewModel(api: container.api))
    }

    var body: some View {
        Group {
            switch model.state {
            case .loading:
                ProgressView("Verificando acesso à Comunidade…")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            case .allowed:
                FeedView(container: container, session: session)
            case let .suspended(access):
                suspendedView(access)
            case let .error(message):
                ErrorStateView(message: message) { Task { await model.load() } }
            }
        }
        .background(YomoraColor.canvas)
        .task { await model.load() }
    }

    private func suspendedView(_ access: CommunityAccess) -> some View {
        ContentUnavailableView {
            Label("Comunidade temporariamente indisponível", systemImage: "person.2.slash")
        } description: {
            VStack(spacing: 8) {
                if let reason = access.reason { Text("Motivo: \(reason)") }
                if let date = formatted(access.suspendedUntil) { Text("Acesso liberado em \(date).") }
                Text("Sua leitura e sua biblioteca continuam disponíveis normalmente.")
            }
        } actions: {
            Button("Verificar novamente") { Task { await model.load() } }
                .buttonStyle(.bordered)
        }
        .padding()
        .navigationTitle("Comunidade")
    }

    private func formatted(_ value: String?) -> String? {
        guard let value, let date = ISO8601DateFormatter().date(from: value) else { return value }
        return date.formatted(date: .abbreviated, time: .shortened)
    }
}
