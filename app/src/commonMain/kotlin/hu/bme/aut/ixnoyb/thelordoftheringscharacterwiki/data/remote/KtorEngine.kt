package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.remote

import io.ktor.client.engine.HttpClientEngineFactory

internal expect fun getKtorEngine(): HttpClientEngineFactory<*>