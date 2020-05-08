package com.jamieadkins.droid.controller.controls

sealed class ConnectionState {

    object Disconnected : ConnectionState()
    object ConnectedWithoutHandshake : ConnectionState()
    object Connected : ConnectionState()
}