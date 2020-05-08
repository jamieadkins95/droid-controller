package com.jamieadkins.droid.controller.connect

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.jamieadkins.droid.controller.databinding.FragmentConnectBinding
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class ConnectFragment : DaggerFragment(), ConnectContract.View {

    private var binding: FragmentConnectBinding? = null
    @Inject lateinit var presenter: ConnectContract.Presenter

    private var bluetoothAdapter: BluetoothAdapter? = null

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            val device = result?.device
            Timber.d("Device Name: ${device?.name} Device Address: ${device?.address}")
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.e("Scan Failed: $errorCode")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothAdapter = getSystemService(requireContext(), BluetoothManager::class.java)?.adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentConnectBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.toolbar?.let { (activity as? AppCompatActivity)?.setSupportActionBar(it) }
        presenter.onAttach(this)
    }

    override fun onResume() {
        super.onResume()
        val filters = listOf(
            ScanFilter.Builder().setDeviceName("DROID").build()
        )
        val settings = ScanSettings.Builder().setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH).build()
        bluetoothAdapter?.bluetoothLeScanner?.startScan(filters, settings, scanCallback)
    }

    override fun onPause() {
        super.onPause()
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
    }

    override fun onDestroyView() {
        presenter.onDetach()
        binding = null
        super.onDestroyView()
    }

    override fun showLoadingIndicator() {
        binding?.animationView?.visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        binding?.animationView?.visibility = View.GONE
    }
}