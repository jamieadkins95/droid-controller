package com.jamieadkins.droid.controller.di

import com.jamieadkins.droid.controller.connect.ConnectionStateMachine
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StateMachineModule {

    @Provides
    @Singleton
    fun provideStateMachine() = ConnectionStateMachine()
}
