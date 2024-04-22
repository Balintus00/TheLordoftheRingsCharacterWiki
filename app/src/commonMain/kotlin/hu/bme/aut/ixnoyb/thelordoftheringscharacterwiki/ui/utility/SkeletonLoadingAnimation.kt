package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.placeholder
import com.eygraber.compose.placeholder.shimmer

@Composable
fun Modifier.placeholder(isVisible: Boolean): Modifier = this.placeholder(
    visible = isVisible,
    color = MaterialTheme.colorScheme.outlineVariant,
    highlight = PlaceholderHighlight.shimmer(
        highlightColor = MaterialTheme.colorScheme.background,
    ),
)