import SwiftUI

struct ActiveReadingSessionBanner: View {
    let context: ActiveReadingSessionContext

    var body: some View {
        NavigationLink(value: AppRoute.reading(context.entry, book: context.book)) {
            HStack(spacing: YomoraSpacing.md) {
                Image(systemName: context.session.pausedAt == nil ? "timer" : "pause.circle.fill")
                    .font(.title2)
                    .foregroundStyle(YomoraColor.progressGold)
                    .frame(width: 42, height: 42)
                    .background(YomoraColor.progressGold.opacity(0.14), in: Circle())
                VStack(alignment: .leading, spacing: 4) {
                    Text("SESSÃO EM ANDAMENTO")
                        .font(.caption2.weight(.bold))
                        .tracking(1.2)
                        .foregroundStyle(YomoraColor.sereneTeal)
                    Text(context.book.title)
                        .font(.headline)
                        .foregroundStyle(YomoraColor.textPrimary)
                        .lineLimit(2)
                    Text("Página \(context.session.currentPage) · \(context.session.pausedAt == nil ? "Lendo agora" : "Pausada")")
                        .font(.caption)
                        .foregroundStyle(YomoraColor.textSecondary)
                }
                Spacer(minLength: 8)
                Image(systemName: "chevron.right")
                    .foregroundStyle(YomoraColor.textSecondary)
            }
            .padding()
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(YomoraColor.surfaceElevated, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
            .overlay {
                RoundedRectangle(cornerRadius: YomoraRadius.card)
                    .stroke(YomoraColor.sereneTeal.opacity(0.45))
            }
        }
        .buttonStyle(.plain)
        .accessibilityIdentifier("activeReadingSessionBanner")
    }
}
