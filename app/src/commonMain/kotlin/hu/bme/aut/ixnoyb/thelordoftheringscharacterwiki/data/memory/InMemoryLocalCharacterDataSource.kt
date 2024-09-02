package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.memory

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.DataSourceFailure
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.DataSourceQueryResponse
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.DataSourceQuerySuccess
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.DataSourceCommandResponse
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.DataSourceCommandSuccess
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal class InMemoryLocalCharacterDataSource : LocalCharacterDataSource {

    private val storedCharacters = MutableStateFlow(emptyList<Character>())

    override fun getAll(): Flow<DataSourceQueryResponse<List<Character>>> =
        storedCharacters.map { DataSourceQuerySuccess(it) }

    override fun getById(id: ID): Flow<DataSourceQueryResponse<Character>> =
        storedCharacters
            .map { characterList ->
                characterList.firstOrNull { it.id == id }?.let {
                    DataSourceQuerySuccess(it)
                } ?: DataSourceFailure()
            }

    override suspend fun insertAll(characters: List<Character>): DataSourceCommandResponse =
        DataSourceCommandSuccess.also {
            storedCharacters.update { storedCharacters.value + characters }
        }

    override suspend fun clear(): DataSourceCommandResponse = DataSourceCommandSuccess.also {
        storedCharacters.update { emptyList() }
    }
}