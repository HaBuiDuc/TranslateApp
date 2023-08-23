package android.buiducha.translateapp.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import com.google.gson.Gson

object LanguageDSRepository {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "recentLang")
    const val SOURCE_RECENT = "source_recent"
    const val DES_RECENT = "destination_recent"
    suspend fun saveLang(context: Context, data: String, langKey: String) {
        val dataStoreKey = stringSetPreferencesKey(langKey)
        val lastValue = readLang(context, langKey)
        if (!lastValue.contains(data)) {
            if (lastValue.size == 3) {
                lastValue.removeAt(0)
            }
            lastValue.add(lastValue.size, data)
            context.dataStore.edit { settings ->
                settings[dataStoreKey] = lastValue.toSet()
            }
        } else {
            lastValue.removeAt(lastValue.indexOf(data))
            context.dataStore.edit { settings ->
                settings[dataStoreKey] = lastValue.toSet()
            }
            lastValue.add(lastValue.size, data)
            context.dataStore.edit { settings ->
                settings[dataStoreKey] = lastValue.toSet()
            }
        }
    }

//    suspend fun saveLang(context: Context, data: String, langKey: String) {
//        val dataStoreKey = stringPreferencesKey(langKey)
//        val gson = Gson()
//
//        val lastValueJson = context.dataStore.data
//            .map { preferences ->
//                preferences[dataStoreKey]
//            }
//            .firstOrNull()
//
//        val lastValue = if (lastValueJson != null) {
//            gson.fromJson(lastValueJson, MutableList::class.java) as MutableList<String>
//        } else {
//            mutableListOf()
//        }
//
//        if (lastValue.contains(data)) {
//            lastValue.remove(data) // Remove existing instance
//        } else if (lastValue.size == 3) {
//            lastValue.removeAt(0) // Remove oldest element if list size exceeds 3
//        }
//
//        lastValue.add(data) // Add the new or updated element to the end
//
//        context.dataStore.edit { settings ->
//            settings[dataStoreKey] = gson.toJson(lastValue)
//        }
//    }

    suspend fun savePairLang(context: Context, source: String, target: String) {
        saveLang(context, source, SOURCE_RECENT)
        saveLang(context, target, DES_RECENT)
    }

    suspend fun readLang(context: Context, langKey: String): MutableList<String> {
        val dataStoreKey = stringSetPreferencesKey(langKey)
        val preferences = context.dataStore.data.first()
        return if (preferences[dataStoreKey] != null) {
            preferences[dataStoreKey]!!.toMutableList()
        } else {
            mutableListOf()
        }
    }
}