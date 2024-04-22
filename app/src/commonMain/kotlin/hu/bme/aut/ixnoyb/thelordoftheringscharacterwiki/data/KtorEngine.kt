package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import io.ktor.client.engine.HttpClientEngineFactory

internal expect fun getKtorEngine(): HttpClientEngineFactory<*>