package com.bottlerocketstudios.brarchitecture.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.bottlerocketstudios.brarchitecture.R
import com.bottlerocketstudios.brarchitecture.databinding.SplashFragmentBinding
import com.bottlerocketstudios.brarchitecture.ui.BaseFragment

class SplashFragment : BaseFragment() {
    private val splashViewModel: SplashFragmentViewModel by lazy {
        getProvidedViewModel(SplashFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        return DataBindingUtil.inflate<SplashFragmentBinding>(inflater, R.layout.splash_fragment, container, false).apply {
            viewModel = splashViewModel
            setLifecycleOwner(this@SplashFragment)
            splashViewModel.authenticated.observe(this@SplashFragment, Observer { authenticated ->
                if (authenticated) {
                    findNavController(this@SplashFragment).navigate(R.id.action_splashFragment_to_homeFragment)
                } else {
                    findNavController(this@SplashFragment).navigate(R.id.action_splashFragment_to_loginFragment)
                }
            })
        }.root
    }
}
