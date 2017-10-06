package com.marverenic.reader.ui.login

import android.content.Context
import android.databinding.BaseObservable
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import com.marverenic.reader.R
import com.marverenic.reader.data.AuthenticationManager
import com.marverenic.reader.utils.resolveIntAttr

class LoginViewModel(
        private val context: Context,
        private val authManager: AuthenticationManager
) : BaseObservable() {

    fun onClickLogin() {
        val intent = CustomTabsIntent.Builder()
                .setToolbarColor(context.resolveIntAttr(R.attr.colorPrimary))
                .build()

        intent.launchUrl(context, Uri.parse(authManager.loginUrl))
    }

}
