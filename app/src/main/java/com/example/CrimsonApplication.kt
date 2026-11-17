package com.example

import android.app.Application
import com.example.core.di.AppContainer
import com.example.core.di.DefaultAppContainer

/**
 * Application entry point initializing the manual DI [AppContainer] and installing
 * the zero-network [com.example.core.crash.LocalCrashHandler].
 */
class CrimsonApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        if (!::container.isInitialized) {
            container = DefaultAppContainer(this)
        }
        container.crashHandler.install()
        container.logger.i(TAG, "MobileGlues Crimson initialized (${BuildConfig.VERSION_NAME})")
    }

    companion object {
        private const val TAG = "CrimsonApp"
    }
}
