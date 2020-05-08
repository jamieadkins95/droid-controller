package com.jamieadkins.droid.controller.connect

interface ConnectContract {

    interface View {
        fun showScanningIndicator()
        fun hideScanningIndicator()
        fun enableScanButton()
        fun disableScanButton()
        fun showBluetoothPrompt()
        fun hideBluetoothPrompt()
        fun showLocationPrompt()
        fun hideLocationPrompt()
        fun connectToDroid(address: String)
    }

    interface Presenter {
        fun onAttach(newView: View)
        fun onDetach()
    }
}