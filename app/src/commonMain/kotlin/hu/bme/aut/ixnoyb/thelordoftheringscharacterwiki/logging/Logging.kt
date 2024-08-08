package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.logging

private const val EMPTY_THROWABLE_MESSAGE = "Empty throwable message"

internal val Throwable.messageOrDefault: String
    get() = message ?: EMPTY_THROWABLE_MESSAGE