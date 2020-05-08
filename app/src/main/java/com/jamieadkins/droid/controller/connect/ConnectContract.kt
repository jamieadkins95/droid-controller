package com.jamieadkins.droid.controller.connect

interface ConnectContract {

    interface View {
        fun showLoadingIndicator()
        fun hideLoadingIndicator()
        fun connectToDroid(address: String)
    }

    interface Presenter {
        fun onAttach(newView: View)
        fun onDetach()
    }
}