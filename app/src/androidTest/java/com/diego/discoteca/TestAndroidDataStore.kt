package com.diego.discoteca

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import com.diego.discoteca.data.PreferencesManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

object TestAndroidDataStore {
    private val lock = Any()
    private const val DATASTORE_NAME = "test_android_datastore"
    const val MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
    const val MODE_NIGHT = AppCompatDelegate.MODE_NIGHT_YES
    const val MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    private var testDataStore: DataStore<Preferences>? = null

    @Volatile
    var preferencesManager: PreferencesManager? = null

    fun providePreferencesManager(context: Context): PreferencesManager {
        synchronized(this) {
            return preferencesManager ?: createPreferencesManager(context)
        }
    }

    private fun createPreferencesManager(context: Context): PreferencesManager {
        val myDataStore = testDataStore ?: createTestDataStore(context)
        val newPref = PreferencesManager(myDataStore)
        preferencesManager = newPref
        return newPref
    }

    private fun createTestDataStore(context: Context): DataStore<Preferences> {
        val myDataStore = PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile(DATASTORE_NAME)
            }
        )
        testDataStore = myDataStore
        return myDataStore
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun reset() {
        synchronized(lock) {
            runTest {
                testDataStore?.edit { it.clear() }
            }
        }
    }
}