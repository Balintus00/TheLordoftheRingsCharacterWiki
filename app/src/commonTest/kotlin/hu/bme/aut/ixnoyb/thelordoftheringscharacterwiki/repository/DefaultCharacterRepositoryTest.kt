package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository

import app.cash.turbine.test
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterPage
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageNumber
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSize
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSpecification
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.DefaultCharacterRepository.Companion.EXCEPTION_MESSAGE_OPERATION_FAILED
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.createFakeLocalCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.createFakeRemoteCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.testutility.DEFAULT_DISPATCHER_THREAD_NAME_PREFIX
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.testutility.WAITING_FOR_TEST_BACKGROUND_TASK_DELAY
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.testutility.createDomainCharacter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.testutility.getThreadName
import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class, DelicateCoroutinesApi::class)
@Suppress("unused", "LargeClass")
class DefaultCharacterRepositoryTest : BehaviorSpec({

    Context("getAll should retrieve the data from the proper data source") {

        Given("unspecified character name filter") {
            val filter: CharacterNameFilter? = null

            And("localPersistentDataSource returns Flow of List of Characters") {
                val characters = listOf(createDomainCharacter())
                val localPersistentDataSource = createFakeLocalCharacterDatasource(
                    getAllAction = { flowOf(characters) },
                )

                And("a repository") {
                    val repository = DefaultCharacterRepository(
                        defaultDispatcher = UnconfinedTestDispatcher(),
                        localPersistentCharacterDataSource = localPersistentDataSource,
                        localTransientCharacterDataSource = createFakeLocalCharacterDatasource(),
                        remoteCharacterDataSource = createFakeRemoteCharacterDatasource(),
                    )

                    When("getAll is called") {
                        val result = repository.getAll(filter)

                        Then(
                            "result flow should contain characters " +
                                    "returned by persistent data source"
                        ) {
                            result.test {
                                awaitItem() shouldBe characters
                                awaitComplete()
                            }
                        }
                    }
                }
            }
        }

        Given("a character name filter") {
            val filter = CharacterNameFilter("CharacterFilter")

            And("localTransientDataSource returns a Flow of List of characters") {
                val characters = listOf(createDomainCharacter())
                val transientDataSource = createFakeLocalCharacterDatasource(
                    getAllAction = { flowOf(characters) },
                )

                And("a repository") {
                    val repository = DefaultCharacterRepository(
                        defaultDispatcher = UnconfinedTestDispatcher(),
                        localPersistentCharacterDataSource = createFakeLocalCharacterDatasource(),
                        localTransientCharacterDataSource = transientDataSource,
                        remoteCharacterDataSource = createFakeRemoteCharacterDatasource(),
                    )

                    When("getAll is called") {
                        val result = repository.getAll(filter)

                        Then(
                            "result flow should contain characters " +
                                    "returned by transient data source"
                        ) {
                            result.test {
                                awaitItem() shouldBe characters
                                awaitComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    Context("getByID returns the proper Character based on the state of local data sources") {

        Given("a character ID") {
            val characterID = ID("1")

            And("localTransientCharacterDataSource contains a character with such ID") {
                val transientCharacter = createDomainCharacter(id = characterID)
                val transientDataSource = createFakeLocalCharacterDatasource(
                    getByIdAction = { id ->
                        if (characterID == id) {
                            flowOf(transientCharacter)
                        } else {
                            flowOf(null)
                        }
                    }
                )

                And("localPersistentCharacterDataSource contains a character with such ID") {
                    val persistentDataSource = createFakeLocalCharacterDatasource(
                        getByIdAction = { id ->
                            if (characterID == id) {
                                flowOf(createDomainCharacter(id = characterID))
                            } else {
                                flowOf(null)
                            }
                        }
                    )

                    And("a repository") {
                        val repository = DefaultCharacterRepository(
                            defaultDispatcher = UnconfinedTestDispatcher(),
                            localPersistentCharacterDataSource = persistentDataSource,
                            localTransientCharacterDataSource = transientDataSource,
                            remoteCharacterDataSource = createFakeRemoteCharacterDatasource(),
                        )

                        When("getByID is called") {
                            val result = repository.getByID(characterID)

                            Then(
                                "result should be character retrieved from transient data source"
                            ) {
                                result.test {
                                    awaitItem() shouldBe transientCharacter
                                    awaitComplete()
                                }
                            }
                        }
                    }
                }

                And(
                    "localPersistentCharacterDataSource doesn't contain a character with such ID"
                ) {
                    val persistentDataSource = createFakeLocalCharacterDatasource(
                        getByIdAction = { _ -> flowOf(null) },
                    )

                    And("a repository") {
                        val repository = DefaultCharacterRepository(
                            defaultDispatcher = UnconfinedTestDispatcher(),
                            localPersistentCharacterDataSource = persistentDataSource,
                            localTransientCharacterDataSource = transientDataSource,
                            remoteCharacterDataSource = createFakeRemoteCharacterDatasource(),
                        )

                        When("getByID is called") {
                            val result = repository.getByID(characterID)

                            Then(
                                "result should be character retrieved from transient data source"
                            ) {
                                result.test {
                                    awaitItem() shouldBe transientCharacter
                                    awaitComplete()
                                }
                            }
                        }
                    }
                }
            }

            And(
                "localTransientCharacterDataSource doesn't contain a character with such ID"
            ) {
                val transientDataSource = createFakeLocalCharacterDatasource(
                    getByIdAction = { _ -> flowOf(null) }
                )

                And("localPersistentCharacterDataSource contains a character with such ID") {
                    val persistentCharacter = createDomainCharacter(id = characterID)
                    val persistentDataSource = createFakeLocalCharacterDatasource(
                        getByIdAction = { id ->
                            if (characterID == id) {
                                flowOf(persistentCharacter)
                            } else {
                                flowOf(null)
                            }
                        }
                    )

                    And("a repository") {
                        val repository = DefaultCharacterRepository(
                            defaultDispatcher = UnconfinedTestDispatcher(),
                            localPersistentCharacterDataSource = persistentDataSource,
                            localTransientCharacterDataSource = transientDataSource,
                            remoteCharacterDataSource = createFakeRemoteCharacterDatasource(),
                        )

                        When("getByID is called") {
                            val result = repository.getByID(characterID)

                            Then(
                                "result should be character retrieved from transient data source"
                            ) {
                                result.test {
                                    awaitItem() shouldBe persistentCharacter
                                    awaitComplete()
                                }
                            }
                        }
                    }
                }

                And(
                    "localPersistentCharacterDataSource doesn't contain a character with such ID"
                ) {
                    val persistentDataSource = createFakeLocalCharacterDatasource(
                        getByIdAction = { _ -> flowOf(null) },
                    )

                    And("a repository") {
                        val repository = DefaultCharacterRepository(
                            defaultDispatcher = UnconfinedTestDispatcher(),
                            localPersistentCharacterDataSource = persistentDataSource,
                            localTransientCharacterDataSource = transientDataSource,
                            remoteCharacterDataSource = createFakeRemoteCharacterDatasource(),
                        )

                        When("getByID is called") {
                            val result = repository.getByID(characterID)

                            Then("result character should be null") {
                                result.test {
                                    awaitItem() shouldBe null
                                    awaitComplete()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Context("loadByID properly returns data from remote data source") {

        Given("a character ID") {
            val characterID = ID("1")

            And("remote data source returns character with ID") {
                val character = createDomainCharacter(id = characterID)

                val remoteDataSource = createFakeRemoteCharacterDatasource(
                    getByIDAction = { id ->
                        if (id == characterID) {
                            character
                        } else {
                            throw NotImplementedError()
                        }
                    }
                )

                And("a repository") {
                    val repository = DefaultCharacterRepository(
                        defaultDispatcher = UnconfinedTestDispatcher(),
                        localPersistentCharacterDataSource = createFakeLocalCharacterDatasource(),
                        localTransientCharacterDataSource = createFakeLocalCharacterDatasource(),
                        remoteCharacterDataSource = remoteDataSource,
                    )

                    When("loadByID is called") {
                        val result = repository.loadByID(characterID)

                        Then("result should be character loaded by remote data source") {
                            result shouldBe character
                        }
                    }
                }
            }

            And("loadByID calling context has custom CoroutineName") {
                val coroutineName = "testCoroutineName"

                And("remote data source returns character and captures coroutine name") {
                    var remoteCoroutineName: CoroutineName? = null

                    val remoteDataSource = createFakeRemoteCharacterDatasource(
                        getByIDAction = { _ ->
                            remoteCoroutineName = currentCoroutineContext()[CoroutineName]

                            createDomainCharacter()
                        }
                    )

                    And("a repository") {
                        val repository = DefaultCharacterRepository(
                            defaultDispatcher = UnconfinedTestDispatcher(),
                            localPersistentCharacterDataSource =
                            createFakeLocalCharacterDatasource(),
                            localTransientCharacterDataSource =
                            createFakeLocalCharacterDatasource(),
                            remoteCharacterDataSource = remoteDataSource,
                        )

                        When("loadByID is called") {
                            withContext(CoroutineName(coroutineName)) {
                                repository.loadByID(characterID)
                            }

                            Then(
                                "captured coroutine name should be the same " +
                                        "that was passed when calling loadByID"
                            ) {
                                remoteCoroutineName?.name shouldBe coroutineName
                            }
                        }
                    }
                }
            }

            And(
                "remote data source returns character and captures dispatcher and thread name"
            ) {
                var capturedDispatcher: CoroutineDispatcher? = null
                var capturedThreadName: String? = null

                val remoteDataSource = createFakeRemoteCharacterDatasource(
                    getByIDAction = { _ ->
                        capturedDispatcher = coroutineContext[CoroutineDispatcher]
                        capturedThreadName = getThreadName()

                        createDomainCharacter()
                    }
                )

                And("a repository with the default DefaultDispatcher parameter") {
                    val repository = DefaultCharacterRepository(
                        localPersistentCharacterDataSource =
                        createFakeLocalCharacterDatasource(),
                        localTransientCharacterDataSource =
                        createFakeLocalCharacterDatasource(),
                        remoteCharacterDataSource = remoteDataSource,
                    )

                    When("loadByID is called on a distinct thread") {
                        val parentDispatcher = newSingleThreadContext(
                            "distinctThreadPoolContext"
                        )
                        var parentThreadName: String?

                        try {
                            withContext(parentDispatcher) {
                                parentThreadName = getThreadName()
                                repository.loadByID(characterID)
                            }
                        } catch (t: Throwable) {
                            fail("Unexpected exception\n${t.stackTraceToString()}")
                        } finally {
                            parentDispatcher.close()
                        }

                        Then("captured dispatcher should be different from parent's") {
                            capturedDispatcher shouldNotBeSameInstanceAs parentDispatcher
                        }

                        Then("captured thread name should be different from parent's") {
                            capturedThreadName shouldNotBe parentThreadName
                        }

                        Then("captured thread name should have default dispatcher prefix") {
                            capturedThreadName should {
                                it?.startsWith(DEFAULT_DISPATCHER_THREAD_NAME_PREFIX) ?: false
                            }
                        }
                    }
                }

                And("a repository with the a custom Dispatcher") {
                    val injectedDispatcherThreadName = "injectedDispatcherThreadName"
                    val injectedDispatcher = newSingleThreadContext(injectedDispatcherThreadName)

                    try {
                        val repository = DefaultCharacterRepository(
                            defaultDispatcher = injectedDispatcher,
                            localPersistentCharacterDataSource =
                            createFakeLocalCharacterDatasource(),
                            localTransientCharacterDataSource =
                            createFakeLocalCharacterDatasource(),
                            remoteCharacterDataSource = remoteDataSource,
                        )

                        When("loadByID is called on a distinct thread") {
                            val parentDispatcher = newSingleThreadContext(
                                "distinctThreadPoolContext"
                            )
                            var parentThreadName: String?

                            try {
                                withContext(parentDispatcher) {
                                    parentThreadName = getThreadName()
                                    repository.loadByID(characterID)
                                }
                            } catch (t: Throwable) {
                                fail("Unexpected exception\n${t.stackTraceToString()}")
                            } finally {
                                parentDispatcher.close()
                            }

                            Then("captured dispatcher should be different from parent's") {
                                capturedDispatcher shouldNotBeSameInstanceAs parentDispatcher
                            }

                            Then("captured thread name should be different from parent's") {
                                capturedThreadName shouldNotBe parentThreadName
                            }

                            Then(
                                "captured thread name should start with " +
                                        "injected dispatcher's Thread's name"
                            ) {
                                capturedThreadName should {
                                    it?.startsWith(injectedDispatcherThreadName) ?: false
                                }
                            }
                        }
                    } catch (t: Throwable) {
                        fail("Unexpected exception\n${t.stackTraceToString()}")
                    } finally {
                        injectedDispatcher.close()
                    }
                }
            }

            And(
                "remote data source throws cancellation exception after character with ID " +
                        "is requested"
            ) {
                val exception = CancellationException()

                val remoteDataSource = createFakeRemoteCharacterDatasource(
                    getByIDAction = { _ -> throw exception }
                )

                And("a repository") {
                    val repository = DefaultCharacterRepository(
                        defaultDispatcher = UnconfinedTestDispatcher(),
                        localPersistentCharacterDataSource = createFakeLocalCharacterDatasource(),
                        localTransientCharacterDataSource = createFakeLocalCharacterDatasource(),
                        remoteCharacterDataSource = remoteDataSource,
                    )

                    When("loadByID is called") {

                        Then("cancellation exception should be rethrown") {
                            try {
                                repository.loadByID(characterID)

                                fail("No exception was thrown!")
                            } catch (t: Throwable) {
                                t shouldBeSameInstanceAs exception
                            }
                        }
                    }
                }
            }

            And("remote data source throws exception after character with ID is requested") {
                val exception = IllegalArgumentException()

                val remoteDataSource = createFakeRemoteCharacterDatasource(
                    getByIDAction = { _ -> throw exception }
                )

                And("a repository") {
                    val repository = DefaultCharacterRepository(
                        defaultDispatcher = UnconfinedTestDispatcher(),
                        localPersistentCharacterDataSource = createFakeLocalCharacterDatasource(),
                        localTransientCharacterDataSource = createFakeLocalCharacterDatasource(),
                        remoteCharacterDataSource = remoteDataSource,
                    )

                    When("loadByID is called") {

                        Then("proper exception should be thrown") {
                            try {
                                repository.loadByID(characterID)

                                fail("No exception was thrown!")
                            } catch (t: Throwable) {
                                t shouldBe IllegalStateException(
                                    EXCEPTION_MESSAGE_OPERATION_FAILED
                                )
                            }
                        }
                    }
                }
            }

            And("remote data source loads for a very long time characters by ID") {
                var childJob: Job? = null

                val remoteDataSource = createFakeRemoteCharacterDatasource(
                    getByIDAction = {
                        childJob = currentCoroutineContext().job
                        delay(Long.MAX_VALUE)

                        fail("Should be impossible to reach this operation")
                    }
                )

                And("a repository") {
                    val repository = DefaultCharacterRepository(
                        localPersistentCharacterDataSource = createFakeLocalCharacterDatasource(),
                        localTransientCharacterDataSource = createFakeLocalCharacterDatasource(),
                        remoteCharacterDataSource = remoteDataSource,
                    )

                    When("loadByID is called and its container Job is cancelled") {
                        val parentJob = CoroutineScope(EmptyCoroutineContext).launch {
                            repository.loadByID(characterID)
                        }

                        while (childJob == null) {
                            delay(WAITING_FOR_TEST_BACKGROUND_TASK_DELAY)
                        }

                        parentJob.cancelAndJoin()

                        Then("remote data source's operation should be cancelled") {
                            childJob?.isCancelled shouldBe true
                        }
                    }
                }
            }
        }
    }

    Context("loadPage propagates exceptions and properly uses data sources") {

        Given("a page specification of first page") {
            val page = PageSpecification(
                number = PageNumber.FIRST_NUMBER,
                size = PageSize(10),
            )

            And("a name filter") {
                val filter = CharacterNameFilter("CharacterFilter")

                And("remote data source returns characters") {
                    val characters = listOf(createDomainCharacter())

                    And("next page exists") {
                        val remoteDataSource = createFakeRemoteCharacterDatasource(
                            getPageAction = { filter, page ->
                                if (filter == filter && page == page) {
                                    CharacterPage(
                                        characters = characters,
                                        isNextPageExist = true,
                                    )
                                } else {
                                    throw NotImplementedError()
                                }
                            }
                        )

                        And("transient data source can be cleared") {
                            And(
                                "transient data source can be used to insert the characters"
                            ) {
                                val transientDataSource = createFakeLocalCharacterDatasource(
                                    clearAction = { /*No-op*/ },
                                    insertAllAction = { charactersToInsert ->
                                        if (charactersToInsert.toList() == characters) {
                                            // No-op
                                        } else {
                                            throw NotImplementedError()
                                        }
                                    }
                                )

                                And("a repository") {
                                    val repository = DefaultCharacterRepository(
                                        defaultDispatcher = UnconfinedTestDispatcher(),
                                        localPersistentCharacterDataSource =
                                        createFakeLocalCharacterDatasource(),
                                        localTransientCharacterDataSource = transientDataSource,
                                        remoteCharacterDataSource = remoteDataSource,
                                    )

                                    When("loadPage is called") {
                                        val result = repository.loadPage(filter, page)

                                        Then(
                                            "result should contain characters " +
                                                    "loaded by remote data source"
                                        ) {
                                            result.first shouldBe characters
                                        }

                                        Then(
                                            "result should contain information " +
                                                    "that next page exists"
                                        ) {
                                            result.second shouldBe true
                                        }
                                    }
                                }
                            }

                            And(
                                "transient data source throws CancellationException while inserting"
                            ) {
                                val cancellationException = CancellationException()

                                val transientDataSource = createFakeLocalCharacterDatasource(
                                    clearAction = { /*No-op*/ },
                                    insertAllAction = { _ -> throw cancellationException }
                                )

                                And("a repository") {
                                    val repository = DefaultCharacterRepository(
                                        defaultDispatcher = UnconfinedTestDispatcher(),
                                        localPersistentCharacterDataSource =
                                        createFakeLocalCharacterDatasource(),
                                        localTransientCharacterDataSource = transientDataSource,
                                        remoteCharacterDataSource = remoteDataSource,
                                    )

                                    When("loadPage is called") {

                                        Then("cancellation exception should be rethrown") {
                                            try {
                                                repository.loadPage(filter, page)
                                                fail("Exception expected")
                                            } catch (t: Throwable) {
                                                t shouldBeSameInstanceAs cancellationException
                                            }
                                        }
                                    }
                                }
                            }

                            And("transient data source fails to insert the characters") {
                                val insertException = IllegalStateException("Insert error")

                                val transientDataSource = createFakeLocalCharacterDatasource(
                                    clearAction = { /*No-op*/ },
                                    insertAllAction = { _ -> throw insertException }
                                )

                                And("a repository") {
                                    val repository = DefaultCharacterRepository(
                                        defaultDispatcher = UnconfinedTestDispatcher(),
                                        localPersistentCharacterDataSource =
                                        createFakeLocalCharacterDatasource(),
                                        localTransientCharacterDataSource = transientDataSource,
                                        remoteCharacterDataSource = remoteDataSource,
                                    )

                                    When("loadPage is called") {

                                        Then("proper exception should be thrown") {
                                            try {
                                                repository.loadPage(filter, page)
                                                fail("Exception expected")
                                            } catch (t: Throwable) {
                                                t shouldBe IllegalStateException(
                                                    EXCEPTION_MESSAGE_OPERATION_FAILED
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        And(
                            "transient data source throws CancellationException while clearing"
                        ) {
                            val cancellationException = CancellationException()
                            val transientDataSource = createFakeLocalCharacterDatasource(
                                clearAction = { throw cancellationException }
                            )

                            And("a repository") {
                                val repository = DefaultCharacterRepository(
                                    defaultDispatcher = UnconfinedTestDispatcher(),
                                    localPersistentCharacterDataSource =
                                    createFakeLocalCharacterDatasource(),
                                    localTransientCharacterDataSource = transientDataSource,
                                    remoteCharacterDataSource = remoteDataSource,
                                )

                                When("loadPage is called") {

                                    Then("cancellation exception should be rethrown") {
                                        try {
                                            repository.loadPage(filter, page)
                                            fail("Exception expected")
                                        } catch (t: Throwable) {
                                            t shouldBeSameInstanceAs cancellationException
                                        }
                                    }
                                }
                            }
                        }

                        And("transient data source fails to clear itself") {
                            val clearException = IllegalStateException("Clear error")
                            val transientDataSource = createFakeLocalCharacterDatasource(
                                clearAction = { throw clearException }
                            )

                            And("a repository") {
                                val repository = DefaultCharacterRepository(
                                    defaultDispatcher = UnconfinedTestDispatcher(),
                                    localPersistentCharacterDataSource =
                                    createFakeLocalCharacterDatasource(),
                                    localTransientCharacterDataSource = transientDataSource,
                                    remoteCharacterDataSource = remoteDataSource,
                                )

                                When("loadPage is called") {

                                    Then("proper exception should be thrown") {
                                        try {
                                            repository.loadPage(filter, page)
                                            fail("Exception expected")
                                        } catch (t: Throwable) {
                                            t shouldBe IllegalStateException(
                                                EXCEPTION_MESSAGE_OPERATION_FAILED
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    And("next page doesn't exist") {
                        val remoteDataSource = createFakeRemoteCharacterDatasource(
                            getPageAction = { filter, page ->
                                if (filter == filter && page == page) {
                                    CharacterPage(
                                        characters = characters,
                                        isNextPageExist = false,
                                    )
                                } else {
                                    throw NotImplementedError()
                                }
                            }
                        )

                        And("transient data source can be cleared") {

                            And(
                                "transient data source can be used to insert the characters"
                            ) {
                                val transientDataSource = createFakeLocalCharacterDatasource(
                                    clearAction = { /*No-op*/ },
                                    insertAllAction = { charactersToInsert ->
                                        if (charactersToInsert.toList() == characters) {
                                            // No-op
                                        } else {
                                            throw NotImplementedError()
                                        }
                                    }
                                )

                                And("a repository") {
                                    val repository = DefaultCharacterRepository(
                                        defaultDispatcher = UnconfinedTestDispatcher(),
                                        localPersistentCharacterDataSource =
                                        createFakeLocalCharacterDatasource(),
                                        localTransientCharacterDataSource = transientDataSource,
                                        remoteCharacterDataSource = remoteDataSource,
                                    )

                                    When("loadPage is called") {
                                        val result = repository.loadPage(filter, page)

                                        Then(
                                            "result should contain characters loaded " +
                                                    "by remote data source"
                                        ) {
                                            result.first shouldBe characters
                                        }

                                        Then(
                                            "result should contain information " +
                                                    "that next page doesn't exist"
                                        ) {
                                            result.second shouldBe false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                And("remote data source throws CancellationException") {
                    val cancellationException = CancellationException()
                    val remoteDataSource = createFakeRemoteCharacterDatasource(
                        getPageAction = { _, _ -> throw cancellationException }
                    )

                    And("a repository") {
                        val repository = DefaultCharacterRepository(
                            defaultDispatcher = UnconfinedTestDispatcher(),
                            localPersistentCharacterDataSource =
                            createFakeLocalCharacterDatasource(),
                            localTransientCharacterDataSource =
                            createFakeLocalCharacterDatasource(),
                            remoteCharacterDataSource = remoteDataSource,
                        )

                        When("loadPage is called") {

                            Then("cancellation exception should be rethrown") {
                                try {
                                    repository.loadPage(filter, page)
                                    fail("Exception expected")
                                } catch (t: Throwable) {
                                    t shouldBeSameInstanceAs cancellationException
                                }
                            }
                        }
                    }
                }

                And("remote data source throws exception") {
                    val remoteLoadingException = IllegalStateException("Remote loading failure")
                    val remoteDataSource = createFakeRemoteCharacterDatasource(
                        getPageAction = { _, _ -> throw remoteLoadingException }
                    )

                    And("a repository") {
                        val repository = DefaultCharacterRepository(
                            defaultDispatcher = UnconfinedTestDispatcher(),
                            localPersistentCharacterDataSource =
                            createFakeLocalCharacterDatasource(),
                            localTransientCharacterDataSource =
                            createFakeLocalCharacterDatasource(),
                            remoteCharacterDataSource = remoteDataSource,
                        )

                        When("loadPage is called") {

                            Then("proper exception should be thrown") {
                                try {
                                    repository.loadPage(filter, page)
                                    fail("Exception expected")
                                } catch (t: Throwable) {
                                    t shouldBe IllegalStateException(
                                        EXCEPTION_MESSAGE_OPERATION_FAILED
                                    )
                                }
                            }
                        }
                    }
                }
            }

            And("no name filter") {
                val filter: CharacterNameFilter? = null

                And("remote data source returns page") {
                    val characters = listOf(createDomainCharacter())
                    val remoteDataSource = createFakeRemoteCharacterDatasource(
                        getPageAction = { filter, page ->
                            if (filter == filter && page == page) {
                                CharacterPage(
                                    characters = characters,
                                    isNextPageExist = true,
                                )
                            } else {
                                throw NotImplementedError()
                            }
                        }
                    )

                    And("persistent data source can successfully insert characters") {

                        And("persistent data source can be cleared") {
                            val persistentCharacterDataSource = createFakeLocalCharacterDatasource(
                                clearAction = {
                                    // No-op
                                },
                                insertAllAction = { charactersToInsert ->
                                    if (charactersToInsert.toList() == characters) {
                                        // No-op
                                    } else {
                                        throw NotImplementedError()
                                    }
                                }
                            )

                            And("transient data source can be cleared") {
                                val transientDataSource = createFakeLocalCharacterDatasource(
                                    clearAction = { /*No-op*/ }
                                )

                                And("a repository") {
                                    val repository = DefaultCharacterRepository(
                                        defaultDispatcher = UnconfinedTestDispatcher(),
                                        localPersistentCharacterDataSource =
                                        persistentCharacterDataSource,
                                        localTransientCharacterDataSource = transientDataSource,
                                        remoteCharacterDataSource = remoteDataSource,
                                    )

                                    When("loadPage is called") {
                                        val result = repository.loadPage(filter, page)

                                        Then(
                                            "result should be characters from remote data source"
                                        ) {
                                            result.first shouldBe characters
                                        }

                                        Then(
                                            "result should contain information next page exists"
                                        ) {
                                            result.second shouldBe true
                                        }
                                    }
                                }
                            }

                            And("transient data source fails to clear") {
                                val clearException = IllegalStateException(
                                    "Transient clear exception"
                                )
                                val transientDataSource = createFakeLocalCharacterDatasource(
                                    clearAction = { throw clearException }
                                )

                                And("a repository") {
                                    val repository = DefaultCharacterRepository(
                                        defaultDispatcher = UnconfinedTestDispatcher(),
                                        localPersistentCharacterDataSource =
                                        persistentCharacterDataSource,
                                        localTransientCharacterDataSource = transientDataSource,
                                        remoteCharacterDataSource = remoteDataSource,
                                    )

                                    When("loadPage is called") {

                                        Then("proper exception should be thrown") {
                                            try {
                                                repository.loadPage(filter, page)
                                                fail("Exception expected")
                                            } catch (t: Throwable) {
                                                t shouldBe IllegalStateException(
                                                    EXCEPTION_MESSAGE_OPERATION_FAILED
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        And("persistent data source fails to clear") {
                            val clearException = IllegalStateException("Clear error")
                            val persistentCharacterDataSource = createFakeLocalCharacterDatasource(
                                clearAction = {
                                    throw clearException
                                },
                                insertAllAction = { charactersToInsert ->
                                    if (charactersToInsert.toList() == characters) {
                                        // No-op
                                    } else {
                                        throw NotImplementedError()
                                    }
                                }
                            )

                            And("a repository") {
                                val repository = DefaultCharacterRepository(
                                    defaultDispatcher = UnconfinedTestDispatcher(),
                                    localPersistentCharacterDataSource =
                                    persistentCharacterDataSource,
                                    localTransientCharacterDataSource =
                                    createFakeLocalCharacterDatasource(),
                                    remoteCharacterDataSource = remoteDataSource,
                                )

                                When("loadPage is called") {

                                    Then("proper exception should be thrown") {
                                        try {
                                            repository.loadPage(filter, page)
                                            fail("Exception expected")
                                        } catch (t: Throwable) {
                                            t shouldBe IllegalStateException(
                                                EXCEPTION_MESSAGE_OPERATION_FAILED
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Given("a page specification of second page") {
            val page = PageSpecification(
                number = PageNumber.FIRST_NUMBER.getNextNumber(),
                size = PageSize(10),
            )

            And("a name filter") {
                val filter = CharacterNameFilter("Filter")

                And("remote data source returns page") {
                    val characters = listOf(createDomainCharacter())
                    val remoteDataSource = createFakeRemoteCharacterDatasource(
                        getPageAction = { characterNameFilter, pageSpecification ->
                            if (characterNameFilter == filter && pageSpecification == page) {
                                CharacterPage(
                                    characters = characters,
                                    isNextPageExist = false,
                                )
                            } else {
                                throw NotImplementedError()
                            }
                        }
                    )

                    And("transient data source can insert characters") {
                        val transientDataSource = createFakeLocalCharacterDatasource(
                            insertAllAction = { charactersToInsert ->
                                if (charactersToInsert.toList() == characters) {
                                    // No-op
                                } else {
                                    throw NotImplementedError()
                                }
                            }
                        )

                        And("a repository") {
                            val repository = DefaultCharacterRepository(
                                defaultDispatcher = UnconfinedTestDispatcher(),
                                localPersistentCharacterDataSource =
                                createFakeLocalCharacterDatasource(),
                                localTransientCharacterDataSource = transientDataSource,
                                remoteCharacterDataSource = remoteDataSource,
                            )

                            When("loadPage is called") {
                                val result = repository.loadPage(filter, page)

                                Then("result should be characters from remote data source") {
                                    result.first shouldBe characters
                                }

                                Then(
                                    "result should contain information next page doesn't exist"
                                ) {
                                    result.second shouldBe false
                                }
                            }
                        }
                    }
                }
            }

            And("no name filter") {
                val filter: CharacterNameFilter? = null

                And("remote data source returns page") {
                    val characters = listOf(createDomainCharacter())
                    val remoteDataSource = createFakeRemoteCharacterDatasource(
                        getPageAction = { characterNameFilter, pageSpecification ->
                            if (characterNameFilter == filter && pageSpecification == page) {
                                CharacterPage(
                                    characters = characters,
                                    isNextPageExist = false,
                                )
                            } else {
                                throw NotImplementedError()
                            }
                        }
                    )

                    And("persistent data source can insert characters") {
                        val persistentDataSource = createFakeLocalCharacterDatasource(
                            insertAllAction = { charactersToInsert ->
                                if (charactersToInsert.toList() == characters) {
                                    // No-op
                                } else {
                                    throw NotImplementedError()
                                }
                            }
                        )

                        And("a repository") {
                            val repository = DefaultCharacterRepository(
                                defaultDispatcher = UnconfinedTestDispatcher(),
                                localPersistentCharacterDataSource = persistentDataSource,
                                localTransientCharacterDataSource =
                                createFakeLocalCharacterDatasource(),
                                remoteCharacterDataSource = remoteDataSource,
                            )

                            When("loadPage is called") {
                                val result = repository.loadPage(filter, page)

                                Then("result should be characters from remote data source") {
                                    result.first shouldBe characters
                                }

                                Then(
                                    "result should contain information next page doesn't exist"
                                ) {
                                    result.second shouldBe false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
})