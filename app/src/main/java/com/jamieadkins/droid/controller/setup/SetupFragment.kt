package com.jamieadkins.droid.controller.setup

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.jamieadkins.droid.controller.databinding.FragmentSetupBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SetupFragment : DaggerFragment(), SetupContract.View {

    private var binding: FragmentSetupBinding? = null
    @Inject lateinit var presenter: SetupContract.Presenter

    private val locationPermission = registerForActivityResult(RequestPermission()) { granted -> presenter.onLocationPermissionGranted(granted) }
    private val enableBluetooth = registerForActivityResult(EnableBluetooth()) { enabled -> presenter.onBluetoothEnabled(enabled) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentSetupBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.bluetooth?.setOnClickListener { enableBluetooth.launch(Unit) }
        binding?.location?.setOnClickListener { locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
        binding?.locationSettings?.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = Uri.parse("package:${view.context.packageName}") }
            activity?.startActivity(intent)
        }
        presenter.onAttach(this)
    }

    override fun onResume() {
        super.onResume()
        checkBluetooth()
        checkLocationPermission()
    }

    override fun onDestroyView() {
        binding = null
        presenter.onDetach()
        super.onDestroyView()
    }

    override fun showBluetoothPrompt() {
        binding?.bluetooth?.isEnabled = true
    }

    override fun hideBluetoothPrompt() {
        binding?.bluetooth?.isEnabled = false
    }

    override fun showLocationPrompt() {
        binding?.location?.isEnabled = true
        binding?.locationSettings?.isEnabled = true
    }

    override fun hideLocationPrompt() {
        binding?.location?.isEnabled = false
        binding?.locationSettings?.isEnabled = false
    }

    override fun setupComplete() {
        findNavController().navigate(SetupFragmentDirections.setupToConnect())
    }

    private fun checkBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        presenter.onBluetoothEnabled(bluetoothAdapter.isEnabled)
    }

    private fun checkLocationPermission() {
        val granted = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        presenter.onLocationPermissionGranted(granted)
    }
}