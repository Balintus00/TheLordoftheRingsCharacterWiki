package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal fun createFakeLocalCharacterDatasource(
    getAllAction: () -> Flow<List<Character>> = { flowOf() },
    getByIdAction: (id: Id) -> Flow<Character?> = { flowOf() },
    insertAllAction: (characters: Array<out Character>) -> Unit = {},
    clearAction: () -> Unit = {},
): LocalCharacterDatasource = object : LocalCharacterDatasource {

    override fun getAll(): Flow<List<Character>> = getAllAction()

    override fun getById(id: Id): Flow<Character?> = getByIdAction(id)

    override suspend fun insertAll(vararg characters: Character) {
        insertAllAction(characters)
    }

    override suspend fun clear() {
        clearAction()
    }
}