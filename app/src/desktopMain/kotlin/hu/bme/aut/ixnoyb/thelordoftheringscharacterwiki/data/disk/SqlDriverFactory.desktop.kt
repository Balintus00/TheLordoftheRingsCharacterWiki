// Currently source set naming convention is marked as false positive
@file:Suppress("MatchingDeclarationName")

package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.disk

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.sqldelight.SqlDelightDatabase

internal actual class SqlDriverFactory {

    actual fun createDriver(): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
        SqlDelightDatabase.Schema.create(it)
    }
}