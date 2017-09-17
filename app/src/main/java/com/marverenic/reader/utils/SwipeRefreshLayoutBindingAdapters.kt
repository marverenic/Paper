package com.marverenic.reader.utils

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.support.annotation.ColorInt
import android.support.v4.widget.SwipeRefreshLayout

@BindingAdapter("colorScheme")
fun bindSwipeRefreshLayoutColorScheme(layout: SwipeRefreshLayout, @ColorInt colors: IntArray) {
    layout.setColorSchemeColors(*colors)
}

@BindingAdapter("refreshing")
fun bindSwipeRefreshLayoutRefreshing(layout: SwipeRefreshLayout, refreshing: Boolean) {
    layout.isRefreshing = refreshing
}

@InverseBindingAdapter(attribute = "refreshing")
fun isSwipeRefreshLayoutRefreshing(layout: SwipeRefreshLayout) = layout.isRefreshing

@BindingAdapter("refreshingAttrChanged")
fun setRefreshListener(layout: SwipeRefreshLayout, inverseBindingListener: InverseBindingListener) {
    layout.setOnRefreshListener { inverseBindingListener.onChange() }
}
