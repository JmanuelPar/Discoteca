package com.diego.discoteca.data

import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException

enum class SortOrder { BY_NAME, BY_TITLE, BY_YEAR }

data class SortOrderPreferences(val sortOrder: SortOrder)

class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    private val NIGHT_MODE = intPreferencesKey("night_mode")
    private val GRID_MODE = booleanPreferencesKey("grid_mode")
    private val SORT_ORDER = stringPreferencesKey("sort_order")

    val nightModeFlow: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e("Error reading nightMode preferences : $exception")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Default : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            preferences[NIGHT_MODE] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

    val gridModeFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e("Error reading gridMode preferences : $exception")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Default : false -> linearLayout
            preferences[GRID_MODE] ?: false
        }

    val sortOrderFlow: Flow<SortOrderPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e("Error reading sortOrder preferences : $exception")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Default : SortOrder.BY_NAME
            val sortOrder =
                SortOrder.valueOf(
                    preferences[SORT_ORDER] ?: SortOrder.BY_NAME.name
                )
            SortOrderPreferences(sortOrder)
        }

    suspend fun updateNightMode(nightMode: Int) {
        dataStore.edit { preferences ->
            preferences[NIGHT_MODE] = nightMode
        }
    }

    suspend fun updateGridMode(gridMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[GRID_MODE] = gridMode
        }
    }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[SORT_ORDER] = sortOrder.name
        }
    }
}