// Currently source set naming convention is marked as false positive
@file:Suppress("MatchingDeclarationName")

package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.disk

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.sqldelight.SqlDelightDatabase

internal actual class SqlDriverFactory {

    actual fun createDriver(): SqlDriver = NativeSqliteDriver(
        SqlDelightDatabase.Schema,
        DATABASE_NAME,
    )

    companion object {

        private const val DATABASE_NAME = "the-lord-of-the-rings-character-wiki.db"
    }
}