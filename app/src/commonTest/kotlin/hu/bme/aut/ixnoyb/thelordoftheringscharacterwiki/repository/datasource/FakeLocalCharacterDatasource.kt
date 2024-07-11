package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import kotlinx.coroutines.flow.Flow

internal inline fun createFakeLocalCharacterDatasource(
    crossinline getAllAction: () -> Flow<List<Character>> = { throw NotImplementedError() },
    crossinline getByIdAction: (id: Id) -> Flow<Character?> = { throw NotImplementedError() },
    crossinline insertAllAction: (characters: Array<out Character>) -> Unit = {
        throw NotImplementedError()
    },
    crossinline clearAction: () -> Unit = { throw NotImplementedError() },
): LocalCharacterDataSource = object : LocalCharacterDataSource {

    override fun getAll(): Flow<List<Character>> = getAllAction()

    override fun getById(id: Id): Flow<Character?> = getByIdAction(id)

    override suspend fun insertAll(vararg characters: Character) {
        insertAllAction(characters)
    }

    override suspend fun clear() {
        clearAction()
    }
}