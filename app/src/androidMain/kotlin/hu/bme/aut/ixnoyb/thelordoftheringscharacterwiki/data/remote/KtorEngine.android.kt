package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.remote

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual fun getKtorEngine(): HttpClientEngineFactory<*> = OkHttp