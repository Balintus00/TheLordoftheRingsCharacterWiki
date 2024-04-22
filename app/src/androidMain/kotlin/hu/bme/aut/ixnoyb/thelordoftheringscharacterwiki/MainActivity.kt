package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.UserInterface
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.DefaultRootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val rootComponent = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            storeFactory = LoggingStoreFactory(DefaultStoreFactory())
        )

        setContent {
            UserInterface(component = rootComponent)
        }
    }
}