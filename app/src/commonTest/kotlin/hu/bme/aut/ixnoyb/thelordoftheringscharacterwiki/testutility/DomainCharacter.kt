package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.testutility

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Birth
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Death
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Gender
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Hair
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Height
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Name
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Race
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Realm
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Spouse

internal fun createDomainCharacter(
    id: ID = ID(""),
    birth: Birth = Birth(""),
    death: Death = Death(""),
    gender: Gender = Gender(""),
    hair: Hair = Hair(""),
    height: Height = Height(""),
    name: Name = Name(""),
    race: Race = Race(""),
    realm: Realm = Realm(""),
    spouse: Spouse = Spouse(""),
) = Character(
    id = id,
    birth = birth,
    death = death,
    gender = gender,
    hair = hair,
    height = height,
    name = name,
    race = race,
    realm = realm,
    spouse = spouse,
)