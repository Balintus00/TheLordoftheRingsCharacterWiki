package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListWithDetailsComponent

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun CharacterListWithDetailsScreen(
    component: CharacterListWithDetailsComponent,
    modifier: Modifier = Modifier,
) {
    if (calculateWindowSizeClass().widthSizeClass != WindowWidthSizeClass.Expanded) {
        component.navigateToCharacterListOrDetails()
    }

    // TODO
    Button(onClick = { component.selectCharacter("1") }) {
        Text("Set character ID")
    }
}