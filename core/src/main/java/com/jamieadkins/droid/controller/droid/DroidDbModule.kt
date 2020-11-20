package com.jamieadkins.droid.controller.droid

import android.content.Context
import com.jamieadkins.droid.controller.di.ApplicationContext
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class DroidDbModule {

    @Provides
    @Reusable
    fun provideDb(@ApplicationContext context: Context): DroidDatabase = DroidDatabase.getInstance(context)

    @Provides
    @Reusable
    fun droidDao(db: DroidDatabase): DroidDao = db.droidDao()
}
