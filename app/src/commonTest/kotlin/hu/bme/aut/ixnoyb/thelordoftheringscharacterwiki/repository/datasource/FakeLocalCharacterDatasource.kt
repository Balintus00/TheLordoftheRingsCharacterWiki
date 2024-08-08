package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import kotlinx.coroutines.flow.Flow

internal fun createFakeLocalCharacterDatasource(
    getAllAction: () -> Flow<List<Character>> = { throw NotImplementedError() },
    getByIdAction: (id: ID) -> Flow<Character?> = { throw NotImplementedError() },
    insertAllAction: suspend (characters: List<Character>) -> Unit = {
        throw NotImplementedError()
    },
    clearAction: suspend () -> Unit = { throw NotImplementedError() },
): LocalCharacterDataSource = object : LocalCharacterDataSource {

    override fun getAll(): Flow<List<Character>> = getAllAction()

    override fun getById(id: ID): Flow<Character?> = getByIdAction(id)

    override suspend fun insertAll(characters: List<Character>) {
        insertAllAction(characters)
    }

    override suspend fun clear() {
        clearAction()
    }
}