package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal class InMemoryLocalCharacterDataSource : LocalCharacterDataSource {

    private val storedCharacters = MutableStateFlow(emptyList<Character>())

    override fun getAll(): Flow<List<Character>> = storedCharacters.asStateFlow()

    override fun getById(id: ID): Flow<Character?> =
        storedCharacters.map { characterList -> characterList.firstOrNull { it.id == id } }

    override suspend fun insertAll(vararg characters: Character) {
        storedCharacters.update { storedCharacters.value + characters }
    }

    override suspend fun clear() {
        storedCharacters.update { emptyList() }
    }
}