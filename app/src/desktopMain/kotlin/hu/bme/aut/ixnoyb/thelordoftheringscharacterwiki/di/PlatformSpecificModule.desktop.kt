package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.di

import app.cash.sqldelight.db.SqlDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.SqlDriverFactory
import org.koin.dsl.module

internal actual val platformSpecificModule = module {

    single<SqlDriver> { SqlDriverFactory().createDriver() }
}