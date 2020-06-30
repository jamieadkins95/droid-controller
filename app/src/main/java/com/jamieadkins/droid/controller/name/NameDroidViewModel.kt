package com.jamieadkins.droid.controller.name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamieadkins.droid.controller.droid.Droid
import com.jamieadkins.droid.controller.droid.DroidDao
import com.jamieadkins.droid.controller.droid.DroidType
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Provider

class NameDroidViewModel(
    private val droidDao: DroidDao
) : ViewModel() {

    fun nameDroid(address: String, name: String, type: DroidType) {
        droidDao.insert(Droid(address, name, type)).subscribeOn(Schedulers.io()).onErrorComplete().subscribe()
    }

    class Factory @Inject constructor(
        private val droidDao: Provider<DroidDao>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NameDroidViewModel(droidDao.get()) as T
        }
    }

}