package com.jamieadkins.droid.controller.controls

sealed class ConnectionState {

    object Disconnected : ConnectionState()
    object ConnectedWithoutHandshake : ConnectionState()
    data class Connected(val doingSequencePlayback: Boolean = false) : ConnectionState()
}