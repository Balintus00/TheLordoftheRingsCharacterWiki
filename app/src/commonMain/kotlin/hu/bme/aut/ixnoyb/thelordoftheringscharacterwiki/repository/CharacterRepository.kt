package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSpecification
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getAll(nameFilter: CharacterNameFilter?): Flow<List<Character>>

    fun getById(id: Id): Flow<Character?>

    suspend fun loadById(id: Id): Character

    suspend fun loadPage(
        nameFilter: CharacterNameFilter?,
        page: PageSpecification,
    ): Pair<List<Character>, Boolean>
}