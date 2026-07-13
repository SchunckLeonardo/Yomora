import SwiftUI

enum YomoraColor {
    static let primary = Color(hex: 0x14333B)
    static let sereneTeal = Color(hex: 0x2C4A52)
    static let progressGold = Color(hex: 0xD4AF37)
    static let warmPaper = Color(hex: 0xFAF7F0)
    static let sepia = Color(hex: 0xF4ECD8)
    static let sepiaInk = Color(hex: 0x5B4636)
    static let muted = Color(hex: 0x728086)
    static let danger = Color(hex: 0xA34545)
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

enum AppTheme: String, CaseIterable, Identifiable {
    case system, light, dark, sepia
    var id: String { rawValue }
    var title: String {
        switch self { case .system: "Automático"; case .light: "Claro"; case .dark: "Escuro"; case .sepia: "Sépia" }
    }
    var colorScheme: ColorScheme? {
        switch self { case .system, .sepia: nil; case .light: .light; case .dark: .dark }
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

