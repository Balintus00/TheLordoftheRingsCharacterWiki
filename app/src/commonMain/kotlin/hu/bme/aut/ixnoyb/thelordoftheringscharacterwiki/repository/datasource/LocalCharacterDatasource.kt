package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import kotlinx.coroutines.flow.Flow

internal interface LocalCharacterDatasource {

    fun getAll(): Flow<List<Character>>

    fun getById(id: Id): Flow<Character?>

    suspend fun insertAll(vararg characters: Character)

    suspend fun clear()
}