package com.marverenic.reader.ui.common.article

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.databinding.ViewArticleBinding
import com.marverenic.reader.model.Article

class ArticleAdapter(articles: List<Article> = emptyList(), val callback: ArticleReadCallback)
    : RecyclerView.Adapter<ArticleViewHolder>() {

    var articles: List<Article> = articles
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.view_article, parent, false),
                callback)
    }

    override fun getItemCount() = articles.size

    override fun getItemId(position: Int) = articles[position].id.hashCode().toLong()

}

class ArticleViewHolder(val binding: ViewArticleBinding,
                        val callback: ArticleReadCallback)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(article: Article) {
        binding.viewModel?.let { it.article = article }
                ?: binding.let { it.viewModel = ArticleViewModel(itemView.context, callback, article) }
    }

}
