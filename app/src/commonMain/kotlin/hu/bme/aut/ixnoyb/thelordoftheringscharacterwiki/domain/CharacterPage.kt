package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

internal data class CharacterPage(
    val characters: List<Character>,
    val isNextPageExist: Boolean,
)