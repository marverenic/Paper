package com.marverenic.reader.ui.login

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.databinding.FragmentLoginBinding
import com.marverenic.reader.ui.BaseFragment

class LoginFragment : BaseFragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(
                inflater, R.layout.fragment_login, container, false)

        binding.viewModel = LoginViewModel()
        return binding.root
    }

}