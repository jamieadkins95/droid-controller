package com.jamieadkins.droid.controller.help

import androidx.annotation.Keep

@Keep
data class DeviceInfo(
    val manufacturer: String,
    val brand: String,
    val model: String,
    val product: String,
    val hardware: String,
    val version: String,
    val appVersion: String
)