package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Birth
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Death
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Gender
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Hair
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Height
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Name
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Race
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Realm
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Spouse
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character as DomainCharacter

internal class RoomLocalPersistentCharacterDatasource(
    private val characterDao: CharacterDao,
) : LocalCharacterDatasource {

    override fun getAll(): Flow<List<DomainCharacter>> = characterDao.getAll()
        .flowOn(Dispatchers.IO)
        .map { characterList -> characterList.map { it.toDomainCharacter() } }

    private fun Character.toDomainCharacter(): DomainCharacter = DomainCharacter(
        id = Id(id),
        birth = Birth(birth),
        death = Death(death),
        gender = Gender(gender),
        hair = Hair(hair),
        height = Height(height),
        name = Name(name),
        race = Race(race),
        realm = Realm(realm),
        spouse = Spouse(spouse),
    )

    override fun getById(id: Id): Flow<DomainCharacter?> = characterDao.getById(id.value)
        .flowOn(Dispatchers.IO)
        .map { it?.toDomainCharacter() }

    override suspend fun insertAll(vararg characters: DomainCharacter) {
        characterDao.insert(*characters.map { it.toCharacter() }.toTypedArray())
    }

    private fun DomainCharacter.toCharacter(): Character = Character(
        id = id.value,
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

    override suspend fun clear() {
        characterDao.clear()
    }
}