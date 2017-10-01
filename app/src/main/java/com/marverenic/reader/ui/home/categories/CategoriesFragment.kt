package com.marverenic.reader.ui.home.categories

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.ReaderApplication
import com.marverenic.reader.data.RssStore
import com.marverenic.reader.databinding.FragmentCategoriesBinding
import com.marverenic.reader.ui.home.HomeFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CategoriesFragment : HomeFragment() {

    @Inject lateinit var rssStore: RssStore

    lateinit var binding: FragmentCategoriesBinding

    override val title: String
        get() = getString(R.string.categories_header)

    companion object {
        fun newInstance() = CategoriesFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReaderApplication.component(this).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false)
        val viewModel = CategoriesViewModel(context)

        binding.viewModel = viewModel
        loadCategories(viewModel)

        return binding.root
    }

    private fun loadCategories(viewModel: CategoriesViewModel) {
        rssStore.getAllCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe({ categories ->
                    viewModel.categories = categories
                    viewModel.refreshing = false
                    setupRefreshListener(viewModel)
                })
    }

    private fun setupRefreshListener(viewModel: CategoriesViewModel) {
        viewModel.getRefreshObservable()
                .distinctUntilChanged()
                .subscribe({ refreshing ->
                    if (refreshing) {
                        rssStore.refreshCategories()
                    }
                })
    }

}