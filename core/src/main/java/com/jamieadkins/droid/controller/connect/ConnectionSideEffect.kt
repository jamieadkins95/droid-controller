package com.jamieadkins.droid.controller.connect

sealed class ConnectionSideEffect {

    data class ConnectToDroid(val address: String) : ConnectionSideEffect()

}