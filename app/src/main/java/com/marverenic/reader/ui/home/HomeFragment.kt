package com.marverenic.reader.ui.home

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.marverenic.reader.ui.BaseFragment

abstract class HomeFragment : BaseFragment() {

    abstract val title: String

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (activity as? AppCompatActivity)?.supportActionBar?.title = title
    }

}