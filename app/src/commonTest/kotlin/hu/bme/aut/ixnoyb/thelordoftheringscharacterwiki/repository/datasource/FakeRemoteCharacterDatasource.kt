package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterPage
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSpecification

internal fun createFakeRemoteCharacterDatasource(
    getByIdAction: (id: Id) -> Character = { throw NotImplementedError() },
    getPageAction: (CharacterNameFilter?, PageSpecification) -> CharacterPage = { _, _ ->
        throw NotImplementedError()
    },
): RemoteCharacterDatasource = object : RemoteCharacterDatasource {

    override suspend fun getById(id: Id): Character = getByIdAction(id)

    override suspend fun getPage(
        nameFilter: CharacterNameFilter?,
        page: PageSpecification
    ): CharacterPage = getPageAction(nameFilter, page)
}