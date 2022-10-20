package com.diego.discoteca

import android.app.Application
import com.diego.discoteca.repository.DiscsRepository
import com.diego.discoteca.util.ServiceLocator
import timber.log.Timber

class DiscotecaApplication : Application() {

    val discsRepository: DiscsRepository
        get() = ServiceLocator.provideDiscRepository(this)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}