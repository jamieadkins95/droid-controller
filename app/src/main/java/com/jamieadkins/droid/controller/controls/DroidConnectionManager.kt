package com.jamieadkins.droid.controller.controls

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.jamieadkins.droid.controller.addToComposite
import com.jamieadkins.droid.controller.connect.ConnectionEvent
import com.jamieadkins.droid.controller.connect.ConnectionStateMachine
import com.jamieadkins.droid.controller.controls.BluetoothConstants.WRITE_CHARACTERISTIC_UUID
import com.jamieadkins.droid.controller.controls.advanced.DroidActionWithDelay
import com.jamieadkins.droid.controller.di.ApplicationContext
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DroidConnectionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stateMachine: ConnectionStateMachine
) : DroidService {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var handshakeComplete = false

    private val commands = ConcurrentLinkedQueue<String>()
    private val compositeDisposable = CompositeDisposable()
    private var sequenceDisposable: Disposable? = null

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Timber.i("onConnectionStateChange status=$status state=$newState")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                stateMachine.postEvent(ConnectionEvent.ConnectionSuccessful(gatt.device.address))
                Timber.i("Connected to GATT server.")
                Timber.i("Attempting to start service discovery: ${bluetoothGatt?.discoverServices()}")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                stateMachine.postEvent(ConnectionEvent.Disconnected)
                Timber.i("Disconnected from GATT server.")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (!handshakeComplete) {
                    performHandshake()
                    handshakeComplete = true
                    stateMachine.postEvent(ConnectionEvent.HandshakeComplete(gatt.device.address))
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
        handshakeComplete = false
        compositeDisposable.clear()
        bluetoothGatt?.close()
        stateMachine.postEvent(ConnectionEvent.Disconnected)
        bluetoothGatt = null
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

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    override fun disconnect() {
        handshakeComplete = false
        stateMachine.postEvent(ConnectionEvent.UserDisconnect)
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    override fun startSequence(actions: List<DroidActionWithDelay>) {
        sequenceDisposable?.dispose()
        sequenceDisposable = Observable.fromIterable(actions)
            .concatMapCompletable { action ->
                Completable.fromCallable { commands.add(action.command) }
                    .delay(action.delayMs, TimeUnit.MILLISECONDS)
            }
            .doOnSubscribe { stateMachine.postEvent(ConnectionEvent.SequenceStarted) }
            .doOnTerminate { stateMachine.postEvent(ConnectionEvent.SequenceEnded) }
            .doOnDispose { stateMachine.postEvent(ConnectionEvent.SequenceEnded) }
            .onErrorComplete()
            .subscribe()
    }

    override fun stopSequence() {
        sequenceDisposable?.dispose()
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