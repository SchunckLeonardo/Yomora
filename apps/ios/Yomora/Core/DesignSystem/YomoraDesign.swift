import SwiftUI
import UIKit

enum YomoraColor {
    static let canvas = adaptive(light: 0xFAF7F0, dark: 0x111516)
    static let surface = adaptive(light: 0xFFFFFF, dark: 0x1B2021)
    static let surfaceElevated = adaptive(light: 0xF1EEE7, dark: 0x242B2C)
    static let textPrimary = adaptive(light: 0x1A2528, dark: 0xF5F1E8)
    static let textSecondary = adaptive(light: 0x59666B, dark: 0xBCC7C9)
    static let outline = adaptive(light: 0xD7D2C8, dark: 0x3A4547)

    static let primary = adaptive(light: 0x14333B, dark: 0xB8DDE2)
    static let sereneTeal = adaptive(light: 0x2C4A52, dark: 0x95CBD2)
    static let interactiveFill = adaptive(light: 0x14333B, dark: 0x79B3BB)
    static let onInteractive = adaptive(light: 0xFFFFFF, dark: 0x102326)
    static let focusBackground = Color(hex: 0x14333B)
    static let focusInputBackground = Color(hex: 0xFFFFFF)
    static let focusInputText = Color(hex: 0x1A2528)
    static let focusInputPlaceholder = Color(hex: 0x65747A)
    static let progressGold = adaptive(light: 0x87660D, dark: 0xE3C86A)
    static let warmPaper = canvas
    static let sepia = Color(hex: 0xF4ECD8)
    static let sepiaInk = Color(hex: 0x5B4636)
    static let muted = adaptive(light: 0x65747A, dark: 0xAEBABD)
    static let danger = adaptive(light: 0xA34545, dark: 0xFFB4AB)

    private static func adaptive(light: UInt, dark: UInt) -> Color {
        Color(uiColor: UIColor { traits in
            UIColor(hex: traits.userInterfaceStyle == .dark ? dark : light)
        })
    }
}

enum YomoraSpacing {
    static let xs: CGFloat = 4
    static let sm: CGFloat = 8
    static let md: CGFloat = 16
    static let lg: CGFloat = 24
    static let xl: CGFloat = 32
}

enum YomoraRadius {
    static let button: CGFloat = 12
    static let card: CGFloat = 20
    static let cover: CGFloat = 12
}

extension Font {
    static let yomoraTitle = Font.system(.largeTitle, design: .serif, weight: .semibold)
    static let yomoraHeading = Font.system(.title2, design: .serif, weight: .semibold)
    static let yomoraBody = Font.system(.body, design: .rounded)
    static let yomoraEditorial = Font.system(.body, design: .serif)
}

extension Color {
    init(hex: UInt, alpha: Double = 1) {
        self.init(.sRGB,
                  red: Double((hex >> 16) & 0xff) / 255,
                  green: Double((hex >> 8) & 0xff) / 255,
                  blue: Double(hex & 0xff) / 255,
                  opacity: alpha)
    }
}

private extension UIColor {
    convenience init(hex: UInt) {
        self.init(
            red: CGFloat((hex >> 16) & 0xff) / 255,
            green: CGFloat((hex >> 8) & 0xff) / 255,
            blue: CGFloat(hex & 0xff) / 255,
            alpha: 1
        )
    }
}

enum AppTheme: String, CaseIterable, Identifiable {
    case system, light, dark, sepia
    var id: String { rawValue }
    var title: String {
        switch self { case .system: "Automático"; case .light: "Claro"; case .dark: "Escuro"; case .sepia: "Sépia" }
    }
    var colorScheme: ColorScheme? {
        switch self { case .system: nil; case .light, .sepia: .light; case .dark: .dark }
    }
}

enum AccentChoice: String, CaseIterable, Identifiable {
    case teal, gold, forest, terracotta
    var id: String { rawValue }
    var title: String { rawValue.capitalized }
    var color: Color {
        switch self {
        case .teal: YomoraColor.sereneTeal
        case .gold: YomoraColor.progressGold
        case .forest: Color(hex: 0x436850)
        case .terracotta: Color(hex: 0xA9654E)
        }
    }
}
