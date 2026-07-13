import SwiftUI

struct OnboardingView: View {
    let onFinish: () -> Void
    @State private var page = 0
    @AppStorage("dailyGoalMinutes") private var dailyMinutes = 20
    @AppStorage("weeklyGoalDays") private var weeklyDays = 5

    var body: some View {
        ZStack {
            YomoraColor.warmPaper.ignoresSafeArea()
            VStack {
                HStack {
                    Spacer()
                    if page < 2 { Button("Pular", action: onFinish).padding() }
                }
                TabView(selection: $page) {
                    OnboardingPage(image: "book.pages", eyebrow: "CONSTÂNCIA",
                                   title: "Leia um pouco.\nTodos os dias.",
                                   text: "Sessões leves transformam intenção em páginas concluídas.").tag(0)
                    OnboardingPage(image: "books.vertical.fill", eyebrow: "SUA BIBLIOTECA",
                                   title: "Cada livro no\nseu lugar.",
                                   text: "Organize edições, estantes, notas e o próximo capítulo.").tag(1)
                    goalPage.tag(2)
                }
                .tabViewStyle(.page(indexDisplayMode: .always))
                PrimaryButton(title: page == 2 ? "Começar minha jornada" : "Continuar", systemImage: "arrow.right") {
                    if page < 2 { withAnimation { page += 1 } } else { onFinish() }
                }
                .padding(YomoraSpacing.md)
            }
        }
    }

    private var goalPage: some View {
        VStack(spacing: 28) {
            Image(systemName: "target").font(.system(size: 68)).foregroundStyle(YomoraColor.progressGold)
            Text("Uma meta que cabe\nno seu dia").font(.yomoraTitle).multilineTextAlignment(.center)
            VStack(spacing: 18) {
                Stepper("\(dailyMinutes) minutos por dia", value: $dailyMinutes, in: 5...120, step: 5)
                Stepper("\(weeklyDays) dias por semana", value: $weeklyDays, in: 1...7)
            }
            .padding().background(.white.opacity(0.75), in: RoundedRectangle(cornerRadius: YomoraRadius.card))
            Text("Você pode ajustar a qualquer momento. Sem culpa, só ritmo.").font(.subheadline).foregroundStyle(.secondary)
        }
        .padding(28)
        .accessibilityElement(children: .contain)
    }
}

private struct OnboardingPage: View {
    let image: String
    let eyebrow: String
    let title: String
    let text: String
    var body: some View {
        VStack(spacing: 28) {
            Spacer()
            ZStack {
                Circle().fill(YomoraColor.sereneTeal.opacity(0.1)).frame(width: 210, height: 210)
                Image(systemName: image).font(.system(size: 86, weight: .light)).foregroundStyle(YomoraColor.sereneTeal)
            }
            Text(eyebrow).font(.caption.weight(.bold)).tracking(2).foregroundStyle(YomoraColor.progressGold)
            Text(title).font(.yomoraTitle).multilineTextAlignment(.center).foregroundStyle(YomoraColor.primary)
            Text(text).font(.yomoraEditorial).multilineTextAlignment(.center).foregroundStyle(YomoraColor.sepiaInk).padding(.horizontal, 20)
            Spacer()
        }
        .padding()
    }
}

struct OnboardingView_Previews: PreviewProvider {
    static var previews: some View { OnboardingView { } }
}
