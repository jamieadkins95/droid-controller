package com.jamieadkins.droid.controller.controls.advanced

import android.content.Context
import android.net.Uri
import com.jamieadkins.droid.controller.di.ApplicationContext
import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class FileImporter @Inject constructor(@ApplicationContext private val context: Context) {

    fun readTextFile(uri: Uri): Single<List<String>> {
        return Single.fromCallable {
            val csvFile = context.contentResolver.openInputStream(uri)
            val isr = InputStreamReader(csvFile)
            BufferedReader(isr).readLines()
        }
    }
}