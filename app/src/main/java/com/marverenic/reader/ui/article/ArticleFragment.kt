package com.marverenic.reader.ui.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marverenic.reader.model.Article
import com.marverenic.reader.ui.ToolbarFragment

class ArticleFragment : ToolbarFragment() {

    private val article: Article
        get() = arguments.getParcelable(ARG_ARTICLE)

    override val title: String
        get() = article.title.orEmpty()

    override val canNavigateUp = true

    companion object {
        private const val ARG_ARTICLE = "article"

        fun newInstance(article: Article): ArticleFragment {
            return ArticleFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ARTICLE, article)
                }
            }
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup,
                                     savedInstanceState: Bundle?): View {
        return View(context)
    }
}