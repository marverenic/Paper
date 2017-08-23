package com.marverenic.reader.inject

import com.marverenic.reader.ui.home.all.AllArticlesFragment
import com.marverenic.reader.ui.home.categories.CategoriesFragment
import com.marverenic.reader.ui.splash.SplashActivity
import com.marverenic.reader.ui.stream.StreamFragment

interface PaperGraph {

    fun inject(activity: SplashActivity)

    fun inject(fragment: CategoriesFragment)
    fun inject(fragment: AllArticlesFragment)
    fun inject(fragment: StreamFragment)

}