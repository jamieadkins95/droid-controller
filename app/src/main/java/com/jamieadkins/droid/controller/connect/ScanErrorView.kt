package com.jamieadkins.droid.controller.connect

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.jamieadkins.droid.controller.R
import com.jamieadkins.droid.controller.databinding.ViewScanErrorBinding

class ScanErrorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    val binding = ViewScanErrorBinding.inflate(LayoutInflater.from(context), this)

    init {
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.primaryLightColor))
    }
}