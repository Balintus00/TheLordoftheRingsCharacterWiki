import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.di.appModule
import org.koin.core.context.startKoin

@Suppress("unused")
fun initKoin() {
    startKoin {
        logger(
            KermitKoinLogger(Logger.withTag("koin"))
        )
        modules(appModule)
    }
}