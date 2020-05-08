package com.jamieadkins.droid.controller.setup

interface SetupContract {

    interface View {
        fun showBluetoothPrompt()
        fun hideBluetoothPrompt()
        fun showLocationPrompt()
        fun hideLocationPrompt()
        fun setupComplete()
    }

    interface Presenter {
        fun onAttach(newView: View)
        fun onDetach()
        fun onLocationPermissionGranted(granted: Boolean)
        fun onBluetoothEnabled(enabled: Boolean)
    }
}