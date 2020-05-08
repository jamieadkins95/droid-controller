package com.jamieadkins.droid.controller.controls

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.jamieadkins.droid.controller.BluetoothLeService
import com.jamieadkins.droid.controller.databinding.FragmentControlsBinding
import dagger.android.support.DaggerFragment
import timber.log.Timber

class ControlsFragment : DaggerFragment() {

    private val args: ControlsFragmentArgs by navArgs()
    private var binding: FragmentControlsBinding? = null
    private var droidService: DroidBluetoothLeService.DroidServiceBinder? = null

    // Code to manage Service lifecycle.
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            droidService = service as DroidBluetoothLeService.DroidServiceBinder

            // Automatically connects to the device upon successful start-up initialization.
            droidService?.connect(args.address)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            droidService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(requireContext(), DroidBluetoothLeService::class.java)
        requireActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentControlsBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.toolbar?.let { (activity as? AppCompatActivity)?.setSupportActionBar(it) }
        binding?.identify?.setOnClickListener { droidService?.sendCommand(DroidAction.Identify) }
        binding?.blaster?.setOnClickListener { droidService?.sendCommand(DroidAction.BlasterSound); droidService?.sendCommand(DroidAction.PlaySound); }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unbindService(serviceConnection)
        droidService = null
    }
}