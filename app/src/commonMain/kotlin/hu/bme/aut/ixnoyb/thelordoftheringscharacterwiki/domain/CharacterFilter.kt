package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

data class CharacterFilter(
    val name: NameFilter,
)

@JvmInline
value class NameFilter(val value: String) {
    init {
        require(value.length in MIN_LENGTH..MAX_LENGTH)
    }

    companion object {
        const val MIN_LENGTH = 1
        const val MAX_LENGTH = 32
    }
}