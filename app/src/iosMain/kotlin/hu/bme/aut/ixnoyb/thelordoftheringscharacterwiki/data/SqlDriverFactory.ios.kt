package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.SqlDelightDatabase

internal actual class SqlDriverFactory {

    actual fun createDriver(): SqlDriver = NativeSqliteDriver(
        SqlDelightDatabase.Schema,
        DATABASE_NAME,
    )

    companion object {

        private const val DATABASE_NAME = "the-lord-of-the-rings-character-wiki.db"
    }
}