package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

data class CharacterPage(
    val characters: List<Character>,
    val isNextPageExist: Boolean,
)