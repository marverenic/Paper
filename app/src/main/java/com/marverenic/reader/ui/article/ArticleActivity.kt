package com.marverenic.reader.ui.article

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.marverenic.reader.model.Article
import com.marverenic.reader.ui.SingleFragmentActivity

class ArticleActivity : SingleFragmentActivity() {

    private val article: Article
        get() = intent.extras.getParcelable(EXTRA_ARTICLE)

    companion object {
        private const val EXTRA_ARTICLE = "article"

        fun newIntent(context: Context, article: Article): Intent {
            return Intent(context, ArticleActivity::class.java).apply {
                putExtra(EXTRA_ARTICLE, article)
            }
        }
    }

    override fun onCreateFragment(): Fragment {
        return ArticleFragment.newInstance(article)
    }
}