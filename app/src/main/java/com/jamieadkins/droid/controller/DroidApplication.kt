package com.jamieadkins.droid.controller

import android.app.Application
import android.content.Context
import com.jamieadkins.droid.controller.di.CoreComponent
import com.jamieadkins.droid.controller.di.DaggerCoreComponent
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber

class DroidApplication : Application() {

    private val coreComponent: CoreComponent by lazy {
        DaggerCoreComponent.builder()
            .context(context = this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(CrashlyticsTree())

        initErrorHandling()
    }

    /**
     * Rx can trigger crashes if exceptions occur after an Observable stream has completed/disposed.
     *
     * In this application, Bluetooth scans can fail after the observable stream has already completed.
     *
     * https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
     */
    private fun initErrorHandling() {
        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) {
//                FirebaseCrashlytics.getInstance().recordException(e)
            } else {
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), e)
            }
        }
    }

    companion object {
        val Context.coreComponent
            get() = (applicationContext as DroidApplication).coreComponent
    }
}