package com.marverenic.reader.ui.home

import android.content.Context
import android.content.Intent
import com.marverenic.reader.ui.SingleFragmentActivity

class HomeActivity : SingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }

    override fun onCreateFragment() = HomeFragment.newInstance()

}