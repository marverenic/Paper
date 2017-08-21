package com.marverenic.reader.ui.home.categories

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import com.marverenic.reader.BR
import com.marverenic.reader.model.Category
import com.marverenic.reader.ui.stream.StreamActivity

class CategoryViewModel(val context: Context, category: Category) : BaseObservable() {

    var category: Category = category
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }

    val title: String
        @Bindable get() = category.label

    fun openCategory() {
        context.startActivity(StreamActivity.newIntent(context, category.label, category.id))
    }

}
