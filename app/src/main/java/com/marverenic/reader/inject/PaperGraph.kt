package com.marverenic.reader.inject

import com.marverenic.reader.ui.home.categories.CategoriesFragment
import com.marverenic.reader.ui.splash.SplashActivity

interface PaperGraph {

    fun inject(activity: SplashActivity)

    fun inject(fragment: CategoriesFragment)

}