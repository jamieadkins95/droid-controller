package com.jamieadkins.droid.controller.watch.controls

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jamieadkins.droid.controller.connect.ConnectionState
import com.jamieadkins.droid.controller.controls.DroidAction
import com.jamieadkins.droid.controller.controls.DroidConnectionViewModel
import com.jamieadkins.droid.controller.watch.databinding.FragmentControlsBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ControlsFragment : DaggerFragment() {

    private var binding: FragmentControlsBinding? = null
    @Inject lateinit var factory: DroidConnectionViewModel.Factory
    private lateinit var viewModel: DroidConnectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, factory).get(DroidConnectionViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentControlsBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.connectionState.observe(viewLifecycleOwner, Observer<ConnectionState> { state ->
            if (state is ConnectionState.Disconnected) {
                findNavController().navigate(ControlsFragmentDirections.toScan())
            }
        })

        binding?.identify?.setOnClickListener { viewModel.sendCommand(DroidAction.Identify) }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}