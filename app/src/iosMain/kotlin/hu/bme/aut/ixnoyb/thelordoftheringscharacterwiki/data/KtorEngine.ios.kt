package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal actual fun getKtorEngine(): HttpClientEngineFactory<*> = Darwin