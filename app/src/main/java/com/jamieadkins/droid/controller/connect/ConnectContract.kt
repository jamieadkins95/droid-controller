package com.jamieadkins.droid.controller.connect

interface ConnectContract {

    interface View {
        fun showLoadingIndicator()
        fun hideLoadingIndicator()
    }

    interface Presenter {
        fun onAttach(newView: View)
        fun onDetach()
    }
}