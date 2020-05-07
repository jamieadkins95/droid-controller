package com.jamieadkins.droid.controller.connect

import dagger.Binds
import dagger.Module

@Module
abstract class ConnectModule {

    @Binds
    abstract fun presenter(presenter: ConnectPresenter): ConnectContract.Presenter

}
