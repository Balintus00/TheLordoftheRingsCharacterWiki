package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun PlatformSpecificListScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
)

@Composable
internal expect fun PlatformSpecificListScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
)