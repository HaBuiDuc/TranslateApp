package android.buiducha.translateapp.repository

import android.buiducha.translateapp.database.FavoriteDatabase
import android.buiducha.translateapp.model.Vocabulary
import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class FavoriteRepository private constructor(context: Context){
    private val database = Room.databaseBuilder(
        context.applicationContext,
        FavoriteDatabase::class.java,
        DATABASE_NAME
    ).build()

    fun getVocabularies(): Flow<List<Vocabulary>> = database.favoriteDao().getVocabularies()

    suspend fun addVocabulary(item: Vocabulary) = database.favoriteDao().addVocabulary(item)

    suspend fun deleteVocabulary(item: Vocabulary) = database.favoriteDao().deleteVocabulary(item)

    companion object {

        private const val DATABASE_NAME = "favorite-vocabulary"

        private var INSTANCE: FavoriteRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = FavoriteRepository(context)
            }
        }

        fun get(): FavoriteRepository {
            return INSTANCE?: throw IllegalStateException("repository must be init")
        }
    }
}