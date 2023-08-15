package android.buiducha.translateapp.database

import androidx.room.TypeConverter
import java.util.*

class FavoriteTypeConverters {

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString()
    }


    @TypeConverter
    fun toList(string: String): List<String> {
        return listOf(string)
    }

    @TypeConverter
    fun toString(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(id: String): UUID {
        return UUID.fromString(id)
    }
}