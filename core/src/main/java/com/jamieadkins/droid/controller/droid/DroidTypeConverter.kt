package com.jamieadkins.droid.controller.droid

import androidx.room.TypeConverter

class DroidTypeConverter {

    @TypeConverter
    fun fromString(value: String): DroidType {
        return when (value) {
            "r" -> DroidType.RUnit
            "bb" -> DroidType.BBUnit
            else -> DroidType.RUnit
        }
    }

    @TypeConverter
    fun fromType(type: DroidType): String {
        return when (type) {
            DroidType.RUnit -> "r"
            DroidType.BBUnit -> "bb"
        }
    }

}