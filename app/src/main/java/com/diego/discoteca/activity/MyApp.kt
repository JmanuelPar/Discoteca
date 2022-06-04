package com.diego.discoteca.activity

import android.app.Application
import android.content.res.Resources
import com.diego.discoteca.database.DiscRoomDatabase
import com.diego.discoteca.repository.DiscRepository
import timber.log.Timber

class MyApp : Application() {

    val database by lazy { DiscRoomDatabase.getInstance(this) }
    val repository by lazy { DiscRepository(database.discDatabaseDao) }

    companion object {
        lateinit var instance: MyApp
            private set
        lateinit var res: Resources
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        res = resources
        Timber.plant(Timber.DebugTree())
    }
}