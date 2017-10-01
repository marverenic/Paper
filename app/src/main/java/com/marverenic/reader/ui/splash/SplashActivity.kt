package com.marverenic.reader.ui.splash

import android.os.Bundle
import com.marverenic.reader.ReaderApplication
import com.marverenic.reader.data.AuthenticationManager
import com.marverenic.reader.ui.BaseActivity
import com.marverenic.reader.ui.home.HomeActivity
import com.marverenic.reader.ui.login.LoginActivity
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    @Inject lateinit var authManager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReaderApplication.component(this).inject(this)

        authManager.isLoggedIn()
                .subscribe{ loggedIn ->
                    if (loggedIn) {
                        startActivity(HomeActivity.newIntent(this))
                    } else {
                        startActivity(LoginActivity.newIntent(this))
                    }
                    finish()
                }

    }

}