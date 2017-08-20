package com.marverenic.reader.ui.home

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.marverenic.reader.R
import com.marverenic.reader.databinding.ActivityHomeBinding
import com.marverenic.reader.ui.BaseActivity
import com.marverenic.reader.ui.home.categories.CategoriesFragment

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    companion object {
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        supportFragmentManager.beginTransaction()
                .add(binding.homeFragmentContainer.id, CategoriesFragment.newInstance())
                .commit()

        setSupportActionBar(binding.toolbar)
    }

}