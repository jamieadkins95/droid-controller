package com.jamieadkins.droid.controller.di

import com.jamieadkins.droid.controller.connect.ConnectFragment
import com.jamieadkins.droid.controller.connect.ConnectModule
import com.jamieadkins.droid.controller.controls.ControlsFragment
import com.jamieadkins.droid.controller.setup.SetupFragment
import com.jamieadkins.droid.controller.setup.SetupModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentProvider {

    @ContributesAndroidInjector(modules = [ConnectModule::class])
    abstract fun connect(): ConnectFragment

    @ContributesAndroidInjector(modules = [SetupModule::class])
    abstract fun setup(): SetupFragment

    @ContributesAndroidInjector
    abstract fun controls(): ControlsFragment


}
