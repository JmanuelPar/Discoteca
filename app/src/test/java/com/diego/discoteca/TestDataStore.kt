package com.diego.discoteca

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

object TestDataStore {
    private const val DATASTORE_NAME = "test_datastore"

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