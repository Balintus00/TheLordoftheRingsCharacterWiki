package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.createFakeLocalCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.createFakeRemoteCharacterDatasource
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultCharacterRepositoryTest {

    @Test
    fun `Given null NameFilter When getAll is called Then persistent datasource must be used`() {
        // Given null NameFilter
        val nameFilter: CharacterNameFilter? = null
        val persistentDatasourceGetAllResult = flowOf<List<Character>>()

        val persistentDatasource = createFakeLocalCharacterDatasource(
            getAllAction = { persistentDatasourceGetAllResult },
        )
        val transientDatasource = createFakeLocalCharacterDatasource()
        val remoteDatasource = createFakeRemoteCharacterDatasource()

        val repository = DefaultCharacterRepository(
            localPersistentCharacterDatasource = persistentDatasource,
            localTransientCharacterDatasource = transientDatasource,
            remoteCharacterDatasource = remoteDatasource,
        )

        // When getAll is called
        val result = repository.getAll(nameFilter = nameFilter)

        // Then persistent datasource must be used
        assertEquals(persistentDatasourceGetAllResult, result)
    }

    @Test
    fun `Given non-null NameFilter When getAll is called Then transient datasource must be used`() {
        // Given non-null NameFilter
        val nameFilter = CharacterNameFilter("Legolas")
        val transientDatasourceGetAllResult = flowOf<List<Character>>()

        val persistentDatasource = createFakeLocalCharacterDatasource()
        val transientDatasource = createFakeLocalCharacterDatasource(
            getAllAction = { transientDatasourceGetAllResult },
        )
        val remoteDatasource = createFakeRemoteCharacterDatasource()

        val repository = DefaultCharacterRepository(
            localPersistentCharacterDatasource = persistentDatasource,
            localTransientCharacterDatasource = transientDatasource,
            remoteCharacterDatasource = remoteDatasource,
        )

        // When getAll is called
        val result = repository.getAll(nameFilter = nameFilter)

        // Then transient datasource must be used
        assertEquals(transientDatasourceGetAllResult, result)
    }
}