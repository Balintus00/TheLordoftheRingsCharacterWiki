package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun getStandardSpace(windowWidthSizeClass: WindowWidthSizeClass): Dp =
    if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
        16.dp
    } else {
        24.dp
    }