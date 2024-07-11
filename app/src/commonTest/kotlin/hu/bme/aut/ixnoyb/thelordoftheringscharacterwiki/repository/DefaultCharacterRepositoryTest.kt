package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository

import app.cash.turbine.test
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterPage
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageNumber
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSize
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSpecification
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.createFakeLocalCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.createFakeRemoteCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.testutility.createDomainCharacter
import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("unused")
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

                        Then("result flow should contain characters returned by persistent data source") {
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

                        Then("result flow should contain characters returned by transient data source") {
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
            val characterID = Id("1")

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

                            Then("result should be character retrieved from transient data source") {
                                result.test {
                                    awaitItem() shouldBe transientCharacter
                                    awaitComplete()
                                }
                            }
                        }
                    }
                }

                And("localPersistentCharacterDataSource doesn't contain a character with such ID") {
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

                            Then("result should be character retrieved from transient data source") {
                                result.test {
                                    awaitItem() shouldBe transientCharacter
                                    awaitComplete()
                                }
                            }
                        }
                    }
                }
            }

            And("localTransientCharacterDataSource doesn't contain a character with such ID") {
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

                            Then("result should be character retrieved from transient data source") {
                                result.test {
                                    awaitItem() shouldBe persistentCharacter
                                    awaitComplete()
                                }
                            }
                        }
                    }
                }

                And("localPersistentCharacterDataSource doesn't contain a character with such ID") {
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
            val characterID = Id("1")

            And("remote data source returns character with ID") {
                val character = createDomainCharacter(id = characterID)

                val remoteDataSource = createFakeRemoteCharacterDatasource(
                    getByIdAction = { id ->
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

            And("remote data source throws exception after character with ID is requested") {
                val exception = IllegalArgumentException()

                val remoteDataSource = createFakeRemoteCharacterDatasource(
                    getByIdAction = { _ -> throw exception }
                )

                And("a repository") {
                    val repository = DefaultCharacterRepository(
                        defaultDispatcher = UnconfinedTestDispatcher(),
                        localPersistentCharacterDataSource = createFakeLocalCharacterDatasource(),
                        localTransientCharacterDataSource = createFakeLocalCharacterDatasource(),
                        remoteCharacterDataSource = remoteDataSource,
                    )

                    When("loadByID is called") {

                        Then("exception thrown by remote data source should be propagated") {
                            try {
                                repository.loadByID(characterID)

                                fail("No exception was thrown!")
                            } catch (t: Throwable) {
                                t shouldBe exception
                            }
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

                                        Then("result should contain characters loaded by remote data source") {
                                            result.first shouldBe characters
                                        }

                                        Then("result should contain information that next page exists") {
                                            result.second shouldBe true
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

                                        Then("insertion exception should be thrown") {
                                            try {
                                                repository.loadPage(filter, page)
                                                fail("Exception expected")
                                            } catch (t: Throwable) {
                                                t shouldBe insertException
                                            }
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

                                    Then("clearing exception should be thrown") {
                                        try {
                                            repository.loadPage(filter, page)
                                            fail("Exception expected")
                                        } catch (t: Throwable) {
                                            t shouldBe clearException
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

                                        Then("result should contain characters loaded by remote data source") {
                                            result.first shouldBe characters
                                        }

                                        Then("result should contain information that next page doesn't exist") {
                                            result.second shouldBe false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                And("remote data source throws exception") {
                    val remoteLoadingException = IllegalStateException("Remote loading failure")
                    val remoteDataSource = createFakeRemoteCharacterDatasource(
                        getPageAction = { _, _ -> throw  remoteLoadingException }
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

                            Then("remote loading exception should be thrown") {
                                try {
                                    repository.loadPage(filter, page)
                                    fail("Exception expected")
                                } catch (t: Throwable) {
                                    t shouldBe remoteLoadingException
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

                                        Then("result should be characters from remote data source") {
                                            result.first shouldBe characters
                                        }

                                        Then("result should contain information next page exists") {
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

                                        Then("clearing exception should be thrown") {
                                            try {
                                                repository.loadPage(filter, page)
                                                fail("Exception expected")
                                            } catch (t: Throwable) {
                                                t shouldBe clearException
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

                                    Then("clearing exception should be thrown") {
                                        try {
                                            repository.loadPage(filter, page)
                                            fail("Exception expected")
                                        } catch (t: Throwable) {
                                            t shouldBe clearException
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
                                localPersistentCharacterDataSource = createFakeLocalCharacterDatasource(),
                                localTransientCharacterDataSource = transientDataSource,
                                remoteCharacterDataSource = remoteDataSource,
                            )

                            When("loadPage is called") {
                                val result = repository.loadPage(filter, page)

                                Then("result should be characters from remote data source") {
                                    result.first shouldBe characters
                                }

                                Then("result should contain information next page doesn't exist") {
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
                                localTransientCharacterDataSource = createFakeLocalCharacterDatasource(),
                                remoteCharacterDataSource = remoteDataSource,
                            )

                            When("loadPage is called") {
                                val result = repository.loadPage(filter, page)

                                Then("result should be characters from remote data source") {
                                    result.first shouldBe characters
                                }

                                Then("result should contain information next page doesn't exist") {
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