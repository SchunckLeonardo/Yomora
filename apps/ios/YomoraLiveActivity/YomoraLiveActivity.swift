import ActivityKit
import SwiftUI
import WidgetKit

@main
struct YomoraLiveActivityBundle: WidgetBundle {
    var body: some Widget {
        ReadingLiveActivityWidget()
    }
}

struct ReadingLiveActivityWidget: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: ReadingActivityAttributes.self) { context in
            lockScreen(context)
                .activityBackgroundTint(Color(red: 0.05, green: 0.20, blue: 0.22))
                .activitySystemActionForegroundColor(.white)
                .widgetURL(deepLink(for: context.attributes.sessionId))
        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    Image(systemName: context.state.status == .running ? "book.fill" : "pause.fill")
                        .foregroundStyle(gold)
                }
                DynamicIslandExpandedRegion(.trailing) {
                    activityTimer(context.state)
                        .font(.headline.monospacedDigit())
                }
                DynamicIslandExpandedRegion(.center) {
                    Text(context.attributes.bookTitle)
                        .font(.headline)
                        .lineLimit(1)
                }
                DynamicIslandExpandedRegion(.bottom) {
                    HStack {
                        Label("Página \(context.state.currentPage)", systemImage: "book.pages")
                        Spacer()
                        Text(context.state.status == .running ? "Lendo agora" : "Sessão pausada")
                    }
                    .font(.caption)
                    .foregroundStyle(.secondary)
                }
            } compactLeading: {
                Image(systemName: context.state.status == .running ? "book.fill" : "pause.fill")
                    .foregroundStyle(gold)
            } compactTrailing: {
                activityTimer(context.state)
                    .font(.caption2.monospacedDigit())
                    .frame(maxWidth: 52)
            } minimal: {
                Image(systemName: "book.fill")
                    .foregroundStyle(gold)
            }
            .widgetURL(deepLink(for: context.attributes.sessionId))
            .keylineTint(gold)
        }
    }

    private func lockScreen(_ context: ActivityViewContext<ReadingActivityAttributes>) -> some View {
        HStack(spacing: 14) {
            Image(systemName: context.state.status == .running ? "book.fill" : "pause.fill")
                .font(.title2)
                .foregroundStyle(gold)
                .frame(width: 44, height: 44)
                .background(gold.opacity(0.16), in: Circle())
            VStack(alignment: .leading, spacing: 5) {
                Text(context.attributes.bookTitle)
                    .font(.headline)
                    .lineLimit(1)
                Text("Página \(context.state.currentPage) · \(context.state.status == .running ? "Lendo agora" : "Pausada")")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
            Spacer(minLength: 8)
            activityTimer(context.state)
                .font(.title3.monospacedDigit())
                .foregroundStyle(.white)
        }
        .padding()
    }

    @ViewBuilder
    private func activityTimer(_ state: ReadingActivityAttributes.ContentState) -> some View {
        if let timerStartedAt = state.timerStartedAt, state.status == .running {
            Text(timerStartedAt, style: .timer)
        } else {
            Text(formatted(state.elapsedSeconds))
        }
    }

    private func formatted(_ seconds: Int) -> String {
        String(format: "%02d:%02d", seconds / 60, seconds % 60)
    }

    private func deepLink(for sessionId: UUID) -> URL? {
        URL(string: "yomora://reading/\(sessionId.uuidString)")
    }

    private var gold: Color { Color(red: 0.91, green: 0.76, blue: 0.31) }
}
