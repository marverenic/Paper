package com.marverenic.reader.ui.home

import android.os.Bundle
import com.marverenic.reader.ReaderApplication
import com.marverenic.reader.ui.BaseFragment

class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReaderApplication.component(this).inject(this)
    }

}