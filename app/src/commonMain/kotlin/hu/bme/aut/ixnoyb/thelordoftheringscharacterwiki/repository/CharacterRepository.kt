package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSpecification
import kotlinx.coroutines.flow.Flow

internal interface CharacterRepository {

    fun getAll(nameFilter: CharacterNameFilter?): Flow<List<Character>>

    fun getByID(id: ID): Flow<Character?>

    suspend fun loadByID(id: ID): Character

    suspend fun loadPage(
        nameFilter: CharacterNameFilter?,
        page: PageSpecification,
    ): Pair<List<Character>, Boolean>
}