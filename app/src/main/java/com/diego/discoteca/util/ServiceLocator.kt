package com.diego.discoteca.util

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.diego.discoteca.data.source.local.DiscsLocalDataSource
import com.diego.discoteca.data.source.paging.DiscsPagingDataSource
import com.diego.discoteca.database.DiscDatabaseDao
import com.diego.discoteca.database.DiscRoomDatabase
import com.diego.discoteca.network.DiscogsApi
import com.diego.discoteca.repository.DefaultDiscsRepository
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.Constants.DATABASE_NAME
import org.jetbrains.annotations.VisibleForTesting

object ServiceLocator {

    private val lock = Any()
    private var database: DiscRoomDatabase? = null

    @Volatile
    var discsRepository: DiscsRepository? = null
        @VisibleForTesting set

    fun provideDiscRepository(context: Context): DiscsRepository {
        synchronized(this) {
            return discsRepository ?: createDiscRepository(context)
        }
    }

    private fun createDiscRepository(context: Context): DiscsRepository {
        val discRoomDatabase = database ?: createDiscRoomDatabase(context)
        val newRepo = DefaultDiscsRepository(
            discsLocalDataSource = createDiscsLocalDataSource(discRoomDatabase.discDatabaseDao()),
            discsPagingDataSource = createDiscsPagingDataSource(
                discRoomDatabase.discDatabaseDao(),
                context
            )
        )
        discsRepository = newRepo
        return newRepo
    }

    private fun createDiscRoomDatabase(context: Context): DiscRoomDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            DiscRoomDatabase::class.java,
            DATABASE_NAME
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
        database = result
        return result
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