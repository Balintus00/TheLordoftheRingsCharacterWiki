package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterPage
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSpecification

internal fun createFakeRemoteCharacterDatasource(
     getByIDAction: suspend (id: ID) -> Character = { throw NotImplementedError() },
     getPageAction: suspend (CharacterNameFilter?, PageSpecification) -> CharacterPage = { _, _ ->
        throw NotImplementedError()
    },
): RemoteCharacterDataSource = object : RemoteCharacterDataSource {

    override suspend fun getById(id: ID): Character = getByIDAction(id)

    override suspend fun getPage(
        nameFilter: CharacterNameFilter?,
        page: PageSpecification,
    ): CharacterPage = getPageAction(nameFilter, page)
}