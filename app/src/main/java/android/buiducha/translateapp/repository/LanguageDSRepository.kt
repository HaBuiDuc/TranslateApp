package android.buiducha.translateapp.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

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