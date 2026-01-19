package com.example.zenithportfolio

import android.app.Application
import com.example.zenithportfolio.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.example.zenithportfolio.data.db.DatabaseDriverFactory
class ZenithApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val androidModule = module {
            single { DatabaseDriverFactory(this@ZenithApplication) }
        }

        startKoin {
            androidLogger()
            androidContext(this@ZenithApplication)
            modules(androidModule + appModules)
        }
    }
}
