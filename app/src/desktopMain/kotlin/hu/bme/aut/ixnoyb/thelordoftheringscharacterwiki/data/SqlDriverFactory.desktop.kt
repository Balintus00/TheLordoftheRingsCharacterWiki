// False positive error, the current naming is the correct according to the official conventions
//  https://kotlinlang.org/docs/coding-conventions.html#multiplatform-projects
@file:Suppress("MatchingDeclarationName")

package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.SqlDelightDatabase

internal actual class SqlDriverFactory {

    actual fun createDriver(): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
        SqlDelightDatabase.Schema.create(it)
    }
}