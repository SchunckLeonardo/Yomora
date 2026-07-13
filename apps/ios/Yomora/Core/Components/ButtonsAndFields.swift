import SwiftUI

struct PrimaryButton: View {
    let title: String
    var systemImage: String?
    var isLoading = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: YomoraSpacing.sm) {
                if isLoading { ProgressView().tint(.white) }
                else if let systemImage { Image(systemName: systemImage) }
                Text(title).font(.headline)
            }
            .frame(maxWidth: .infinity, minHeight: 52)
        }
        .buttonStyle(.plain)
        .foregroundStyle(.white)
        .background(YomoraColor.primary, in: RoundedRectangle(cornerRadius: YomoraRadius.button))
        .disabled(isLoading)
        .accessibilityLabel(title)
    }
}

struct SecondaryButton: View {
    let title: String
    let action: () -> Void
    var body: some View {
        Button(title, action: action)
            .font(.headline)
            .frame(maxWidth: .infinity, minHeight: 50)
            .foregroundStyle(YomoraColor.primary)
            .background(.clear, in: RoundedRectangle(cornerRadius: YomoraRadius.button))
            .overlay(RoundedRectangle(cornerRadius: YomoraRadius.button).stroke(YomoraColor.sereneTeal))
    }
}

struct YomoraTextField: View {
    let title: String
    var prompt: String = ""
    @Binding var text: String
    var secure = false
    var keyboard: UIKeyboardType = .default

    var body: some View {
        VStack(alignment: .leading, spacing: YomoraSpacing.xs) {
            Text(title).font(.caption.weight(.semibold)).foregroundStyle(.secondary)
            Group {
                if secure { SecureField(prompt, text: $text) }
                else { TextField(prompt, text: $text).keyboardType(keyboard).textInputAutocapitalization(.never) }
            }
            .padding(.horizontal, 14)
            .frame(minHeight: 50)
            .background(.background, in: RoundedRectangle(cornerRadius: YomoraRadius.button))
            .overlay(RoundedRectangle(cornerRadius: YomoraRadius.button).stroke(.secondary.opacity(0.25)))
        }
    }
}

