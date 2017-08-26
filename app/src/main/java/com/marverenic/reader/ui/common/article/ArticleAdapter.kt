package com.marverenic.reader.ui.common.article

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.databinding.ViewArticleBinding
import com.marverenic.reader.databinding.ViewLoadMoreBinding
import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Stream

private const val ARTICLE_VIEW_TYPE = 0
private const val LOAD_MORE_VIEW_TYPE = 1

class ArticleAdapter(stream: Stream? = null,
                     val readCallback: ArticleReadCallback,
                     val fetchCallback: ArticleFetchCallback)
    : RecyclerView.Adapter<ArticleAdapterViewHolder>() {

    var stream: Stream? = stream
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val articles: List<Article>
        get() = stream?.items ?: emptyList()

    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: ArticleAdapterViewHolder, position: Int) {
        when (holder) {
            is ArticleViewHolder -> holder.bind(articles[position])
            is LoadMoreViewHolder -> stream?.let { holder.bind(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleAdapterViewHolder {
        return if (viewType == ARTICLE_VIEW_TYPE) {
            ArticleViewHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), R.layout.view_article, parent, false),
                    readCallback)
        } else if (viewType == LOAD_MORE_VIEW_TYPE) {
            LoadMoreViewHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), R.layout.view_load_more, parent, false),
                    fetchCallback)
        } else {
            throw IllegalArgumentException("Invalid viewType: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position in articles.indices) ARTICLE_VIEW_TYPE else LOAD_MORE_VIEW_TYPE
    }

    override fun getItemCount() = articles.size + if (stream?.continuation != null) 1 else 0

    override fun getItemId(position: Int) =
            if (position in articles.indices) articles[position].id.hashCode().toLong()
            else (1L shl 32)

}

sealed class ArticleAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class ArticleViewHolder(val binding: ViewArticleBinding,
                        val callback: ArticleReadCallback)
    : ArticleAdapterViewHolder(binding.root) {

    fun bind(article: Article) {
        binding.viewModel?.let { it.article = article }
                ?: binding.let { it.viewModel = ArticleViewModel(itemView.context, callback, article) }
    }

}

class LoadMoreViewHolder(val binding: ViewLoadMoreBinding,
                         val callback: ArticleFetchCallback)
    : ArticleAdapterViewHolder(binding.root) {

    fun bind(stream: Stream) {
        binding.viewModel?.let { it.stream = stream }
                ?: binding.let { it.viewModel = StreamContinuationViewModel(stream, callback) }
    }

}