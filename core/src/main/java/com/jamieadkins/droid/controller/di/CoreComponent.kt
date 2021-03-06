package com.jamieadkins.droid.controller.di

import android.content.Context
import com.jamieadkins.droid.controller.DroidApplication
import com.jamieadkins.droid.controller.connect.ConnectionStateMachine
import com.jamieadkins.droid.controller.controls.DroidConnectionManager
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [StateMachineModule::class])
@Singleton
interface CoreComponent {

    @ApplicationContext
    fun exposeContext(): Context

    fun exposeDroidManager(): DroidConnectionManager

    fun exposeStateMachine(): ConnectionStateMachine

    fun inject(target: DroidApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(@ApplicationContext context: Context): Builder

        fun build(): CoreComponent
    }
}