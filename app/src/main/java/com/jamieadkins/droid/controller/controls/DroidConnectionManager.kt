package com.jamieadkins.droid.controller.controls

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.jamieadkins.droid.controller.addToComposite
import com.jamieadkins.droid.controller.controls.BluetoothConstants.WRITE_CHARACTERISTIC_UUID
import com.jamieadkins.droid.controller.di.ApplicationContext
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DroidConnectionManager @Inject constructor(@ApplicationContext private val context: Context) : DroidService {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var handshakeComplete = false
    val connectionStatus: BehaviorSubject<ConnectionState> = BehaviorSubject.createDefault(ConnectionState.Disconnected)

    private val commands = ConcurrentLinkedQueue<String>()
    private val compositeDisposable = CompositeDisposable()

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Timber.i("onConnectionStateChange status=$status state=$newState")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionStatus.onNext(ConnectionState.ConnectedWithoutHandshake)
                Timber.i("Connected to GATT server.")
                Timber.i("Attempting to start service discovery: ${bluetoothGatt?.discoverServices()}")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                handshakeComplete = false
                connectionStatus.onNext(ConnectionState.Disconnected)
                Timber.i("Disconnected from GATT server.")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (!handshakeComplete) {
                    performHandshake()
                    handshakeComplete = true
                    connectionStatus.onNext(ConnectionState.Connected)
                }
            } else {
                Timber.w("onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.d("onCharacteristicRead ${characteristic.uuid}")
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            Timber.d("onCharacteristicWrite ${characteristic.uuid} : $status")
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            Timber.e("onCharacteristicChanged ${characteristic.uuid} : ${characteristic.value?.toHex()}")
        }
    }

    fun initialise() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        Observable.interval(5, TimeUnit.MILLISECONDS)
            .subscribe { commands.poll()?.let(::writeCharacteristic) }
            .addToComposite(compositeDisposable)
    }

    fun onDestroy() {
        bluetoothGatt?.close()
        connectionStatus.onNext(ConnectionState.Disconnected)
        bluetoothGatt = null
        compositeDisposable.clear()
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    override fun connect(address: String): Boolean {
        if (bluetoothAdapter == null) {
            Timber.w("BluetoothAdapter not initialized or unspecified address.")
            return false
        }
        // Previously connected device.  Try to reconnect.
        if (bluetoothGatt != null) {
            Timber.d("Trying to use an existing mBluetoothGatt for connection.")
            return bluetoothGatt?.connect() ?: false
        }
        val device = bluetoothAdapter?.getRemoteDevice(address)
        if (device == null) {
            Timber.w("Device not found.  Unable to connect.")
            return false
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bluetoothGatt = device.connectGatt(context, true, mGattCallback)
        Timber.d("Trying to create a new connection.")
        return true
    }

    override fun observe(): Observable<ConnectionState> = connectionStatus

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    override fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    override fun sendCommand(droidAction: DroidAction) {
        droidAction.commands.forEach { commands.add(it) }
    }

    private fun writeCharacteristic(uuid: String, valueHex: String) {
        val characteristic = getCharacteristic(uuid)
        if (characteristic != null) {
            characteristic.value = valueHex.hexToByteArray()
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            Timber.d("Writing to $uuid")
            bluetoothGatt?.writeCharacteristic(characteristic)
        } else {
            Timber.d("Couldn't find $uuid")
        }
    }

    private fun writeCharacteristic(valueHex: String) {
        Timber.d("Writing $valueHex")
        writeCharacteristic(WRITE_CHARACTERISTIC_UUID, valueHex)
    }

    private fun getCharacteristic(uuid: String?): BluetoothGattCharacteristic? {
        bluetoothGatt?.services?.forEach { service ->
            val characteristic = service.getCharacteristic(UUID.fromString(uuid))
            if (characteristic != null) return characteristic
        }
        return null
    }

    private fun performHandshake() {
        Timber.i("performHandshake")
        try {
            sendCommand(DroidAction.Handshake)
            Thread.sleep(50)
            sendCommand(DroidAction.Handshake)
            Thread.sleep(50)
            sendCommand(DroidAction.Handshake)
            Thread.sleep(50)
            sendCommand(DroidAction.Handshake)
            Thread.sleep(50)
            sendCommand(DroidAction.Identify)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}