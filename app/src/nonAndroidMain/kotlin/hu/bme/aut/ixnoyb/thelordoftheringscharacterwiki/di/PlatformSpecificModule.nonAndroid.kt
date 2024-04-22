package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.di

import app.cash.sqldelight.db.SqlDriver
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.SqlDelightDatabase
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.SqlDelightLocalPersistentCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDatasource
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

    single<LocalCharacterDatasource>(named(NAME_PERSISTENT_CHARACTER_DATA_SOURCE)) {
        SqlDelightLocalPersistentCharacterDatasource(get())
    }
}

internal expect fun getSqlDelightDriver(): SqlDriver