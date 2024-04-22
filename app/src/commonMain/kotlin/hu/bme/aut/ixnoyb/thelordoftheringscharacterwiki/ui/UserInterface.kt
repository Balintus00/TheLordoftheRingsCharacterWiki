package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme.TheLordOfTheRingsCharacterWikiTheme
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.RootComponent

@Composable
internal fun UserInterface(component: RootComponent) {
    TheLordOfTheRingsCharacterWikiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RootScaffold(component = component, modifier = Modifier.fillMaxSize())
        }
    }
}