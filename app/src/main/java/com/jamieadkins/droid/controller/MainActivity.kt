package com.jamieadkins.droid.controller

import android.os.Bundle
import com.jamieadkins.droid.controller.DroidApplication.Companion.coreComponent
import com.jamieadkins.droid.controller.databinding.ActivityMainBinding
import com.jamieadkins.droid.controller.di.DaggerAndroidActivity
import com.jamieadkins.droid.controller.di.DaggerAppComponent

class MainActivity : DaggerAndroidActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onInject() {
        DaggerAppComponent.builder()
            .core(coreComponent)
            .activity(this)
            .build()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}
