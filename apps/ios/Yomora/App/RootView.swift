import SwiftUI

struct RootView: View {
    let container: AppContainer
    @State private var session: SessionStore
    @State private var showingSplash = true
    @AppStorage("hasOnboarded") private var hasOnboarded = false

    init(container: AppContainer) {
        self.container = container
        _session = State(initialValue: SessionStore(api: container.api, tokens: container.tokenStore))
    }

    var body: some View {
        Group {
            if showingSplash { SplashView() }
            else if !hasOnboarded { OnboardingView { hasOnboarded = true } }
            else {
                switch session.state {
                case .restoring: ProgressView("Retomando sua leitura…")
                case .signedOut: AuthenticationView(session: session)
                case .signedIn: MainTabView(container: container, session: session)
                }
            }
        }
        .task {
            if ProcessInfo.processInfo.arguments.contains("--skip-onboarding") { hasOnboarded = true }
            async let restore: Void = session.restore()
            try? await Task.sleep(for: .milliseconds(650))
            withAnimation(.easeOut(duration: 0.25)) { showingSplash = false }
            _ = await restore
        }
    }
}

private struct SplashView: View {
    var body: some View {
        ZStack {
            YomoraColor.canvas.ignoresSafeArea()
            VStack(spacing: 18) {
                Image("Logo").resizable().scaledToFit().frame(width: 190, height: 190)
                Text("Yomora").font(.system(size: 44, weight: .semibold, design: .serif)).foregroundStyle(YomoraColor.primary)
                Text("Transformar livros começados em livros terminados.")
                    .font(.yomoraEditorial).multilineTextAlignment(.center).foregroundStyle(YomoraColor.textSecondary)
                    .padding(.horizontal, 40)
            }
        }
        .accessibilityElement(children: .combine)
        .accessibilityLabel("Yomora. Transformar livros começados em livros terminados.")
    }
}
