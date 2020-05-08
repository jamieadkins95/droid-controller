package com.jamieadkins.droid.controller.connect

sealed class ScanState {

    object BluetoothDisabled : ScanState()
    object LocationPermissionNotGranted : ScanState()
    object Scanning : ScanState()
    data class DroidFound(val address: String) : ScanState()
    object ScanFailed : ScanState()
}