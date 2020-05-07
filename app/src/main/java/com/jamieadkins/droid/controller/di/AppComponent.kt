package com.jamieadkins.droid.controller.di

import android.app.Activity
import com.jamieadkins.droid.controller.MainActivity
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        FragmentProvider::class
    ],
    dependencies = [CoreComponent::class]
)
@PerActivity
interface AppComponent {

    fun inject(target: MainActivity)

    @Component.Builder
    interface Builder {

        fun core(coreComponent: CoreComponent): Builder

        @BindsInstance
        fun activity(activity: Activity): Builder

        fun build(): AppComponent
    }
}