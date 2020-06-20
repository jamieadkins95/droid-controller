package com.jamieadkins.droid.controller.connect

sealed class ConnectionEvent {

    object StartScan : ConnectionEvent()
    object Failure : ConnectionEvent()
    object UserDisconnect : ConnectionEvent()
    object Disconnected : ConnectionEvent()
    object BluetoothDisabled : ConnectionEvent()
    object LocationPermissionNotGranted : ConnectionEvent()
    object DroidNamingCancelled : ConnectionEvent()
    data class UnnamedDroidFound(val address: String) : ConnectionEvent()
    data class NamedDroidFound(val address: String) : ConnectionEvent()
    data class DroidNamed(val address: String) : ConnectionEvent()
    data class DroidSelected(val address: String) : ConnectionEvent()
    data class ConnectionSuccessful(val address: String) : ConnectionEvent()
    data class HandshakeComplete(val address: String) : ConnectionEvent()
    object SequenceStarted : ConnectionEvent()
    object SequenceEnded : ConnectionEvent()

}