package com.jamieadkins.droid.controller

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addToComposite(compositeDisposable: CompositeDisposable): Disposable {
    compositeDisposable.add(this)
    return this
}