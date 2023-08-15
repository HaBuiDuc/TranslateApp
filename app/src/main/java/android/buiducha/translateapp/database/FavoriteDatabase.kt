package android.buiducha.translateapp.database

import android.buiducha.translateapp.model.Vocabulary
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Vocabulary::class], version = 1, exportSchema = false)
@TypeConverters(FavoriteTypeConverters::class)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}