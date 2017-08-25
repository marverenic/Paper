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
import com.marverenic.reader.ui.home.all.AllArticlesFragment
import com.marverenic.reader.ui.home.categories.CategoriesFragment
import com.marverenic.reader.utils.first
import com.marverenic.reader.utils.forEach

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    companion object {
        fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setSelectedFragment(R.id.menu_item_all_articles)

        binding.homeDrawerNavigationView.setNavigationItemSelectedListener { menuItem ->
            setSelectedFragment(menuItem.itemId)
            binding.homeDrawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

        setSupportActionBar(binding.homeContent!!.toolbar)
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

    private fun setSelectedFragment(menuItemId: Int) {
        val menu = binding.homeDrawerNavigationView.menu
        if (menu.first { it.isChecked }?.itemId == menuItemId) {
            return
        }

        menu.forEach { it.isChecked = (it.itemId == menuItemId) }

        val fragment = when (menuItemId) {
            R.id.menu_item_all_articles -> AllArticlesFragment.newInstance()
            R.id.menu_item_categories -> CategoriesFragment.newInstance()
            else -> throw IllegalArgumentException("No fragment found for $menuItemId")
        }

        supportFragmentManager.beginTransaction()
                .replace(binding.homeContent!!.homeFragmentContainer.id, fragment)
                .commit()
    }

}