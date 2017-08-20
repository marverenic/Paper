package com.marverenic.reader.ui.login

import android.content.Context
import android.content.Intent
import com.marverenic.reader.ui.BaseActivity

class LoginActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

}