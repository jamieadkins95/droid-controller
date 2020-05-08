package com.jamieadkins.droid.controller.controls

interface DroidService {

    fun connect(address: String): Boolean

    fun disconnect()

    fun sendCommand(droidAction: DroidAction)
}