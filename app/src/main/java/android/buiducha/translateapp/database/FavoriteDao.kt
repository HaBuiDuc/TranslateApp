package android.buiducha.translateapp.database

import android.buiducha.translateapp.model.Vocabulary
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM vocabulary")
    fun getVocabularies(): Flow<List<Vocabulary>>

    @Insert
    suspend fun addVocabulary(item: Vocabulary)

    @Delete
    suspend fun deleteVocabulary(item: Vocabulary)
}