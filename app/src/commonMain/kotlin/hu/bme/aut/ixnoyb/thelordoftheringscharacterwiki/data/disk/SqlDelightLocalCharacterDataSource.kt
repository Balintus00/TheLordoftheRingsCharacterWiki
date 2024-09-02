package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.disk

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.touchlab.kermit.Logger
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.CharacterQueries
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Birth
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character as DomainCharacter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Death
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Gender
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Hair
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Height
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Name
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Race
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Realm
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Spouse
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.logging.messageOrDefault
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.DefaultCharacterRepository
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.DataSourceFailure
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.DataSourceQueryResponse
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.DataSourceQuerySuccess
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.DataSourceCommandResponse
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.DataSourceCommandSuccess
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal class SqlDelightLocalCharacterDataSource(
    private val characterQueries: CharacterQueries,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LocalCharacterDataSource {

    private val log = Logger.withTag(DefaultCharacterRepository::class.simpleName!!)

    override fun getAll(): Flow<DataSourceQueryResponse<List<DomainCharacter>>> =
        characterQueries
            .selectAll()
            .asFlow()
            .mapToList(ioDispatcher)
            .map<List<Character>, DataSourceQueryResponse<List<DomainCharacter>>> { characterList ->
                DataSourceQuerySuccess(characterList.map { it.toDomainCharacter() })
            }
            .catch { cause -> handleSqlDelightThrowableOrRethrow(cause) }

    private fun Character.toDomainCharacter(): DomainCharacter = DomainCharacter(
        id = ID(id),
        birth = Birth(birth),
        death = Death(death),
        gender = Gender(gender),
        hair = Hair(hair),
        height = Height(height),
        name = Name(name),
        race = Race(race),
        realm = Realm(realm),
        spouse = Spouse(spouse),
    )

    private suspend fun <T>
            FlowCollector<DataSourceQueryResponse<T>>.handleSqlDelightThrowableOrRethrow(
        cause: Throwable,
    ) {
        log.w(cause.messageOrDefault, cause)

        if (cause is SqlDelightThrowable) {
            emit(DataSourceFailure(cause))
        } else {
            throw cause
        }
    }

    override fun getById(id: ID): Flow<DataSourceQueryResponse<DomainCharacter?>> =
        characterQueries
            .selectById(id.value)
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map<Character?, DataSourceQueryResponse<DomainCharacter?>> {
                DataSourceQuerySuccess(it?.toDomainCharacter())
            }
            .catch { cause -> handleSqlDelightThrowableOrRethrow(cause) }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun insertAll(
        characters: List<DomainCharacter>,
    ): DataSourceCommandResponse = executeCommandWithErrorHandling {
        withContext(ioDispatcher.limitedParallelism(SINGLE_THREAD_PARALLELISM)) {
            with(characterQueries) {
                transaction(noEnclosing = true) {
                    characters.map { it.toCharacter() }.forEach { insertCharacter(it) }
                }
            }
        }
    }

    @OptIn(ExperimentalContracts::class)
    private inline fun executeCommandWithErrorHandling(
        command: () -> Unit,
    ): DataSourceCommandResponse {
        contract { callsInPlace(command, InvocationKind.EXACTLY_ONCE) }

        return try {
            command()

            DataSourceCommandSuccess
        } catch (sqlDelightThrowable: SqlDelightThrowable) {
            log.w(sqlDelightThrowable.messageOrDefault, sqlDelightThrowable)

            DataSourceFailure(sqlDelightThrowable)
        } catch (ise: IllegalStateException) {
            log.w(ise.messageOrDefault, ise)

            DataSourceFailure(ise)
        }
    }

    private fun DomainCharacter.toCharacter(): Character = Character(
        id = id.value,
        birth = birth.value,
        death = death.value,
        gender = gender.value,
        hair = hair.value,
        height = height.value,
        name = name.value,
        race = race.value,
        realm = realm.value,
        spouse = spouse.value,
    )

    override suspend fun clear(): DataSourceCommandResponse = executeCommandWithErrorHandling {
        withContext(ioDispatcher) { characterQueries.clear() }
    }

    companion object {

        private const val SINGLE_THREAD_PARALLELISM = 1
    }
}