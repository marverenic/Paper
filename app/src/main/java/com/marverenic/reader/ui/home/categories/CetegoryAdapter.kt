package com.marverenic.reader.ui.home.categories

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.marverenic.reader.R
import com.marverenic.reader.databinding.ViewCategoryBinding
import com.marverenic.reader.model.Category

class CategoryAdapter(categories: List<Category> = emptyList())
    : RecyclerView.Adapter<CategoryViewHolder>() {

    var categories: List<Category> = categories
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CategoryViewHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), R.layout.view_category, parent, false))

}

class CategoryViewHolder(private val binding: ViewCategoryBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(category: Category) {
        binding.viewModel?.let { it.category = category }
                ?: binding.let { it.viewModel = CategoryViewModel(category) }
    }

}