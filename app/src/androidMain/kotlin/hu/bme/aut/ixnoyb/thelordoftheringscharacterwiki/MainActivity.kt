package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.UserInterface
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.DefaultRootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootComponent = DefaultRootComponent(defaultComponentContext())

        setContent {
            UserInterface(component = rootComponent)
        }
    }
}