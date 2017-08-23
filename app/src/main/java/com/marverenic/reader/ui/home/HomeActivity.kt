package com.marverenic.reader.ui.home

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.view.GravityCompat
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
        val content = binding.homeContent!!

        supportFragmentManager.beginTransaction()
                .add(content.homeFragmentContainer.id, CategoriesFragment.newInstance())
                .commit()

        setSupportActionBar(content.toolbar)
        supportActionBar?.let {
            it.setHomeAsUpIndicator(R.drawable.ic_menu_24dp)
            it.setDisplayHomeAsUpEnabled(true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        binding.homeDrawerLayout.openDrawer(GravityCompat.START)
        return true
    }

}