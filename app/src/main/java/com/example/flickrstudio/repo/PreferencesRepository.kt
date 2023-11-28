package com.example.flickrstudio.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


class PreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>
) {
    val storedQuery = dataStore.data.map {
        it[SEARCH_QUERY_KEY] ?: "Flowers"
    }.distinctUntilChanged()

    suspend fun setStoredQuery(query: String) {
        dataStore.edit {
            it[SEARCH_QUERY_KEY] = query
        }
    }

    companion object {
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
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