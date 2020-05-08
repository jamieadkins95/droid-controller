package com.jamieadkins.droid.controller.connect

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import androidx.core.content.ContextCompat
import com.jamieadkins.droid.controller.di.ApplicationContext
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class ConnectModule {

    @Binds
    abstract fun presenter(presenter: ConnectPresenter): ConnectContract.Presenter

    companion object {

        @Provides
        fun adapter(): BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        @Provides
        fun bleScanner(adapter: BluetoothAdapter?): BluetoothLeScanner? = adapter?.bluetoothLeScanner
    }

}
