package com.jamieadkins.droid.controller.setup

import dagger.Binds
import dagger.Module

@Module
abstract class SetupModule {

    @Binds
    abstract fun presenter(presenter: SetupPresenter): SetupContract.Presenter

}
