package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme

import androidx.compose.runtime.Composable

@Composable
internal actual fun ApplyPlatformSpecificCompositionLocalSettings(content: @Composable () -> Unit) {
    content()
}