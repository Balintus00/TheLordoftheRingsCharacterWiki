package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

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