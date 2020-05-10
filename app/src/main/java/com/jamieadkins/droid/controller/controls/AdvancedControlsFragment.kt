package com.jamieadkins.droid.controller.controls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jamieadkins.droid.controller.databinding.FragmentAdvancedControlsBinding
import com.jamieadkins.droid.controller.databinding.FragmentControlsBinding

class AdvancedControlsFragment : BottomSheetDialogFragment() {

    private var binding: FragmentAdvancedControlsBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentAdvancedControlsBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }
}