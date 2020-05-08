package com.jamieadkins.droid.controller.connect

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewAnimator
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieDrawable
import com.jamieadkins.droid.controller.databinding.FragmentConnectBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ConnectFragment : DaggerFragment(), ConnectContract.View {

    private var binding: FragmentConnectBinding? = null
    @Inject lateinit var presenter: ConnectContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newBinding = FragmentConnectBinding.inflate(inflater, container, false)
        binding = newBinding
        return newBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.toolbar?.let { (activity as? AppCompatActivity)?.setSupportActionBar(it) }
    }

    override fun onResume() {
        super.onResume()
        presenter.onAttach(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.onDetach()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun showLoadingIndicator() {
        binding?.animationView?.repeatCount = ValueAnimator.INFINITE
        binding?.animationView?.resumeAnimation()
    }

    override fun hideLoadingIndicator() {
        binding?.animationView?.repeatCount = 0
    }

    override fun connectToDroid(address: String) {
        binding?.status?.text = address
    }
}