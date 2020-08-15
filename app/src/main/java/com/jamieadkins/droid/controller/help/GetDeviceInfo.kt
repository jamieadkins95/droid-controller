package com.jamieadkins.droid.controller.help

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.ShareCompat
import com.jamieadkins.droid.controller.BuildConfig
import com.jamieadkins.droid.controller.R

object GetDeviceInfo {

    fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            Build.MANUFACTURER,
            Build.BRAND,
            Build.MODEL,
            Build.PRODUCT,
            Build.HARDWARE,
            Build.VERSION.RELEASE,
            BuildConfig.VERSION_NAME
        )
    }

    fun getDeviceInfoString(context: Context): String {
        return context.getString(R.string.help_template, getDeviceInfo().toString())
    }

    fun getHelpIntent(activity: Activity): Intent {
        val email = activity.getString(R.string.help_to)
        val subject = activity.getString(R.string.help_subject)
        val body = getDeviceInfoString(activity)
        return ShareCompat.IntentBuilder.from(activity)
            .setType("message/rfc822")
            .addEmailTo(email)
            .setSubject(subject)
            .setText(body)
            .setChooserTitle(R.string.help_prompt)
            .createChooserIntent()
    }
}