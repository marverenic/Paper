package com.marverenic.reader.ui.home.categories

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import com.marverenic.reader.model.Category

class CategoriesViewModel(context: Context) {

    var categories: List<Category> = emptyList()
        set(value) {
            adapter.categories = value
            field = value
        }

    val adapter: CategoryAdapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        CategoryAdapter().also {
            it.categories = categories
        }
    }

    val layoutManager = LinearLayoutManager(context)

}