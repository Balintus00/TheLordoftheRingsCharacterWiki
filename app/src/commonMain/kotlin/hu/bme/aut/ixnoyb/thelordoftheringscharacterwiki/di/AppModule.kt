package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.di

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.SqlDelightDatabase
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.KtorRemoteCharacterDataSource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.SqlDelightLocalCharacterDataSource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.InMemoryLocalCharacterDataSource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.getKtorEngine
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.CharacterRepository
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.DefaultCharacterRepository
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDataSource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.RemoteCharacterDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal const val NAME_PERSISTENT_CHARACTER_DATA_SOURCE = "Persistent"
internal const val NAME_TRANSIENT_CHARACTER_DATA_SOURCE = "Transient"

internal val appModule = module {
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

    single<RemoteCharacterDataSource> {
        KtorRemoteCharacterDataSource(httpClient = get())
    }

    single {
        SqlDelightDatabase(driver = get())
    }

    single {
        val database: SqlDelightDatabase = get()
        database.characterQueries
    }

    single<LocalCharacterDataSource>(named(NAME_PERSISTENT_CHARACTER_DATA_SOURCE)) {
        SqlDelightLocalCharacterDataSource(characterQueries = get())
    }

    single<LocalCharacterDataSource>(named(NAME_TRANSIENT_CHARACTER_DATA_SOURCE)) {
        InMemoryLocalCharacterDataSource()
    }

    single<CharacterRepository> {
        DefaultCharacterRepository(
            localPersistentCharacterDataSource = get<LocalCharacterDataSource>(
                named(NAME_PERSISTENT_CHARACTER_DATA_SOURCE)
            ),
            localTransientCharacterDataSource =  get<LocalCharacterDataSource>(
                named(NAME_TRANSIENT_CHARACTER_DATA_SOURCE)
            ),
            remoteCharacterDataSource = get(),
        )
    }
}