package com.jamieadkins.droid.controller.controls.advanced

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jamieadkins.droid.controller.R
import com.jamieadkins.droid.controller.connect.ConnectionState
import com.jamieadkins.droid.controller.databinding.FragmentAdvancedControlsBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class AdvancedControlsFragment : BottomSheetDialogFragment() {

    private val importFromFile = registerForActivityResult(ImportCsv()) { data ->
        data?.also {
            fileImporter.readTextFile(it)
                .subscribe { lines ->
                    binding?.input?.setText(lines.joinToString("\n"))
                }
        }
    }

    private var binding: FragmentAdvancedControlsBinding? = null

    @Inject lateinit var factory: DroidAdvancedControlsViewModel.Factory
    @Inject lateinit var inputParser: CommandInputParser
    @Inject lateinit var fileImporter: FileImporter
    private lateinit var viewModel: DroidAdvancedControlsViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, factory).get(DroidAdvancedControlsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentAdvancedControlsBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.connectionState.observe(viewLifecycleOwner, Observer<ConnectionState> { state ->
            when (state) {
                is ConnectionState.Disconnected -> dismiss()
                is ConnectionState.ConnectedWithoutHandshake -> {
                    binding?.start?.isEnabled = false
                    binding?.start?.setText(R.string.start)
                    binding?.cancel?.visibility = View.GONE
                }
                is ConnectionState.Connected -> {
                    binding?.start?.isEnabled = !state.doingSequencePlayback
                    binding?.start?.setText(if (state.doingSequencePlayback) R.string.playing else R.string.start)
                    binding?.cancel?.visibility = if (state.doingSequencePlayback) View.VISIBLE else View.GONE
                }
            }
        })

        binding?.start?.setOnClickListener {
            val text = binding?.input?.text?.toString() ?: ""
            viewModel.startSequence(inputParser.parseInput(text))
        }

        binding?.cancel?.setOnClickListener {
            viewModel.stopSequence()
        }

        binding?.importFromFile?.setOnClickListener { importFromFile.launch(Unit) }
    }
}