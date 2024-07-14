package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.SqlDelightDatabase

internal actual class SqlDriverFactory {

    actual fun createDriver(): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
        SqlDelightDatabase.Schema.create(it)
    }
}