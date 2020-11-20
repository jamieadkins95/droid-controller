package com.jamieadkins.droid.controller.controls

fun ByteArray.toHex(): String {
    return joinToString(separator = "") { String.format("%02X", it) }
}

fun String.hexToByteArray(): ByteArray {
    return replace(" ", "").chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

fun Int.to2DigitHexString(): String = String.format("%02X", this)