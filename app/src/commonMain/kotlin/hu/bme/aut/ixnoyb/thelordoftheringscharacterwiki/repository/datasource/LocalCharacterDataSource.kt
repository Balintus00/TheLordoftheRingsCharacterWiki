package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import kotlinx.coroutines.flow.Flow

internal interface LocalCharacterDataSource {

    fun getAll(): Flow<List<Character>>

    fun getById(id: ID): Flow<Character?>

    suspend fun insertAll(characters: List<Character>)

    suspend fun clear()
}