package com.jamieadkins.droid.controller.connect

sealed class ScanState {

    object BluetoothDisabled : ScanState()
    object LocationPermissionNotGranted : ScanState()
    object Scanning : ScanState()
    data class UnnamedDroidFound(val address: String) : ScanState()
    data class NamedDroidFound(val address: String) : ScanState()
    data class Connected(val address: String) : ScanState()
    object ScanFailed : ScanState()
}