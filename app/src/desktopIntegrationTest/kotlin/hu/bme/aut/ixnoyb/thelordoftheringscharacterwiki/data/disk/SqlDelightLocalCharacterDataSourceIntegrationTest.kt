package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.disk

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.JdbcPreparedStatement
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Birth
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Death
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Gender
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Hair
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Height
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Name
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Race
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Realm
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Spouse
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.sqldelight.SqlDelightDatabase
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import java.util.Properties
import kotlin.io.path.createTempFile

class SqlDelightLocalCharacterDataSourceIntegrationTest : BehaviorSpec({

    xContext(
        "if a character is inserted again into the database, " +
                "the old entity should be overwritten"
    ) {
        Given("an in-memory JDBC SQLite Driver") {
            val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
                SqlDelightDatabase.Schema.create(it)
            }
            val database = SqlDelightDatabase(driver)
            val characterQueries = database.characterQueries
            val dataSource = SqlDelightLocalCharacterDataSource(characterQueries)

            And("a character to insert") {
                val characterID = "1"

                val freshlyInsertedBirth = "freshlyInsertedBirth"
                val freshlyInsertedDeath = "freshlyInsertedDeath"
                val freshlyInsertedGender = "freshlyInsertedGender"
                val freshlyInsertedHair = "freshlyInsertedHair"
                val freshlyInsertedHeight = "freshlyInsertedHeight"
                val freshlyInsertedName = "freshlyInsertedName"
                val freshlyInsertedRace = "freshlyInsertedRace"
                val freshlyInsertedRealm = "freshlyInsertedRealm"
                val freshlyInsertedSpouse = "freshlyInsertedSpouse"

                val characterToInsert = Character(
                    id = ID(characterID),
                    birth = Birth(freshlyInsertedBirth),
                    death = Death(freshlyInsertedDeath),
                    gender = Gender(freshlyInsertedGender),
                    hair = Hair(freshlyInsertedHair),
                    height = Height(freshlyInsertedHeight),
                    name = Name(freshlyInsertedName),
                    race = Race(freshlyInsertedRace),
                    realm = Realm(freshlyInsertedRealm),
                    spouse = Spouse(freshlyInsertedSpouse),
                )

                And("another character with the same ID was inserted before") {
                    val previouslyInsertedBirth = "previouslyInsertedBirth"
                    val previouslyInsertedDeath = "previouslyInsertedDeath"
                    val previouslyInsertedGender = "previouslyInsertedGender"
                    val previouslyInsertedHair = "previouslyInsertedHair"
                    val previouslyInsertedHeight = "previouslyInsertedHeight"
                    val previouslyInsertedName = "previouslyInsertedName"
                    val previouslyInsertedRace = "previouslyInsertedRace"
                    val previouslyInsertedRealm = "previouslyInsertedRealm"
                    val previouslyInsertedSpouse = "previouslyInsertedSpouse"

                    driver.execute(
                        binders = @Suppress("MagicNumber") {
                            this.bindString(0, characterID)
                            this.bindString(1, previouslyInsertedBirth)
                            this.bindString(2, previouslyInsertedDeath)
                            this.bindString(3, previouslyInsertedGender)
                            this.bindString(4, previouslyInsertedHair)
                            this.bindString(5, previouslyInsertedHeight)
                            this.bindString(6, previouslyInsertedName)
                            this.bindString(7, previouslyInsertedRace)
                            this.bindString(8, previouslyInsertedRealm)
                            this.bindString(9, previouslyInsertedSpouse)
                        },
                        identifier = null,
                        parameters = 10,
                        sql = @Suppress("LanguageMismatch") // KTIJ-24766
                        """
                        INSERT INTO character(
                            id, 
                            birth,
                            death,
                            gender,
                            hair,
                            height,
                            name,
                            race,
                            realm,
                            spouse
                        ) 
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent(),
                    )

                    When("the character is inserted") {
                        dataSource.insertAll(listOf(characterToInsert))

                        Then(
                            "the character should overwrite the previously inserted character"
                        ) {

                            @Suppress("MagicNumber")
                            driver.executeQuery(
                                binders = {
                                    bindString(0, characterID)
                                },
                                identifier = null,
                                mapper = { cursor ->
                                    cursor.getString(0) shouldBe characterID
                                    cursor.getString(1) shouldBe freshlyInsertedBirth
                                    cursor.getString(2) shouldBe freshlyInsertedDeath
                                    cursor.getString(3) shouldBe freshlyInsertedGender
                                    cursor.getString(4) shouldBe freshlyInsertedHair
                                    cursor.getString(5) shouldBe freshlyInsertedHeight
                                    cursor.getString(6) shouldBe freshlyInsertedName
                                    cursor.getString(7) shouldBe freshlyInsertedRace
                                    cursor.getString(8) shouldBe freshlyInsertedRealm
                                    cursor.getString(9) shouldBe freshlyInsertedSpouse

                                    QueryResult.Unit
                                },
                                parameters = 1,
                                sql = @Suppress("LanguageMismatch") // KTIJ-24766
                                """
                                SELECT *
                                FROM character
                                WHERE id = ?
                            """.trimIndent(),
                            )
                        }
                    }
                }
            }
        }
    }

    // TODO SQLite full
    xContext("an insertion with a full database should result in proper error result") {
        Given("TODO1") {
            val tempFile = createTempFile("test_database_limited_size", ".db")
            println("Temp file absolute path: ${tempFile.toAbsolutePath()}")

            val driver = JdbcSqliteDriver("jdbc:sqlite:$tempFile").also {
                SqlDelightDatabase.Schema.create(it)
            }
            val database = SqlDelightDatabase(driver)
            val characterQueries = database.characterQueries
            val dataSource = SqlDelightLocalCharacterDataSource(characterQueries)

            val (connection, onClose) = driver.connectionAndClose()
            try {
                //region page_size
                QueryResult.Value(
                    connection.prepareStatement("PRAGMA page_size").use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .executeQuery { cursor ->
                                println("Initial page size: ${cursor.getString(0)}")
                            }
                    },
                )

                connection.prepareStatement("PRAGMA page_size=512").use { jdbcStatement ->
                    JdbcPreparedStatement(jdbcStatement)
                        .execute()
                }

                connection.prepareStatement("VACUUM").use { jdbcStatement ->
                    JdbcPreparedStatement(jdbcStatement)
                        .execute()
                }

                QueryResult.Value(
                    connection.prepareStatement("PRAGMA page_size").use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .executeQuery { cursor ->
                                println("Modified page size: ${cursor.getString(0)}")
                            }
                    },
                )
                //endregion

                //region page_count
                QueryResult.Value(
                    connection.prepareStatement("PRAGMA page_count").use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .executeQuery { cursor ->
                                println("Page count: ${cursor.getString(0)}")
                            }
                    },
                )
                //endregion

                //region max_page_count
                QueryResult.Value(
                    connection.prepareStatement("PRAGMA max_page_count").use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .executeQuery { cursor ->
                                println("Initial max page count: ${cursor.getString(0)}")
                            }
                    },
                )

                val defaultPageCount = 3
                connection.prepareStatement("PRAGMA max_page_count=$defaultPageCount")
                    .use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .execute()
                    }

                QueryResult.Value(
                    connection.prepareStatement("PRAGMA max_page_count").use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .executeQuery { cursor ->
                                println("Modified max page count: ${cursor.getString(0)}")
                            }
                    },
                )
                //endregion

                //region database filling
                var counter = 0
                while (true) {
                    connection.prepareStatement(
                        """
                                    INSERT INTO character(
                                        id,
                                        birth,
                                        death,
                                        gender,
                                        hair,
                                        height,
                                        name,
                                        race,
                                        realm,
                                        spouse
                                    )
                                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                """.trimIndent()
                    ).use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .apply {
                                bindString(0, "id${counter++}")
                                bindString(1, "someValue")
                                bindString(2, "someValue")
                                bindString(3, "someValue")
                                bindString(4, "someValue")
                                bindString(5, "someValue")
                                bindString(6, "someValue")
                                bindString(7, "someValue")
                                bindString(8, "someValue")
                                bindString(9, "someValue")
                            }
                            .execute()
                    }
                    //endregion
                }
            } finally {
                onClose()
            }

            /*            driver.executeQuery(
                            binders = null,
                            identifier = null,
                            mapper = { cursor ->
                                println("Executed count")

                                println(cursor.getString(0))

                                QueryResult.Unit
                            },
                            parameters = 0,
                            sql = @Suppress("LanguageMismatch") "PRAGMA page_count",   // KTIJ-24766
                        )

                        driver.execute(
                            binders = null,
                            identifier = null,
                            parameters = 0,
                            sql = @Suppress("LanguageMismatch") "PRAGMA page_count = 1",   // KTIJ-24766
                        )

                        driver.executeQuery(
                            binders = null,
                            identifier = null,
                            mapper = { cursor ->
                                println("Executed count 2")

                                println(cursor.getString(0))

                                QueryResult.Unit
                            },
                            parameters = 0,
                            sql = @Suppress("LanguageMismatch") "PRAGMA page_count",   // KTIJ-24766
                        )

                        driver.executeQuery(
                            binders = null,
                            identifier = null,
                            mapper = { cursor ->
                                println("Executed size")

                                println(cursor.getString(0))

                                QueryResult.Unit
                            },
                            parameters = 0,
                            sql = @Suppress("LanguageMismatch") "PRAGMA page_size",   // KTIJ-24766
                        )

                        driver.execute(
                            binders = null,
                            identifier = null,
                            parameters = 0,
                            sql = @Suppress("LanguageMismatch") "PRAGMA page_size = 512",   // KTIJ-24766
                        )

            *//*            driver.execute(
                binders = null,
                identifier = null,
                parameters = 0,
                sql = @Suppress("LanguageMismatch") "VACUUM",   // KTIJ-24766
            )*//*

            driver.executeQuery(
                binders = null,
                identifier = null,
                mapper = { cursor ->
                    println("Executed size 2")

                    println(cursor.getString(0))

                    QueryResult.Unit
                },
                parameters = 0,
                sql = @Suppress("LanguageMismatch") "PRAGMA page_size",   // KTIJ-24766
            )*/
            /*
                        var counter = 0

                        while (true) {
                            if (counter % 50 == 0) {
                                println("Counter: $counter")
                            }

                            driver.execute(
                                binders = @Suppress("MagicNumber") {
                                    this.bindString(0, "id${counter++}")
                                    this.bindString(1, "someValue")
                                    this.bindString(2, "someValue")
                                    this.bindString(3, "someValue")
                                    this.bindString(4, "someValue")
                                    this.bindString(5, "someValue")
                                    this.bindString(6, "someValue")
                                    this.bindString(7, "someValue")
                                    this.bindString(8, "someValue")
                                    this.bindString(9, "someValue")
                                },
                                identifier = null,
                                parameters = 10,
                                sql = @Suppress("LanguageMismatch") // KTIJ-24766
                                """
                                    INSERT INTO character(
                                        id,
                                        birth,
                                        death,
                                        gender,
                                        hair,
                                        height,
                                        name,
                                        race,
                                        realm,
                                        spouse
                                    )
                                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                """.trimIndent(),
                            )
                        }
            */

            /*            driver.executeQuery(
                            binders = null,
                            identifier = null,
                            mapper = { cursor ->
                                println("Executed max count")

                                println(cursor.getString(0))

                                QueryResult.Unit
                            },
                            parameters = 0,
                            sql = @Suppress("LanguageMismatch") "PRAGMA max_page_count",   // KTIJ-24766
                        )

                        driver.execute(
                            binders = null,
                            identifier = null,
                            parameters = 0,
                            sql = @Suppress("LanguageMismatch") "PRAGMA max_page_count = 4294967295",   // KTIJ-24766
                        )

                        driver.executeQuery(
                            binders = null,
                            identifier = null,
                            mapper = { cursor ->
                                println("Executed max count 2")

                                println(cursor.getString(0))

                                QueryResult.Unit
                            },
                            parameters = 0,
                            sql = @Suppress("LanguageMismatch") "PRAGMA max_page_count",   // KTIJ-24766
                        )*/

            When("TODO2") {
                Then("TODO3") {

                }
            }
        }
    }

    // deadlock
    Context("Deadlock") {
        xGiven("TODO") {
            val tempFile = createTempFile("test_database_limited_size", ".db")
            println("Temp file absolute path: ${tempFile.toAbsolutePath()}")

            val driver = JdbcSqliteDriver("jdbc:sqlite:$tempFile").also {
                SqlDelightDatabase.Schema.create(it)
            }
            /*
                        LogSqliteDriver(
                            JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
                                SqlDelightDatabase.Schema.create(it)
                            }
                        ) { message -> println(message) }
            */
            val database = SqlDelightDatabase(driver)
            /*            val characterQueries = database.characterQueries
                        val dataSource = SqlDelightLocalCharacterDataSource(characterQueries)*/

            val transactionAThreadContext = newSingleThreadContext("TransactionAContext")
            val transactionBThreadContext = newSingleThreadContext("TransactionBContext")

            /*            withContext(transactionAThreadContext) {
                            println(driver.connectionAndClose().first)
                        }

                        withContext(transactionBThreadContext) {
                            println(driver.connectionAndClose().first)
                        }*/

            val (transactionAConnection, closeTransactionAAction) = async(transactionAThreadContext) {
                driver.connectionAndClose()
            }.await()

            val (transactionBConnection, closeTransactionBAction) = async(transactionBThreadContext) {
                driver.connectionAndClose()
            }.await()

            println(transactionAConnection)
            println(transactionBConnection)
            println(transactionAConnection === transactionBConnection)

            try {
                transactionAConnection.autoCommit = false
                transactionBConnection.autoCommit = false

                val transactionAStatement = transactionAConnection.createStatement()

                transactionAStatement.executeUpdate(
                    """
                        INSERT INTO character(
                            id, 
                            birth,
                            death,
                            gender,
                            hair,
                            height,
                            name,
                            race,
                            realm,
                            spouse
                        ) 
                        VALUES ('1', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd')
                    """.trimIndent()
                )

                val transactionBStatement = transactionBConnection.createStatement()

                transactionBStatement.executeQuery("SELECT * FROM character")

                transactionAConnection.commit()

//                transactionBConnection.commit()


            } catch (t: Throwable) {
                println("Throwable caught: ${t.message}\n${t.stackTraceToString()}")

                try {
                    val transactionBStatement = transactionBConnection.createStatement()

                    transactionBStatement.executeUpdate(
                        """
                        INSERT INTO character(
                            id, 
                            birth,
                            death,
                            gender,
                            hair,
                            height,
                            name,
                            race,
                            realm,
                            spouse
                        ) 
                        VALUES ('2', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd')
                    """.trimIndent()
                    )
                } catch (t: Throwable) {
                    println("Caught for second time: ${t.message}\n${t.stackTraceToString()}")
                }
            } finally {
                withContext(transactionAThreadContext) {
                    closeTransactionAAction()
                }

                withContext(transactionBThreadContext) {
                    closeTransactionBAction()
                }

                transactionAThreadContext.close()
                transactionBThreadContext.close()
            }

            /*            withContext(transactionAThreadContext) {
                            println(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
                                SqlDelightDatabase.Schema.create(it)
                            }.connectionAndClose().first)
                        }

                        withContext(transactionBThreadContext) {
                            println(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
                                SqlDelightDatabase.Schema.create(it)
                            }.connectionAndClose().first)
                        }*/

            /*            val (transactionAConnection, closeTransactionAAction) = async(transactionAThreadContext) {
                           driver.connectionAndClose()
                       }.await()

                       val (transactionBConnection, closeTransactionBAction) = async(transactionBThreadContext) {
                           driver.connectionAndClose()
                       }.await()

                      try {
                           println("AutoCommit enabled: ${transactionAConnection.autoCommit}")
                           println("Transaction isolation: ${transactionAConnection.transactionIsolation}")

                           println("Driver transaction: ${driver.transaction}")
                           println("Are connections the same by reference: ${transactionAConnection === transactionBConnection}")
                           println(transactionAConnection)
                           println(transactionBConnection)
                       } finally {
                           closeTransactionAAction()
                           closeTransactionBAction()
                       }*/

            database.transaction { }

            When("TODO2") {
                Then("TODO3") {

                }
            }
        }

        xGiven("TODO???") {
            val tempFile = createTempFile("test_database_limited_size", ".db")
            println("Temp file absolute path: ${tempFile.toAbsolutePath()}")

            val driver = JdbcSqliteDriver(
                "jdbc:sqlite:$tempFile",
                Properties().apply {
                    put("transaction_mode", "EXCLUSIVE")
                },
            ).also {
                SqlDelightDatabase.Schema.create(it)
            }
            /*
                        LogSqliteDriver(
                            JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
                                SqlDelightDatabase.Schema.create(it)
                            }
                        ) { message -> println(message) }
            */
            val database = SqlDelightDatabase(driver)
            /*            val characterQueries = database.characterQueries
                        val dataSource = SqlDelightLocalCharacterDataSource(characterQueries)*/

            val transactionAThreadContext = newSingleThreadContext("TransactionAContext")
            val transactionBThreadContext = newSingleThreadContext("TransactionBContext")

            /*            withContext(transactionAThreadContext) {
                            println(driver.connectionAndClose().first)
                        }

                        withContext(transactionBThreadContext) {
                            println(driver.connectionAndClose().first)
                        }*/

            val (transactionAConnection, closeTransactionAAction) = async(transactionAThreadContext) {
                driver.connectionAndClose()
            }.await()

            val (transactionBConnection, closeTransactionBAction) = async(transactionBThreadContext) {
                driver.connectionAndClose()
            }.await()

            println(transactionAConnection)
            println(transactionBConnection)
            println(transactionAConnection === transactionBConnection)

            try {
                transactionAConnection.autoCommit = false
                transactionBConnection.autoCommit = false

                val transactionAStatement = transactionAConnection.createStatement()

                transactionAStatement.executeQuery("SELECT * FROM character")

                val transactionBStatement = transactionBConnection.createStatement()

                transactionBStatement.executeUpdate(
                    """
                        INSERT INTO character(
                            id, 
                            birth,
                            death,
                            gender,
                            hair,
                            height,
                            name,
                            race,
                            realm,
                            spouse
                        ) 
                        VALUES ('1', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd')
                    """.trimIndent()
                )

//                transactionAConnection.commit()

//                transactionBConnection.commit()


            } catch (t: Throwable) {
                println("Throwable caught: ${t.message}\n${t.stackTraceToString()}")

                /*
                                try {
                                    val transactionBStatement = transactionBConnection.createStatement()

                                    transactionBStatement.executeUpdate(
                                        """
                                        INSERT INTO character(
                                            id,
                                            birth,
                                            death,
                                            gender,
                                            hair,
                                            height,
                                            name,
                                            race,
                                            realm,
                                            spouse
                                        )
                                        VALUES ('2', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd', 'asd')
                                    """.trimIndent()
                                    )
                                } catch (t: Throwable) {
                                    println("Caught for second time: ${t.message}\n${t.stackTraceToString()}")
                                }
                */
            } finally {
                withContext(transactionAThreadContext) {
                    closeTransactionAAction()
                }

                withContext(transactionBThreadContext) {
                    closeTransactionBAction()
                }

                transactionAThreadContext.close()
                transactionBThreadContext.close()
            }

            /*            withContext(transactionAThreadContext) {
                            println(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
                                SqlDelightDatabase.Schema.create(it)
                            }.connectionAndClose().first)
                        }

                        withContext(transactionBThreadContext) {
                            println(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
                                SqlDelightDatabase.Schema.create(it)
                            }.connectionAndClose().first)
                        }*/

            /*            val (transactionAConnection, closeTransactionAAction) = async(transactionAThreadContext) {
                           driver.connectionAndClose()
                       }.await()

                       val (transactionBConnection, closeTransactionBAction) = async(transactionBThreadContext) {
                           driver.connectionAndClose()
                       }.await()

                      try {
                           println("AutoCommit enabled: ${transactionAConnection.autoCommit}")
                           println("Transaction isolation: ${transactionAConnection.transactionIsolation}")

                           println("Driver transaction: ${driver.transaction}")
                           println("Are connections the same by reference: ${transactionAConnection === transactionBConnection}")
                           println(transactionAConnection)
                           println(transactionBConnection)
                       } finally {
                           closeTransactionAAction()
                           closeTransactionBAction()
                       }*/

            database.transaction { }

            When("TODO2") {
                Then("TODO3") {

                }
            }
        }

        xGiven("journaling mode") {
            val tempFile = createTempFile("test_database_limited_size", ".db")
            println("Temp file absolute path: ${tempFile.toAbsolutePath()}")

            val driver = JdbcSqliteDriver(
                "jdbc:sqlite:$tempFile"
//                JdbcSqliteDriver.IN_MEMORY
            ).also {
                SqlDelightDatabase.Schema.create(it)
            }

            val (connection, onClose) = driver.connectionAndClose()

            try {
                QueryResult.Value(
                    connection.prepareStatement("PRAGMA journal_mode").use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .executeQuery { cursor ->
                                println("Journaling mode: ${cursor.getString(0)}")
                            }
                    },
                )

                QueryResult.Value(
                    connection.prepareStatement("PRAGMA journal_mode=wal").use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .executeQuery { cursor ->
                                println("Changed journaling mode to: ${cursor.getString(0)}")
                            }
                    },
                )

                QueryResult.Value(
                    connection.prepareStatement("PRAGMA journal_mode").use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .executeQuery { cursor ->
                                println("Journaling mode: ${cursor.getString(0)}")
                            }
                    },
                )
            } finally {
                onClose()
            }

            val (connection2, onClose2) = driver.connectionAndClose()
            try {
                QueryResult.Value(
                    connection2.prepareStatement("PRAGMA journal_mode").use { jdbcStatement ->
                        JdbcPreparedStatement(jdbcStatement)
                            .executeQuery { cursor ->
                                println("Journaling mode: ${cursor.getString(0)}")
                            }
                    },
                )

            } finally {
                onClose2()
            }

            When("TODO2") {
                Then("TODO3") {

                }
            }
        }
    }

    Given("SQLite version") {
/*        val tempFile = createTempFile("test_database_limited_size", ".db")
        println("Temp file absolute path: ${tempFile.toAbsolutePath()}")*/

        val driver = JdbcSqliteDriver(
//            "jdbc:sqlite:$tempFile"
                JdbcSqliteDriver.IN_MEMORY
        ).also {
            SqlDelightDatabase.Schema.create(it)
        }

        val (connection, onClose) = driver.connectionAndClose()

        try {
            QueryResult.Value(
                connection.prepareStatement("SELECT SQLITE_VERSION()").use { jdbcStatement ->
                    JdbcPreparedStatement(jdbcStatement)
                        .executeQuery { cursor ->
                            println("SQLITE Version: ${cursor.getString(0)}")
                        }
                },
            )
        } finally {
            onClose()
        }

        When("TODO") {
            Then("TODO2") {

            }
        }
    }

    // TODO lock conflict testing

    // TODO bulk insert

    //  TODO read flow retry problem
})