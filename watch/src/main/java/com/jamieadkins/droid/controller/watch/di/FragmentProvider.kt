package com.jamieadkins.droid.controller.watch.di

import com.jamieadkins.droid.controller.connect.ConnectModule
import com.jamieadkins.droid.controller.droid.DroidDbModule
import com.jamieadkins.droid.controller.watch.connect.ConnectFragment
import com.jamieadkins.droid.controller.watch.controls.ControlsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentProvider {

    @ContributesAndroidInjector(modules = [ConnectModule::class, DroidDbModule::class])
    abstract fun connect(): ConnectFragment

    @ContributesAndroidInjector(modules = [DroidDbModule::class])
    abstract fun controls(): ControlsFragment

}
