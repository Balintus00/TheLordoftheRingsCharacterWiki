package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.SqlDelightDatabase

private const val DATABASE_NAME = "the-lord-of-the-rings-character-wiki.db"
internal actual fun getSqlDelightDriver(): SqlDriver = NativeSqliteDriver(
    SqlDelightDatabase.Schema,
    DATABASE_NAME,
)