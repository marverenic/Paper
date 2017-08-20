package com.marverenic.reader.ui.home.categories

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.marverenic.reader.BR
import com.marverenic.reader.model.Category

class CategoryViewModel(category: Category) : BaseObservable() {

    var category: Category = category
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }

    val title: String
        @Bindable get() = category.label

}
