import SwiftUI
import Observation

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
        .foregroundStyle(YomoraColor.onInteractive)
        .background(YomoraColor.interactiveFill, in: RoundedRectangle(cornerRadius: YomoraRadius.button))
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
            .foregroundStyle(YomoraColor.sereneTeal)
            .background(.clear, in: RoundedRectangle(cornerRadius: YomoraRadius.button))
            .overlay(RoundedRectangle(cornerRadius: YomoraRadius.button).stroke(YomoraColor.sereneTeal))
    }
}

@Observable
final class TextDraft {
    var value: String

    init(_ value: String = "") {
        self.value = value
    }

    var trimmed: String {
        value.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    var isBlank: Bool { trimmed.isEmpty }
}

struct DraftTextField: View {
    let prompt: String
    @Bindable var draft: TextDraft
    var axis: Axis = .horizontal

    var body: some View {
        TextField(prompt, text: $draft.value, axis: axis)
    }
}

struct DraftActionButton: View {
    let title: String
    @Bindable var draft: TextDraft
    var isBusy = false
    let action: () -> Void

    var body: some View {
        Button(title, action: action)
            .disabled(draft.isBlank || isBusy)
    }
}

struct YomoraTextField: View {
    let title: String
    var prompt: String = ""
    @Binding var text: String
    var secure = false
    var keyboard: UIKeyboardType = .default
    var contentType: UITextContentType?

    var body: some View {
        VStack(alignment: .leading, spacing: YomoraSpacing.xs) {
            Text(title).font(.caption.weight(.semibold)).foregroundStyle(.secondary)
            Group {
                if secure {
                    SecureField(prompt, text: $text)
                        .textContentType(contentType ?? .password)
                } else {
                    TextField(prompt, text: $text)
                        .keyboardType(keyboard)
                        .textInputAutocapitalization(.never)
                        .textContentType(contentType)
                }
            }
            .autocorrectionDisabled(secure || keyboard == .emailAddress || keyboard == .URL)
            .padding(.horizontal, 14)
            .frame(minHeight: 50)
            .background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: YomoraRadius.button))
            .overlay(RoundedRectangle(cornerRadius: YomoraRadius.button).stroke(YomoraColor.outline))
        }
    }
}

struct YomoraDraftField: View {
    let title: String
    var prompt: String = ""
    @Bindable var draft: TextDraft
    var secure = false
    var keyboard: UIKeyboardType = .default
    var contentType: UITextContentType?

    var body: some View {
        YomoraTextField(
            title: title,
            prompt: prompt,
            text: $draft.value,
            secure: secure,
            keyboard: keyboard,
            contentType: contentType
        )
    }
}
