package com.jamieadkins.droid.controller.controls

import com.jamieadkins.droid.controller.controls.advanced.DroidActionWithDelay
import io.reactivex.Observable

interface DroidService {

    fun connect(address: String): Boolean

    fun observe(): Observable<ConnectionState>

    fun disconnect()

    fun sendCommand(droidAction: DroidAction)

    fun startSequence(actions: List<DroidActionWithDelay>)

    fun stopSequence()
}