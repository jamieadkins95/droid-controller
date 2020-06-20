package com.jamieadkins.droid.controller.connect

import android.animation.ValueAnimator
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jamieadkins.droid.controller.R
import com.jamieadkins.droid.controller.databinding.FragmentConnectBinding
import com.jamieadkins.droid.controller.name.NameDroidFragment
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class ConnectFragment : DaggerFragment() {

    private var binding: FragmentConnectBinding? = null

    @Inject lateinit var factory: DroidConnectViewModel.Factory
    private lateinit var viewModel: DroidConnectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, factory).get(DroidConnectViewModel::class.java)

        setFragmentResultListener("name") { key, bundle ->
            val name = bundle.getString("name") ?: ""
            val droidType = bundle.getString("type") ?: "r"
            val address = bundle.getString("address")!!
            viewModel.onDroidNamed(name, address, droidType)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentConnectBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.toolbar?.let { (activity as? AppCompatActivity)?.setSupportActionBar(it) }
        binding?.scan?.setOnClickListener { viewModel.scan() }
        viewModel.scanState.observe(viewLifecycleOwner, Observer<ConnectionState> { state ->
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
                    val dialog = NameDroidFragment().apply {
                        arguments = bundleOf("address" to state.address)
                    }
                    dialog.show(parentFragmentManager, "name")
                    disableScanButton()
                    showScanningIndicator()
                    hideBluetoothPrompt()
                    hideLocationPrompt()
                }
                is ConnectionState.Connecting -> {
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
        binding?.animationView?.repeatCount = ValueAnimator.INFINITE
        binding?.animationView?.resumeAnimation()
    }

    private fun hideScanningIndicator() {
        binding?.animationView?.repeatCount = 0
    }

    private fun enableScanButton() {
        binding?.scan?.isEnabled = true
    }

    private fun disableScanButton() {
        binding?.scan?.isEnabled = false
    }

    private fun showBluetoothPrompt() {
        binding?.bluetoothError?.apply {
            binding.text.setText(R.string.bt_reason)
            binding.button.setText(R.string.enable)
            binding.button.setOnClickListener {
                activity?.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
            visibility = View.VISIBLE
        }
    }

    private fun hideBluetoothPrompt() {
        binding?.bluetoothError?.visibility = View.GONE
    }

    private fun showLocationPrompt() {
        binding?.locationError?.apply {
            binding.text.setText(R.string.location_error)
            binding.button.setText(R.string.settings)
            binding.button.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = Uri.parse("package:${context.packageName}") }
                activity?.startActivity(intent)
            }
            visibility = View.VISIBLE
        }
    }

    private fun hideLocationPrompt() {
        binding?.locationError?.visibility = View.GONE
    }
}