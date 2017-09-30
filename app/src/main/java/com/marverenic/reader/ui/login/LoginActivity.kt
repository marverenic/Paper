package com.marverenic.reader.ui.login

import android.content.Context
import android.content.Intent
import com.marverenic.reader.ui.SingleFragmentActivity

class LoginActivity : SingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override fun onCreateFragment() = LoginFragment.newInstance()

}