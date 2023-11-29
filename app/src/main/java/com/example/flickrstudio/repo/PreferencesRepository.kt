package com.example.flickrstudio.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.flickrstudio.util.read
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow


class PreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>
) {
    val storedQuery = dataStore.read(SEARCH_QUERY_KEY, "Flowers")

    val storedListQuery = dataStore.read(LIST_SEARCH_QUERY_KEY, "Flowers")

    val lastResultId = dataStore.read(PERF_LAST_RESULT_ID, "")

    val isPolling: Flow<Boolean> = dataStore.data.map {
        it[PREF_IS_POLLING] ?: false
    }.distinctUntilChanged()

    suspend fun setPolling(isPolling: Boolean) {
        dataStore.edit {
            it[PREF_IS_POLLING] = isPolling
        }
    }

    suspend fun setLastResultId(lastResultId: String) {
        dataStore.edit {
            it[PERF_LAST_RESULT_ID] = lastResultId
        }
    }

    suspend fun setStoredQuery(query: String) {
        dataStore.edit {
            it[SEARCH_QUERY_KEY] = query
            it[LIST_SEARCH_QUERY_KEY] += "&$query"
        }
    }

    companion object {
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
        private val LIST_SEARCH_QUERY_KEY = stringPreferencesKey("list_search_query")
        private val PERF_LAST_RESULT_ID = stringPreferencesKey("last_id")
        private val PREF_IS_POLLING = booleanPreferencesKey("isPolling")
        private var INSTANCE: PreferencesRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("Settings")
                }
                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun get(): PreferencesRepository {
            return INSTANCE ?: throw IllegalStateException(
                "You must initialize preference first"
            )
        }
    }
}

val Context.dataStore by preferencesDataStore(name = "Settings")