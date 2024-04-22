package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun getColorScheme(): ColorScheme = if (isSystemInDarkTheme()) {
    darkScheme
} else {
    lightScheme
}

@Composable
actual fun ApplyPlatformSpecificThemeSettings(colorScheme: ColorScheme) {
    // No-op
}