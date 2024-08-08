package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository

import co.touchlab.kermit.Logger
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSpecification
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.logging.messageOrDefault
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDataSource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.RemoteCharacterDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

internal class DefaultCharacterRepository(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val localPersistentCharacterDataSource: LocalCharacterDataSource,
    private val localTransientCharacterDataSource: LocalCharacterDataSource,
    private val remoteCharacterDataSource: RemoteCharacterDataSource,
) : CharacterRepository {

    private val log = Logger.withTag(DefaultCharacterRepository::class.simpleName!!)

    override fun getAll(nameFilter: CharacterNameFilter?): Flow<List<Character>> =
        if (nameFilter != null) {
            localTransientCharacterDataSource
        } else {
            localPersistentCharacterDataSource
        }.getAll().flowOn(defaultDispatcher)

    override fun getByID(id: ID): Flow<Character?> =
        localTransientCharacterDataSource.getById(id).combine(
            localPersistentCharacterDataSource.getById(id)
        ) { transientCharacter, persistentCharacter ->
            when {
                transientCharacter != null -> transientCharacter
                persistentCharacter != null -> persistentCharacter
                else -> null
            }
        }.flowOn(defaultDispatcher)

    override suspend fun loadByID(id: ID): Character = withContext(defaultDispatcher) {
        try {
            remoteCharacterDataSource.getById(id)
        } catch (ce: CancellationException) {
            handleCancellationException(ce)
        } catch (t: Throwable) {
            log.w(t) { t.messageOrDefault }

            throw IllegalStateException(EXCEPTION_MESSAGE_OPERATION_FAILED, t)
        }
    }

    private fun handleCancellationException(exception: CancellationException): Nothing {
        log.i(exception) { LOG_MESSAGE_COROUTINE_CANCELLED }

        throw exception
    }

    override suspend fun loadPage(
        nameFilter: CharacterNameFilter?,
        page: PageSpecification,
    ): Pair<List<Character>, Boolean> = withContext(defaultDispatcher) {
        val destinationLocalDatasource = if (nameFilter != null) {
            localTransientCharacterDataSource
        } else {
            localPersistentCharacterDataSource
        }

        try {
            val nextPage = remoteCharacterDataSource.getPage(
                nameFilter = nameFilter,
                page = page,
            )

            when {
                page.number.isFirst && nameFilter == null -> {
                    setOf(
                        localPersistentCharacterDataSource,
                        localTransientCharacterDataSource,
                    ).forEach {
                        it.clear()
                    }
                }

                page.number.isFirst -> {
                    destinationLocalDatasource.clear()
                }
            }

            destinationLocalDatasource.insertAll(nextPage.characters)

            nextPage.run { characters to isNextPageExist }
        } catch (ce: CancellationException) {
            handleCancellationException(ce)
        }
        catch (t: Throwable) {
            log.i(t) { LOG_MESSAGE_FAILED_PAGE_LOADING }

            throw IllegalStateException(EXCEPTION_MESSAGE_OPERATION_FAILED, t)
        }
    }

    companion object {
        const val EXCEPTION_MESSAGE_OPERATION_FAILED = "Operation failed!"

        private const val LOG_MESSAGE_FAILED_PAGE_LOADING = "Failed to load page!"
        private const val LOG_MESSAGE_COROUTINE_CANCELLED = "Coroutine was cancelled!"
    }
}