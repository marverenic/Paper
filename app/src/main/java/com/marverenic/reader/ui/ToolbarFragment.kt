package com.marverenic.reader.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.databinding.FragmentToolbarBinding

abstract class ToolbarFragment : BaseFragment() {

    abstract val title: String

    private lateinit var binding: FragmentToolbarBinding

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                    savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_toolbar, container, false)
        binding.contentView.let {
            it.addView(onCreateContentView(inflater, it, savedInstanceState))
        }

        binding.toolbar.title = title
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)

        return binding.root
    }

    abstract fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup,
                                     savedInstanceState: Bundle?): View

}