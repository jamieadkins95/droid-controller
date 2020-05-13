package com.jamieadkins.droid.controller.controls

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jamieadkins.droid.controller.R
import com.jamieadkins.droid.controller.controls.advanced.AdvancedControlsFragment
import com.jamieadkins.droid.controller.databinding.FragmentControlsBinding
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ControlsFragment : DaggerFragment() {

    private var binding: FragmentControlsBinding? = null
    @Inject lateinit var factory: DroidConnectionViewModel.Factory
    private lateinit var viewModel: DroidConnectionViewModel
    private var compositeDisposable = CompositeDisposable()

    private val joystickConstraints = ConstraintSet()
    private val buttonsConstraints = ConstraintSet()

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, factory).get(DroidConnectionViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentControlsBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.controls, menu)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.connectionState.observe(viewLifecycleOwner, Observer<ConnectionState> { state ->
            if (state is ConnectionState.Disconnected) {
                findNavController().navigate(ControlsFragmentDirections.toScan())
            }
        })

        binding?.toolbar?.let {
            val activity = activity as? AppCompatActivity
            activity?.setSupportActionBar(it)
            activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_24)
            activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.identify?.setOnClickListener { viewModel.sendCommand(DroidAction.Identify) }
        binding?.blaster?.setOnClickListener { viewModel.sendCommand(DroidAction.BlasterSound) }
        binding?.reaction1?.setOnClickListener { viewModel.sendCommand(DroidAction.Reaction(1)) }
        binding?.reaction2?.setOnClickListener { viewModel.sendCommand(DroidAction.Reaction(2)) }
        binding?.reaction3?.setOnClickListener { viewModel.sendCommand(DroidAction.Reaction(3)) }
        binding?.reaction4?.setOnClickListener { viewModel.sendCommand(DroidAction.Reaction(4)) }
        binding?.reaction5?.setOnClickListener { viewModel.sendCommand(DroidAction.Reaction(5)) }
        binding?.reaction6?.setOnClickListener { viewModel.sendCommand(DroidAction.Reaction(6)) }
        binding?.reaction7?.setOnClickListener { viewModel.sendCommand(DroidAction.Reaction(7)) }
        binding?.reaction8?.setOnClickListener { viewModel.sendCommand(DroidAction.Reaction(8)) }
        binding?.volume?.addOnChangeListener { _, value, _ -> viewModel.sendCommand(DroidAction.Volume(value.toInt())) }
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
        // Show button controls by default for now. Joystick is WIP
        binding?.constraintLayout?.let(buttonsConstraints::applyTo)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.disconnect()
                findNavController().navigate(ControlsFragmentDirections.toScan())
                true
            }
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
            R.id.menu_advanced -> {
                val dialog = AdvancedControlsFragment()
                dialog.show(requireActivity().supportFragmentManager, "advanced")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupButtonConstraints(set: ConstraintSet) {
        set.connect(R.id.head_left, ConstraintSet.TOP, R.id.forward, ConstraintSet.TOP)
        set.connect(R.id.head_left, ConstraintSet.BOTTOM, R.id.forward, ConstraintSet.BOTTOM)
        set.setVisibility(R.id.button_controls, View.VISIBLE)
        set.setVisibility(R.id.joystick, View.GONE)
    }

    private fun onButtonTouch(event: MotionEvent, downAction: DroidAction, upAction: DroidAction): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> viewModel?.sendCommand(downAction)
            MotionEvent.ACTION_UP -> viewModel?.sendCommand(upAction)
        }
        return true
    }
}