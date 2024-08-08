package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.testutility

import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.Worker

@OptIn(ObsoleteWorkersApi::class)
internal actual fun getThreadName(): String = Worker.current.name