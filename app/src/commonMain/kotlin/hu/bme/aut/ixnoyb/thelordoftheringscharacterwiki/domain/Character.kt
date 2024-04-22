package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

import kotlin.jvm.JvmInline

internal data class Character(
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
internal value class Id(val value: String)

@JvmInline
internal value class Height(val value: String)

@JvmInline
internal value class Race(val value: String)

@JvmInline
internal value class Gender(val value: String)

@JvmInline
internal value class Birth(val value: String)

@JvmInline
internal value class Spouse(val value: String)

@JvmInline
internal value class Death(val value: String)

@JvmInline
internal value class Realm(val value: String)

@JvmInline
internal value class Hair(val value: String)

@JvmInline
internal value class Name(val value: String)