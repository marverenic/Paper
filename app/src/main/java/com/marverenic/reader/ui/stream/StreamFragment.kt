package com.marverenic.reader.ui.stream

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.*
import com.marverenic.reader.R
import com.marverenic.reader.ReaderApplication
import com.marverenic.reader.data.RssStore
import com.marverenic.reader.databinding.FragmentStreamBinding
import com.marverenic.reader.ui.ToolbarFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

private const val ARG_STREAM_NAME = "stream_name"
private const val ARG_STREAM_ID = "stream_id"

private const val EXTRA_UNREAD_ONLY = "unread_only"

class StreamFragment : ToolbarFragment() {

    @Inject lateinit var rssStore: RssStore

    private lateinit var binding: FragmentStreamBinding

    override val title: String
        get() = arguments.getString(ARG_STREAM_NAME).orEmpty()

    override val canNavigateUp: Boolean = true

    private val streamId: String
        get() = arguments.getString(ARG_STREAM_ID)

    private var unreadOnly: Boolean = false

    private var streamSubscription: Disposable? = null

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
        savedInstanceState?.let {
            unreadOnly = it.getBoolean(EXTRA_UNREAD_ONLY, false)
        }

        ReaderApplication.component(this).inject(this)
        setHasOptionsMenu(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_UNREAD_ONLY, unreadOnly)
    }

    override fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stream, container, false)
        val viewModel = StreamViewModel(context,
                readCallback = { rssStore.markAsRead(it) },
                fetchCallback = { rssStore.loadMoreArticles(it) })

        binding.viewModel = viewModel
        loadArticles(viewModel)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.article_list, menu)
        menu.findItem(R.id.menu_item_show_all).isVisible = unreadOnly
        menu.findItem(R.id.menu_item_filter_unread).isVisible = !unreadOnly
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_show_all -> filterUnreadArticles(false)
            R.id.menu_item_filter_unread -> filterUnreadArticles(true)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun loadArticles(viewModel: StreamViewModel) {
        streamSubscription?.dispose()
        streamSubscription = rssStore.getStream(streamId, unreadOnly)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe({ stream ->
                    viewModel.entries = stream
                    viewModel.refreshing = false
                    setupRefreshListener(viewModel)
                })
    }

    private fun setupRefreshListener(viewModel: StreamViewModel) {
        viewModel.getRefreshObservable()
                .distinctUntilChanged()
                .subscribe({ refreshing ->
                    if (refreshing) {
                        rssStore.refreshStream(streamId)
                    }
                })
    }

    private fun filterUnreadArticles(hideReadArticles: Boolean) {
        if (unreadOnly != hideReadArticles) {
            unreadOnly = hideReadArticles
            loadArticles(binding.viewModel ?: throw IllegalStateException("View model is not set"))
            activity.invalidateOptionsMenu()
        }
    }

}