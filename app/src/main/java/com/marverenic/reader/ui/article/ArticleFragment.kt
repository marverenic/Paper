package com.marverenic.reader.ui.article

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.databinding.FragmentArticleBinding
import com.marverenic.reader.model.Article
import com.marverenic.reader.ui.ToolbarFragment

class ArticleFragment : ToolbarFragment() {

    private val article: Article
        get() = arguments.getParcelable(ARG_ARTICLE)

    override val title: String
        get() = arguments.getString(ARG_TITLE)

    override val canNavigateUp = true

    companion object {
        private const val ARG_ARTICLE = "article"
        private const val ARG_TITLE = "title"

        fun newInstance(article: Article, title: String): ArticleFragment {
            return ArticleFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ARTICLE, article)
                    putString(ARG_TITLE, title)
                }
            }
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup,
                                     savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<FragmentArticleBinding>(inflater,
                R.layout.fragment_article, container, false)

        binding.viewModel = ArticleViewerViewModel(context, article)

        return binding.root
    }
}