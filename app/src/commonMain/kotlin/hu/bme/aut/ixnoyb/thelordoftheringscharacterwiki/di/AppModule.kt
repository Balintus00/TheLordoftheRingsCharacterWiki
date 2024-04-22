package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.di

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.KtorRemoteCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.LocalTransientCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.getKtorEngine
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.CharacterRepository
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.DefaultCharacterRepository
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.RemoteCharacterDatasource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val NAME_PERSISTENT_CHARACTER_DATA_SOURCE = "Persistent"
const val NAME_TRANSIENT_CHARACTER_DATA_SOURCE = "Transient"

val appModule = module {
    includes(platformSpecificModule)

    single {
        HttpClient(getKtorEngine()) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                    }
                )
            }
            install(Logging) {
                logger = object : Logger {

                    val KTOR_LOG_TAG = "HTTP Client"

                    override fun log(message: String) {
                        co.touchlab.kermit.Logger.v(
                            tag = KTOR_LOG_TAG,
                            messageString = message,
                        )
                    }
                }

                level = LogLevel.ALL
            }
        }
    }

    single<RemoteCharacterDatasource> {
        KtorRemoteCharacterDatasource(httpClient = get())
    }

    single<LocalCharacterDatasource>(named(NAME_TRANSIENT_CHARACTER_DATA_SOURCE)) {
        LocalTransientCharacterDatasource()
    }

    single<CharacterRepository> {
        DefaultCharacterRepository(
            localPersistentCharacterDatasource = get<LocalCharacterDatasource>(
                named(NAME_PERSISTENT_CHARACTER_DATA_SOURCE)
            ),
            localTransientCharacterDatasource =  get<LocalCharacterDatasource>(
                named(NAME_TRANSIENT_CHARACTER_DATA_SOURCE)
            ),
            remoteCharacterDatasource = get(),
        )
    }
}