package com.jamieadkins.droid.controller

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.jamieadkins.droid.controller.DroidApplication.Companion.coreComponent
import com.jamieadkins.droid.controller.databinding.ActivityMainBinding
import com.jamieadkins.droid.controller.di.DaggerAndroidActivity
import com.jamieadkins.droid.controller.di.DaggerAppComponent
import javax.inject.Inject

class MainActivity : DaggerAndroidActivity() {

    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var factory: DroidInitialisationViewModel.Factory
    private lateinit var viewModel: DroidInitialisationViewModel

    override fun onInject() {
        DaggerAppComponent.builder()
            .core(coreComponent)
            .activity(this)
            .build()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(DroidInitialisationViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}
