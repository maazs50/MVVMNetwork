package com.example.mvvmnetwork.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnetwork.databinding.ListItemBinding
import com.example.mvvmnetwork.extenstions.loadImage
import com.mvvmnews.api.models.Article

class NewsListAdapter(val listener: OnArticleClickedListener) : ListAdapter<Article,NewsListAdapter.NewsViewHolder>
    (NewsDiffUtilCallback()) {

    inner class NewsViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ListItemBinding.inflate(
            parent.context.getSystemService(LayoutInflater::class.java),
            parent,
            false
        )
        return NewsViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
       val article = getItem(position)
        ListItemBinding.bind(holder.itemView).apply {
            tvSource.text = article.source.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            ivArticleImage.loadImage(article.urlToImage!!)
            root.setOnClickListener {
                listener.onClick(article.url)
            }
        }

    }


    class NewsDiffUtilCallback: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

    }


    interface OnArticleClickedListener{
        fun onClick(url: String)
    }
}