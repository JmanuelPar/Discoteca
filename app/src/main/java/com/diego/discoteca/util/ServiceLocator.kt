package com.diego.discoteca.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.room.RoomDatabase
import com.diego.discoteca.data.PreferencesManager
import com.diego.discoteca.data.source.local.DiscsLocalDataSource
import com.diego.discoteca.data.source.paging.DiscsPagingDataSource
import com.diego.discoteca.database.DiscDatabaseDao
import com.diego.discoteca.database.DiscRoomDatabase
import com.diego.discoteca.network.DiscogsApi
import com.diego.discoteca.repository.DefaultDiscsRepository
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.Constants.DATABASE_NAME
import com.diego.discoteca.util.Constants.USER_PREFERENCES_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.annotations.VisibleForTesting

object ServiceLocator {

    private val lock = Any()
    private var database: DiscRoomDatabase? = null
    private var dataStore: DataStore<Preferences>? = null

    @Volatile
    var discsRepository: DiscsRepository? = null
        @VisibleForTesting set

    @Volatile
    var preferencesManager: PreferencesManager? = null

    fun provideDiscRepository(context: Context): DiscsRepository {
        synchronized(this) {
            return discsRepository ?: createDiscRepository(context)
        }
    }

    fun providePreferencesManager(context: Context): PreferencesManager {
        synchronized(this) {
            return preferencesManager ?: createPreferencesManager(context)
        }
    }

    private fun createPreferencesManager(context: Context): PreferencesManager {
        val myDataStore = dataStore ?: createPreferencesDataStore(context)
        val newPref = PreferencesManager(myDataStore)
        preferencesManager = newPref
        return newPref
    }

    private fun createPreferencesDataStore(context: Context): DataStore<Preferences> {
        val myDataStore = PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(context, USER_PREFERENCES_NAME)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = {
                context.preferencesDataStoreFile(USER_PREFERENCES_NAME)
            }
        )
        dataStore = myDataStore
        return myDataStore
    }

    private fun createDiscRepository(context: Context): DiscsRepository {
        val discRoomDatabase = database ?: createDiscRoomDatabase(context)
        val myRepo = DefaultDiscsRepository(
            discsLocalDataSource = createDiscsLocalDataSource(discRoomDatabase.discDatabaseDao()),
            discsPagingDataSource = createDiscsPagingDataSource(
                discRoomDatabase.discDatabaseDao(),
                context
            )
        )
        discsRepository = myRepo
        return myRepo
    }

    private fun createDiscRoomDatabase(context: Context): DiscRoomDatabase {
        val discRoomDatabase = Room.databaseBuilder(
            context.applicationContext,
            DiscRoomDatabase::class.java,
            DATABASE_NAME
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
        database = discRoomDatabase
        return discRoomDatabase
    }

    private fun createDiscsLocalDataSource(discDatabaseDao: DiscDatabaseDao) =
        DiscsLocalDataSource(discDatabaseDao)

    private fun createDiscsPagingDataSource(discDatabaseDao: DiscDatabaseDao, context: Context) =
        DiscsPagingDataSource(
            dao = discDatabaseDao,
            service = createApiService(),
            context = context
        )

    private fun createApiService() = DiscogsApi.retrofitService

    @androidx.annotation.VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            discsRepository = null
        }
    }
}