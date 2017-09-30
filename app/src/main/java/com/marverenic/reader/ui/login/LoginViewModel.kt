package com.marverenic.reader.ui.login

import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.net.Uri
import com.marverenic.reader.data.AuthenticationManager

class LoginViewModel(
        private val context: Context,
        private val authManager: AuthenticationManager
) : BaseObservable() {

    fun onClickLogin() {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(authManager.loginUrl)))
    }

}
