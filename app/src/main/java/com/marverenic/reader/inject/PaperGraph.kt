package com.marverenic.reader.inject

import com.marverenic.reader.ui.home.HomeFragment
import com.marverenic.reader.ui.splash.SplashActivity

interface PaperGraph {

    fun inject(activity: SplashActivity)

    fun inject(fragment: HomeFragment)

}