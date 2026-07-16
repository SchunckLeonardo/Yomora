import SwiftUI

struct EmailVerificationBanner: View {
    let session: SessionStore
    @State private var sending = false
    @State private var sent = false

    var body: some View {
        VStack(alignment: .leading, spacing: YomoraSpacing.sm) {
            Label("Confirme seu e-mail", systemImage: "envelope.badge")
                .font(.headline)
                .foregroundStyle(YomoraColor.primary)
            Text("Enviamos um link para \(session.user?.email ?? "seu e-mail"). Você pode continuar usando o Yomora.")
                .font(.footnote)
                .foregroundStyle(YomoraColor.textSecondary)
            Button(sent ? "Link enviado" : "Reenviar confirmação") {
                Task {
                    sending = true
                    sent = await session.requestEmailVerification()
                    sending = false
                }
            }
            .font(.footnote.weight(.semibold))
            .disabled(sending || sent)
        }
        .padding(.horizontal, YomoraSpacing.md)
        .padding(.vertical, YomoraSpacing.sm)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(YomoraColor.progressGold.opacity(0.16))
        .overlay(alignment: .bottom) { Divider() }
        .accessibilityElement(children: .contain)
    }
}
