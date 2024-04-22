package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import io.ktor.client.engine.HttpClientEngineFactory

expect fun getKtorEngine(): HttpClientEngineFactory<*>