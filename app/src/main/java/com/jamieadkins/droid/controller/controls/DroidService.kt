package com.jamieadkins.droid.controller.controls

import io.reactivex.Observable

interface DroidService {

    fun connect(address: String): Boolean

    fun observe(): Observable<ConnectionState>

    fun disconnect()

    fun sendCommand(droidAction: DroidAction)
}