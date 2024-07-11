package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.di

import app.cash.sqldelight.db.SqlDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.SqlDelightDatabase
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.SqlDelightLocalPersistentCharacterDataSource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual val platformSpecificModule = module {

    single {
        SqlDelightDatabase(getSqlDelightDriver())
    }

    single {
        val database: SqlDelightDatabase = get()
        database.characterQueries
    }

    single<LocalCharacterDataSource>(named(NAME_PERSISTENT_CHARACTER_DATA_SOURCE)) {
        SqlDelightLocalPersistentCharacterDataSource(get())
    }
}

internal expect fun getSqlDelightDriver(): SqlDriver