package com.marverenic.reader.ui.common.article

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.marverenic.reader.BR
import com.marverenic.reader.model.Stream

class StreamContinuationViewModel(stream: Stream, val callback: ArticleFetchCallback)
    : BaseObservable() {

    var stream: Stream = stream
        set(value) {
            field = value
            loading = false
        }

    var loading: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.loadMoreEnabled)
        }

    val loadMoreEnabled: Boolean
        @Bindable get() = !loading

    fun loadMore() {
        callback(stream)
        loading = true
    }

}