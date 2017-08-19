package com.marverenic.reader.ui.splash

import android.os.Bundle
import com.marverenic.reader.ui.BaseActivity
import com.marverenic.reader.ui.home.HomeActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO show login prompt to auth with Feedly

        startActivity(HomeActivity.newIntent(this))
        finish()
    }

}