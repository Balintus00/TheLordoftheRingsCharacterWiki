package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.model

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character as DomainCharacter

data class Character(
    val birth: String,
    val death: String,
    val gender: String,
    val hair: String,
    val height: String,
    val name: String,
    val race: String,
    val realm: String,
    val spouse: String,
)

fun DomainCharacter.toUiModel(): Character = Character(
    birth = birth.value,
    death = death.value,
    gender = gender.value,
    hair = hair.value,
    height = height.value,
    name = name.value,
    race = race.value,
    realm = realm.value,
    spouse = spouse.value,
)