package com.marverenic.reader.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class ToolbarFragment : BaseFragment() {

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                    savedInstanceState: Bundle?): View? {
        return onCreateContentView(inflater, container!!, savedInstanceState)
    }

    abstract fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup,
                                     savedInstanceState: Bundle?): View

}