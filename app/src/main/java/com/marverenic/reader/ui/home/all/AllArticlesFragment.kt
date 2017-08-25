package com.marverenic.reader.ui.home.all

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.ReaderApplication
import com.marverenic.reader.data.RssStore
import com.marverenic.reader.databinding.FragmentAllArticlesBinding
import com.marverenic.reader.ui.home.HomeFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AllArticlesFragment : HomeFragment() {

    @Inject lateinit var rssStore: RssStore

    lateinit var binding: FragmentAllArticlesBinding

    override val title: String
        get() = getString(R.string.all_articles_header)

    companion object {
        fun newInstance() = AllArticlesFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReaderApplication.component(this).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_articles, container, false)
        val viewModel = AllArticlesViewModel(context) { rssStore.markAsRead(it) }
        binding.viewModel = viewModel

        rssStore.getAllArticles()
                .map { it.items }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe({articles ->
                    viewModel.articles = articles
                })

        return binding.root
    }

}