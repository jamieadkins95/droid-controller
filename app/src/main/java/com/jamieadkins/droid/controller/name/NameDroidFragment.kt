package com.jamieadkins.droid.controller.name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jamieadkins.droid.controller.R
import com.jamieadkins.droid.controller.databinding.FragmentNameDroidBinding

class NameDroidFragment : BottomSheetDialogFragment() {

    private var binding: FragmentNameDroidBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentNameDroidBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.ok?.setOnClickListener {
            val name = binding?.input?.text?.toString() ?: ""
            val type = if (binding?.droidType?.checkedChipId == R.id.r_unit) "r" else "bb"

            setFragmentResult("name", bundleOf("name" to name, "type" to type, "address" to arguments?.getString("address")!!))
            dismiss()
        }

        binding?.cancel?.setOnClickListener { dismiss() }
    }
}