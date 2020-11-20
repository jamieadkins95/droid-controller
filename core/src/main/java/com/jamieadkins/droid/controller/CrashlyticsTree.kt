package com.jamieadkins.droid.controller

import com.jamieadkins.droid.controller.core.BuildConfig
import timber.log.Timber

class CrashlyticsTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG)
            super.log(priority, tag, message, throwable)
        throwable?.let {
//            FirebaseCrashlytics.getInstance().log(message)
//            FirebaseCrashlytics.getInstance().recordException(it)
        }
    }
}
