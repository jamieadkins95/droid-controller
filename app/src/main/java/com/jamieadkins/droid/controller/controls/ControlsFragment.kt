package com.jamieadkins.droid.controller.controls

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.navArgs
import com.jamieadkins.droid.controller.R
import com.jamieadkins.droid.controller.databinding.FragmentControlsBinding
import dagger.android.support.DaggerFragment

class ControlsFragment : DaggerFragment() {

    private val args: ControlsFragmentArgs by navArgs()
    private var binding: FragmentControlsBinding? = null
    private var droidService: DroidBluetoothLeService.DroidServiceBinder? = null

    private val joystickConstraints = ConstraintSet()
    private val buttonsConstraints = ConstraintSet()

    init {
        setHasOptionsMenu(true)
    }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.controls, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.toolbar?.let { (activity as? AppCompatActivity)?.setSupportActionBar(it) }
        binding?.identify?.setOnClickListener { droidService?.sendCommand(DroidAction.Identify) }
        binding?.blaster?.setOnClickListener {
            droidService?.sendCommand(DroidAction.BlasterSound)
        }
        binding?.volume?.addOnChangeListener { _, value, _ -> droidService?.sendCommand(DroidAction.Volume(value.toInt())) }
        binding?.forward?.setOnTouchListener { _, event ->
            onButtonTouch(event, DroidAction.Forward(binding?.speed?.value?.toInt() ?: 0), DroidAction.Forward(0))
        }
        binding?.backwards?.setOnTouchListener { _, event ->
            onButtonTouch(event, DroidAction.Backwards(binding?.speed?.value?.toInt() ?: 0), DroidAction.Backwards(0))
        }
        binding?.headLeft?.setOnTouchListener { _, event ->
            onButtonTouch(event, DroidAction.HeadLeft(binding?.speed?.value?.toInt() ?: 0), DroidAction.HeadLeft(0))
        }
        binding?.headRight?.setOnTouchListener { _, event ->
            onButtonTouch(event, DroidAction.HeadRight(binding?.speed?.value?.toInt() ?: 0), DroidAction.HeadRight(0))
        }
        binding?.left?.setOnTouchListener { _, event ->
            onButtonTouch(event, DroidAction.Left(binding?.speed?.value?.toInt() ?: 0), DroidAction.Left(0))
        }
        binding?.right?.setOnTouchListener { _, event ->
            onButtonTouch(event, DroidAction.Right(binding?.speed?.value?.toInt() ?: 0), DroidAction.Right(0))
        }

        joystickConstraints.clone(binding?.constraintLayout)
        buttonsConstraints.clone(binding?.constraintLayout)
        setupButtonConstraints(buttonsConstraints)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_joystick -> {
                item.isChecked = !item.isChecked
                binding?.constraintLayout?.let(joystickConstraints::applyTo)
                true
            }
            R.id.menu_buttons -> {
                item.isChecked = !item.isChecked
                binding?.constraintLayout?.let(buttonsConstraints::applyTo)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun setupButtonConstraints(set: ConstraintSet) {
        set.connect(R.id.head_left, ConstraintSet.TOP, R.id.forward, ConstraintSet.TOP)
        set.connect(R.id.head_left, ConstraintSet.BOTTOM, R.id.forward, ConstraintSet.BOTTOM)
        set.setVisibility(R.id.button_controls, View.VISIBLE)
        set.setVisibility(R.id.joystick, View.GONE)
    }

    private fun onButtonTouch(event: MotionEvent, downAction: DroidAction, upAction: DroidAction): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> droidService?.sendCommand(downAction)
            MotionEvent.ACTION_UP -> droidService?.sendCommand(upAction)
        }
        return true
    }
}