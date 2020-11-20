package com.jamieadkins.droid.controller.connect

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import dagger.Module
import dagger.Provides

@Module
class ConnectModule {

    @Provides
    fun adapter(): BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    @Provides
    fun bleScanner(adapter: BluetoothAdapter?): BluetoothLeScanner? = adapter?.bluetoothLeScanner

}
