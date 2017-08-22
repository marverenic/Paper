package com.marverenic.reader.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import com.marverenic.reader.R
import com.marverenic.reader.databinding.ActivitySingleFragmentBinding

abstract class SingleFragmentActivity : BaseActivity() {

    private lateinit var binding: ActivitySingleFragmentBinding

    private val isFragmentCreated: Boolean
        get() = supportFragmentManager.findFragmentById(binding.singleFragmentContainer.id) != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_fragment)

        if (!isFragmentCreated) {
            supportFragmentManager.beginTransaction()
                    .add(binding.singleFragmentContainer.id, onCreateFragment())
                    .commit()
        }
    }

    abstract fun onCreateFragment(): Fragment

}