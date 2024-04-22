package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository

import co.touchlab.kermit.Logger
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSpecification
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.RemoteCharacterDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class DefaultCharacterRepository(
    private val localPersistentCharacterDatasource: LocalCharacterDatasource,
    private val localTransientCharacterDatasource: LocalCharacterDatasource,
    private val remoteCharacterDatasource: RemoteCharacterDatasource,
) : CharacterRepository {

    private val log = Logger.withTag(DefaultCharacterRepository::class.simpleName!!)

    override fun getAll(nameFilter: CharacterNameFilter?): Flow<List<Character>> =
        if (nameFilter != null) {
            localTransientCharacterDatasource
        } else {
            localPersistentCharacterDatasource
        }.getAll()

    override fun getById(id: Id): Flow<Character?> =
        localTransientCharacterDatasource.getById(id).combine(
            localPersistentCharacterDatasource.getById(id)
        ) { transientCharacter, persistentCharacter ->
            when {
                transientCharacter != null -> transientCharacter
                persistentCharacter != null -> persistentCharacter
                else -> null
            }
        }

    override suspend fun loadById(id: Id): Character = remoteCharacterDatasource.getById(id)

    override suspend fun loadPage(
        nameFilter: CharacterNameFilter?,
        page: PageSpecification,
    ): Pair<List<Character>, Boolean> {
        val destinationLocalDatasource = if (nameFilter != null) {
            localTransientCharacterDatasource
        } else {
            localPersistentCharacterDatasource
        }

        return try {
            val nextPage = remoteCharacterDatasource.getPage(
                nameFilter = nameFilter,
                page = page,
            )

            when {
                page.number.isFirst && nameFilter == null -> {
                    setOf(
                        localPersistentCharacterDatasource,
                        localTransientCharacterDatasource,
                    ).forEach {
                        it.clear()
                    }
                }
                page.number.isFirst -> {
                    destinationLocalDatasource.clear()
                }
            }

            destinationLocalDatasource.insertAll(*nextPage.characters.toTypedArray())

            nextPage.run { characters to isNextPageExist  }
        } catch (t: Throwable) {
            log.i(t) { LOG_MESSAGE_FAILED_PAGE_LOADING }
            throw t
        }
    }

    companion object {
        private const val LOG_MESSAGE_FAILED_PAGE_LOADING = "Failed to load page!"
    }
}