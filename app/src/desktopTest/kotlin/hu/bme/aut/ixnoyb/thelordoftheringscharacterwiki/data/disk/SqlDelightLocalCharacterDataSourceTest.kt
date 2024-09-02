package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.disk

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.sqldelight.SqlDelightDatabase
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class SqlDelightLocalCharacterDataSourceTest : BehaviorSpec({

    Context("unitTestDummyContext") {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
            SqlDelightDatabase.Schema.create(it)
        }



        Given("dummyGiven") {

            When("dummyWhen") {


                Then("dummyThen") {
                    true shouldBe true
                }
            }
        }
    }
})