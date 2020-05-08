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
import androidx.navigation.fragment.findNavController
import com.jamieadkins.droid.controller.DeviceControlActivity
import com.jamieadkins.droid.controller.R
import com.jamieadkins.droid.controller.databinding.FragmentConnectBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ConnectFragment : DaggerFragment(), ConnectContract.View {

    private var binding: FragmentConnectBinding? = null
    @Inject lateinit var presenter: ConnectContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentConnectBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.toolbar?.let { (activity as? AppCompatActivity)?.setSupportActionBar(it) }
    }

    override fun onResume() {
        super.onResume()
        presenter.onAttach(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.onDetach()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun showScanningIndicator() {
        binding?.animationView?.repeatCount = ValueAnimator.INFINITE
        binding?.animationView?.resumeAnimation()
        binding?.scan?.setText(R.string.scanning)
    }

    override fun hideScanningIndicator() {
        binding?.animationView?.repeatCount = 0
        binding?.scan?.setText(R.string.scan_for_droids)
    }

    override fun enableScanButton() {
        binding?.scan?.isEnabled = true
    }

    override fun disableScanButton() {
        binding?.scan?.isEnabled = false
    }

    override fun showBluetoothPrompt() {
        binding?.bluetoothError?.apply {
            binding.text.setText(R.string.bt_reason)
            binding.button.setText(R.string.enable)
            binding.button.setOnClickListener {
                activity?.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
            visibility = View.VISIBLE
        }
    }

    override fun hideBluetoothPrompt() {
        binding?.bluetoothError?.visibility = View.GONE
    }

    override fun showLocationPrompt() {
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

    override fun hideLocationPrompt() {
        binding?.locationError?.visibility = View.GONE
    }

    override fun connectToDroid(address: String) {
        findNavController().navigate(ConnectFragmentDirections.toControls(address))
    }
}