import SwiftUI

struct AppColors {
    let background: Color
    let surface: Color
    let onBackground: Color
    let onSurface: Color
    let onSurfaceVariant: Color

    static let accent = Color(hex: "00D4AA")
    static let positive = Color(hex: "00D4AA")
    static let negative = Color(hex: "FF4444")

    static let dark = AppColors(
        background: Color(hex: "0D0D0D"),
        surface: Color(hex: "1A1A1A"),
        onBackground: .white,
        onSurface: .white,
        onSurfaceVariant: .gray
    )

    static let light = AppColors(
        background: Color(hex: "F5F5F5"),
        surface: .white,
        onBackground: Color(hex: "1A1A1A"),
        onSurface: Color(hex: "1A1A1A"),
        onSurfaceVariant: Color(hex: "666666")
    )
}

struct ThemeKey: EnvironmentKey {
    static let defaultValue = AppColors.dark
}

extension EnvironmentValues {
    var appColors: AppColors {
        get { self[ThemeKey.self] }
        set { self[ThemeKey.self] = newValue }
    }
}

struct ThemeModifier: ViewModifier {
    @Environment(\.colorScheme) var colorScheme

    func body(content: Content) -> some View {
        content.environment(\.appColors, colorScheme == .dark ? AppColors.dark : AppColors.light)
    }
}

extension View {
    func withAppTheme() -> some View {
        modifier(ThemeModifier())
    }
}
