import androidx.compose.ui.window.ComposeUIViewController
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.UserInterface
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.RootComponent

@Suppress("unused", "FunctionName")
fun TheLordOfTheRingsCharacterWikiViewController(component: RootComponent) =
    ComposeUIViewController { UserInterface(component) }