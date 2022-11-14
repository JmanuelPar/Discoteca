package com.diego.discoteca

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

object TestAndroidDataStore {
    private const val DATASTORE_NAME = "test_android_datastore"
    const val MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
    const val MODE_NIGHT = AppCompatDelegate.MODE_NIGHT_YES
    const val MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    @Volatile
    var testDataStore: DataStore<Preferences>? = null

    fun provideTestDataStore(context: Context): DataStore<Preferences> {
        synchronized(this) {
            return testDataStore ?: createTestDataStore(context)
        }
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
}