package com.example.flickrstudio.util

import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged

fun View.changeVisibility(viewVisibility: Int) {
    visibility = viewVisibility
}

fun DataStore<Preferences>.read(key: Preferences.Key<String>, defaultValue: String): Flow<String> {
    return data.map {
        it[key] ?: defaultValue
    }.distinctUntilChanged()
}