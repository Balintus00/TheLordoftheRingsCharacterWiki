package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

import kotlin.jvm.JvmInline

@JvmInline
value class CharacterNameFilter(val value: String) {
    init {
        require(value.isBlank().not())
        require(value.length in MINIMUM_LENGTH..MAXIMUM_LENGTH)
    }

    companion object {
        const val MINIMUM_LENGTH = 1
        const val MAXIMUM_LENGTH = 32
    }
}