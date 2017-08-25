package com.marverenic.reader.ui.stream

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.ReaderApplication
import com.marverenic.reader.data.RssStore
import com.marverenic.reader.databinding.FragmentStreamBinding
import com.marverenic.reader.ui.ToolbarFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

private const val ARG_STREAM_NAME = "stream_name"
private const val ARG_STREAM_ID = "stream_id"

class StreamFragment : ToolbarFragment() {

    @Inject lateinit var rssStore: RssStore

    private lateinit var binding: FragmentStreamBinding

    override val title: String
        get() = arguments.getString(ARG_STREAM_NAME).orEmpty()

    override val canNavigateUp: Boolean = true

    private val streamId: String
        get() = arguments.getString(ARG_STREAM_ID)

    companion object {
        @SuppressLint("NewApi")
        fun newInstance(streamName: String, streamId: String) = StreamFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_STREAM_NAME, streamName)
                putString(ARG_STREAM_ID, streamId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReaderApplication.component(this).inject(this)
    }

    override fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stream, container, false)
        val viewModel = StreamViewModel(context) { rssStore.markAsRead(it) }
        binding.viewModel = viewModel

        rssStore.getStream(streamId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe({ stream ->
                    viewModel.entries = stream.items
                })

        return binding.root
    }

}