package com.jamieadkins.droid.controller.name

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jamieadkins.droid.controller.R
import com.jamieadkins.droid.controller.databinding.FragmentNameDroidBinding
import com.jamieadkins.droid.controller.droid.DroidType
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class NameDroidFragment : BottomSheetDialogFragment() {

    private var binding: FragmentNameDroidBinding? = null

    @Inject lateinit var factory: NameDroidViewModel.Factory
    private lateinit var viewModel: NameDroidViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         viewModel = ViewModelProvider(viewModelStore, factory).get(NameDroidViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentNameDroidBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.ok?.setOnClickListener {
            val name = binding?.input?.text?.toString() ?: ""
            val type = if (binding?.droidType?.checkedChipId == R.id.r_unit) DroidType.RUnit else DroidType.BBUnit
            val address = arguments?.getString("address")!!
            viewModel.nameDroid(address, name, type)

            setFragmentResult("name", bundleOf("success" to true, "address" to address))
            dismiss()
        }

        binding?.cancel?.setOnClickListener {
            setCancelResult()
            dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        setCancelResult()
        super.onCancel(dialog)
    }

    private fun setCancelResult() {
        val address = arguments?.getString("address")!!
        setFragmentResult("name", bundleOf("success" to false, "address" to address))
    }
}