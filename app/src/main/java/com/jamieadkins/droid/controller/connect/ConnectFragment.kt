package com.jamieadkins.droid.controller.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
        presenter.onAttach(this)
    }

    override fun onDestroyView() {
        presenter.onDetach()
        binding = null
        super.onDestroyView()
    }

    override fun showLoadingIndicator() {
        binding?.animationView?.visibility = View.VISIBLE
    }

    override fun hideLoadingIndicator() {
        binding?.animationView?.visibility = View.GONE
    }
}