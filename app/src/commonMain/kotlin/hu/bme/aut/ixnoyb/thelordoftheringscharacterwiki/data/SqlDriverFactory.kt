package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import app.cash.sqldelight.db.SqlDriver

internal expect class SqlDriverFactory {

    fun createDriver(): SqlDriver
}