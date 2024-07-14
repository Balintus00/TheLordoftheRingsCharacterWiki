package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.SqlDelightDatabase

internal actual class SqlDriverFactory(private val context: Context) {

    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        SqlDelightDatabase.Schema,
        context,
        DATABASE_NAME,
    )

    companion object {

        private const val DATABASE_NAME = "the-lord-of-the-rings-character-wiki.db"
    }
}