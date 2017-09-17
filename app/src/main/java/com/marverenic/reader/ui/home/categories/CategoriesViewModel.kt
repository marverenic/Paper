package com.marverenic.reader.ui.home.categories

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.marverenic.reader.BR
import com.marverenic.reader.R
import com.marverenic.reader.model.Category
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class CategoriesViewModel(context: Context,
                          categories: List<Category>? = null)
    : BaseObservable() {

    var categories: List<Category>? = categories
        set(value) {
            adapter.categories = value.orEmpty()
            field = value
        }

    var refreshing: Boolean = (categories == null)
        set(value) {
            if (value != field) {
                refreshSubject.onNext(value)
                field = value
                notifyPropertyChanged(BR.refreshing)
            }
        }
        @Bindable get() = field

    private val refreshSubject = BehaviorSubject.createDefault(refreshing)

    val adapter: CategoryAdapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        CategoryAdapter().also {
            it.categories = categories.orEmpty()
        }
    }

    val layoutManager = LinearLayoutManager(context)

    val swipeRefreshColors = intArrayOf(
            ContextCompat.getColor(context, R.color.colorPrimary),
            ContextCompat.getColor(context, R.color.colorPrimaryDark),
            ContextCompat.getColor(context, R.color.colorAccent)
    )

    fun getRefreshObservable(): Observable<Boolean> = refreshSubject

}