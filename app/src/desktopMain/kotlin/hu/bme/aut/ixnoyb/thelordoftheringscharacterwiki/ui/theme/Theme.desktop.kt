package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
internal actual fun getColorScheme(): ColorScheme = if (isSystemInDarkTheme()) {
    darkScheme
} else {
    lightScheme
}

@Composable
internal actual fun ApplyPlatformSpecificThemeSettings(colorScheme: ColorScheme) {
    // No-op
}

@Composable
actual fun ApplyPlatformSpecificCompositionLocalSettings(content: @Composable () -> Unit) {
    val scrollBarTheme = defaultScrollbarStyle().copy(
        unhoverColor = colorScheme.outline,
        hoverColor = colorScheme.secondary,
    )

    CompositionLocalProvider(LocalScrollbarStyle provides scrollBarTheme) {
        content()
    }
}