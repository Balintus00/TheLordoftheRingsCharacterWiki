package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.di

import androidx.room.Room
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.AppDatabase
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.RoomLocalPersistentCharacterDatasource
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.LocalCharacterDatasource
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DATABASE_NAME = "the-lord-of-the-rings-character-wiki-db"

actual val platformSpecificModule = module {

    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    single {
        val database: AppDatabase = get()
        database.characterDao()
    }

    single<LocalCharacterDatasource>(named(NAME_PERSISTENT_CHARACTER_DATA_SOURCE)) {
        RoomLocalPersistentCharacterDatasource(get())
    }
}