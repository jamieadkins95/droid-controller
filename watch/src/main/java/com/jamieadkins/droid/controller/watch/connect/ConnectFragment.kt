package com.jamieadkins.droid.controller.watch.connect

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jamieadkins.droid.controller.connect.ConnectionState
import com.jamieadkins.droid.controller.connect.DroidConnectViewModel
import com.jamieadkins.droid.controller.droid.Droid
import com.jamieadkins.droid.controller.droid.DroidDao
import com.jamieadkins.droid.controller.droid.DroidType
import com.jamieadkins.droid.controller.setup.EnableBluetooth
import com.jamieadkins.droid.controller.watch.R
import com.jamieadkins.droid.controller.watch.databinding.FragmentConnectBinding
import dagger.android.support.DaggerFragment
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ConnectFragment : DaggerFragment() {

    private val locationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* do nothing */ }
    private val enableBluetooth = registerForActivityResult(EnableBluetooth()) { /* do nothing */ }

    private var binding: FragmentConnectBinding? = null

    @Inject lateinit var factory: DroidConnectViewModel.Factory
    private lateinit var viewModel: DroidConnectViewModel

    @Inject lateinit var droidDao: DroidDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, factory).get(DroidConnectViewModel::class.java)

        setFragmentResultListener("name") { key, bundle ->
            val successfullyNamed = bundle.getBoolean("success")
            if (successfullyNamed) {
                val address = bundle.getString("address")!!
                viewModel.onDroidNamed(address)
            } else {
                viewModel.onDroidNamingCancelled()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentConnectBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.scan?.setOnClickListener { viewModel.scan() }
        viewModel.scanState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ConnectionState.Disconnected -> {
                    binding?.scan?.setText(R.string.scan_for_droids)
                    enableScanButton()
                    hideScanningIndicator()
                    if (state.bluetoothDisabled) showBluetoothPrompt() else hideBluetoothPrompt()
                    if (state.locationDisabled) showLocationPrompt() else hideLocationPrompt()
                }
                ConnectionState.Scanning -> {
                    binding?.scan?.setText(R.string.scanning)
                    disableScanButton()
                    showScanningIndicator()
                    hideBluetoothPrompt()
                    hideLocationPrompt()
                }
                is ConnectionState.Naming -> {
//                    val dialog = NameDroidFragment().apply {
//                        arguments = bundleOf("address" to state.address)
//                    }
//                    dialog.show(parentFragmentManager, "name")
//                    disableScanButton()
//                    showScanningIndicator()
//                    hideBluetoothPrompt()
//                    hideLocationPrompt()

                    droidDao.insert(Droid(state.address, "DROID", DroidType.RUnit)).subscribeOn(Schedulers.io()).onErrorComplete().subscribe()
                    viewModel.onDroidNamed(state.address)
                }
                is ConnectionState.Connecting,
                is ConnectionState.ConnectedWithoutHandshake -> {
                    binding?.scan?.setText(R.string.connecting)
                    disableScanButton()
                    showScanningIndicator()
                    hideBluetoothPrompt()
                    hideLocationPrompt()
                }
                is ConnectionState.Connected -> {
                    binding?.scan?.setText(R.string.connected)
                    disableScanButton()
                    hideScanningIndicator()
                    hideBluetoothPrompt()
                    hideLocationPrompt()

                    findNavController().navigate(ConnectFragmentDirections.toControls())
                }
            }
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun showScanningIndicator() {
        // Do nothing
    }

    private fun hideScanningIndicator() {
        // Do nothing
    }

    private fun enableScanButton() {
        binding?.scan?.isEnabled = true
    }

    private fun disableScanButton() {
        binding?.scan?.isEnabled = false
    }

    private fun showBluetoothPrompt() {
        enableBluetooth.launch(Unit)
    }

    private fun hideBluetoothPrompt() {
        // do nothing.
    }

    private fun showLocationPrompt() {
        locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun hideLocationPrompt() {
        //do nothing.
    }
}