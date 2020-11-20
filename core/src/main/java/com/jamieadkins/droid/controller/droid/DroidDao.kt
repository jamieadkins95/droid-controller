package com.jamieadkins.droid.controller.droid

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface DroidDao {

    @Query("SELECT * FROM droids ORDER BY name")
    fun getDroids(): LiveData<List<Droid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(droid: Droid): Completable

    @Query("SELECT COUNT(*) from droids WHERE address = :address")
    fun doesDroidExist(address: String) : Single<Int>

    @Query("SELECT * from droids WHERE address = :address")
    fun getDroid(address: String) : LiveData<Droid>
}