package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformSpecificListScrollbar(listState: LazyListState, modifier: Modifier) {
    // No-op
}

@Composable
actual fun PlatformSpecificListScrollbar(
    scrollState: ScrollState,
    modifier: Modifier
) {
    // No-op
}