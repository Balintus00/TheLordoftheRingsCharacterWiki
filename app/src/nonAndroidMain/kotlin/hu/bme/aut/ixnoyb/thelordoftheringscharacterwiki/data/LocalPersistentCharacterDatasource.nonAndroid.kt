package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.CharacterQueries
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
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character as DomainCharacter

class SqlDelightLocalPersistentCharacterDatasource(
    private val characterQueries: CharacterQueries,
) : LocalCharacterDatasource {

    override fun getAll(): Flow<List<DomainCharacter>> = characterQueries
        .selectAll { id: String,
                     birth: String,
                     death: String,
                     gender: String,
                     hair: String,
                     height: String,
                     name: String,
                     race: String,
                     realm: String,
                     spouse: String ->
            DomainCharacter(
                Id(id),
                Birth(birth),
                Death(death),
                Gender(gender),
                Hair(hair),
                Height((height)),
                Name(name),
                Race(race),
                Realm(realm),
                Spouse(spouse),
            )
        }
        .asFlow()
        .mapToList(Dispatchers.IO)

    override fun getById(id: Id): Flow<DomainCharacter?> {
        return characterQueries.selectById(
            id = id.value,
            mapper = { resultId: String,
                       birth: String,
                       death: String,
                       gender: String,
                       hair: String,
                       height: String,
                       name: String,
                       race: String,
                       realm: String,
                       spouse: String ->
                DomainCharacter(
                    Id(resultId),
                    Birth(birth),
                    Death(death),
                    Gender(gender),
                    Hair(hair),
                    Height((height)),
                    Name(name),
                    Race(race),
                    Realm(realm),
                    Spouse(spouse),
                )
            },
        )
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
    }

    override suspend fun insertAll(vararg characters: DomainCharacter) =
        withContext(Dispatchers.IO) {
            characters.forEach {
                characterQueries.insertCharacter(
                    Character(
                        id = it.id.value,
                        birth = it.birth.value,
                        death = it.death.value,
                        gender = it.gender.value,
                        hair = it.hair.value,
                        height = it.height.value,
                        name = it.name.value,
                        race = it.race.value,
                        realm = it.realm.value,
                        spouse = it.spouse.value,
                    )
                )
            }
        }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        characterQueries.clear()
    }
}