package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query("SELECT * FROM character WHERE id = :id")
    fun getById(id: String): Flow<Character?>

    @Query("SELECT * FROM character ORDER BY name asc")
    fun getAll(): Flow<List<Character>>

    @Query("DELETE FROM character")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg characters: Character)
}