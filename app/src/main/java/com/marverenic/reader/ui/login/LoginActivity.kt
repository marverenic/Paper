package com.marverenic.reader.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.marverenic.reader.ReaderApplication
import com.marverenic.reader.data.AuthenticationManager
import com.marverenic.reader.ui.SingleFragmentActivity
import com.marverenic.reader.ui.home.HomeActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginActivity : SingleFragmentActivity() {

    @Inject lateinit var authManager: AuthenticationManager

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReaderApplication.component(this).inject(this)
        onNewIntent(intent)
    }

    override fun onCreateFragment() = LoginFragment.newInstance()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.toString()?.takeIf { it.startsWith(authManager.redirectUrlPrefix) }?.let {
            authManager.logIn(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { loggedIn ->
                        if (loggedIn) {
                            startActivity(HomeActivity.newIntent(this).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            })
                            finishAffinity()
                        } else {
                            // TODO show error message
                        }
                    }
        }
    }

}