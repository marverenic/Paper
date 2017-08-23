package com.marverenic.reader.ui.common.article

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.databinding.ViewArticleBinding
import com.marverenic.reader.model.Article

class ArticleAdapter(articles: List<Article> = emptyList()) : RecyclerView.Adapter<ArticleViewHolder>() {

    var articles: List<Article> = articles
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.view_article, parent, false))
    }

    override fun getItemCount() = articles.size

}

class ArticleViewHolder(val binding: ViewArticleBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(article: Article) {
        binding.viewModel?.let { it.article = article }
                ?: binding.let { it.viewModel = ArticleViewModel(binding.root.context, article) }
    }

}
