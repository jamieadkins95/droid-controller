package com.jamieadkins.droid.controller.droid

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Droid::class], version = 1, exportSchema = false)
@TypeConverters(DroidTypeConverter::class)
abstract class DroidDatabase : RoomDatabase() {

    abstract fun droidDao(): DroidDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: DroidDatabase? = null

        fun getInstance(context: Context): DroidDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): DroidDatabase {
            return Room.databaseBuilder(context, DroidDatabase::class.java, "droid_db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}