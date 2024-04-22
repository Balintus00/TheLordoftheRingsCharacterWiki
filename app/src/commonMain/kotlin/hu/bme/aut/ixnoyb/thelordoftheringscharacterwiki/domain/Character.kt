package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

import kotlin.jvm.JvmInline

data class Character(
    val id: Id,
    val birth: Birth,
    val death: Death,
    val gender: Gender,
    val hair: Hair,
    val height: Height,
    val name: Name,
    val race: Race,
    val realm: Realm,
    val spouse: Spouse,
)

@JvmInline
value class Id(val value: String)

@JvmInline
value class Height(val value: String)

@JvmInline
value class Race(val value: String)

@JvmInline
value class Gender(val value: String)

@JvmInline
value class Birth(val value: String)

@JvmInline
value class Spouse(val value: String)

@JvmInline
value class Death(val value: String)

@JvmInline
value class Realm(val value: String)

@JvmInline
value class Hair(val value: String)

@JvmInline
value class Name(val value: String)