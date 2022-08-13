package com.diego.discoteca.activity

import android.app.Application
import com.diego.discoteca.database.DiscRoomDatabase
import com.diego.discoteca.network.DiscogsApi
import com.diego.discoteca.repository.DiscRepository
import timber.log.Timber

class DiscotecaApplication : Application() {

    val database by lazy { DiscRoomDatabase.getInstance(this) }
    val repository by lazy {
        DiscRepository(
            dao = database.discDatabaseDao,
            service = DiscogsApi.retrofitService,
            context = this
        )
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}