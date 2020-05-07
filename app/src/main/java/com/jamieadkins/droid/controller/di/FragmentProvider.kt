package com.jamieadkins.droid.controller.di

import com.jamieadkins.droid.controller.connect.ConnectFragment
import com.jamieadkins.droid.controller.connect.ConnectModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentProvider {

    @ContributesAndroidInjector(modules = [ConnectModule::class])
    abstract fun connect(): ConnectFragment

}
