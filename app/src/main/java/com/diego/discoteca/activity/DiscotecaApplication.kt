package com.diego.discoteca.activity

import android.app.Application
import com.diego.discoteca.database.DiscRoomDatabase
import com.diego.discoteca.repository.DiscRepository
import timber.log.Timber

class DiscotecaApplication : Application() {

    val database by lazy { DiscRoomDatabase.getInstance(this) }
    val repository by lazy { DiscRepository(database.discDatabaseDao) }

    /*companion object {
        lateinit var instance: DiscotecaApplication
            private set
        lateinit var res: Resources
            private set
    }*/

    override fun onCreate() {
        super.onCreate()
     //   instance = this
       // res = resources
        Timber.plant(Timber.DebugTree())
    }
}