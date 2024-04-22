package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.model

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character as DomainCharacter

data class CharacterListItem(
    val id: String,
    val name: String
)

fun DomainCharacter.toListItem(): CharacterListItem = CharacterListItem(
    id = id.value,
    name = name.value,
)