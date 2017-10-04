package com.marverenic.reader.ui.home.all

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.*
import com.marverenic.reader.R
import com.marverenic.reader.ReaderApplication
import com.marverenic.reader.data.RssStore
import com.marverenic.reader.databinding.FragmentAllArticlesBinding
import com.marverenic.reader.ui.home.HomeFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

private const val EXTRA_UNREAD_ONLY = "unread_only"

class AllArticlesFragment : HomeFragment() {

    @Inject lateinit var rssStore: RssStore

    lateinit var binding: FragmentAllArticlesBinding

    override val title: String
        get() = getString(R.string.all_articles_header)

    private var unreadOnly: Boolean = false

    private var streamSubscription: Disposable? = null

    companion object {
        fun newInstance() = AllArticlesFragment()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_articles, container, false)
        val viewModel = AllArticlesViewModel(context,
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

    private fun loadArticles(viewModel: AllArticlesViewModel) {
        streamSubscription?.dispose()
        streamSubscription = rssStore.getAllArticles(unreadOnly)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe({ stream ->
                    viewModel.stream = stream
                    viewModel.refreshing = false
                    setupRefreshListener(viewModel)
                })
    }

    private fun setupRefreshListener(viewModel: AllArticlesViewModel) {
        viewModel.getRefreshObservable()
                .distinctUntilChanged()
                .subscribe({ refreshing ->
                    if (refreshing) {
                        rssStore.refreshAllArticles()
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