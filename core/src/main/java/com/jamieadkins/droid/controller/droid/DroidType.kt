package com.jamieadkins.droid.controller.droid

sealed class DroidType {

    object RUnit : DroidType()

    object BBUnit : DroidType()
}