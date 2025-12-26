package at.xa1.architecture.kmp.sample.app

import android.app.Application
import at.xa1.architecture.kmp.sample.shared.integration.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@SampleApplication)
            modules(appModule)
        }
    }
}
