package com.bottlerocketstudios.brarchitecture

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("unused")
class BitbucketApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // TODO: Remove logging in production
        Timber.plant(Timber.DebugTree())
        startKoin {
            // TODO: Remove logging in production
            androidLogger()
            androidContext(this@BitbucketApplication)
            modules(appModule)
        }
    }
}
