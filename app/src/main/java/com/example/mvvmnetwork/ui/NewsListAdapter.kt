package com.example.mvvmnetwork.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnetwork.R
import com.example.mvvmnetwork.databinding.ListItemBinding
import com.example.mvvmnetwork.extenstions.loadImage
import com.mvvmnews.api.models.Article

class NewsListAdapter() : ListAdapter<Article,NewsListAdapter.NewsViewHolder>
    (NewsDiffUtilCallback()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
       return NewsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
       val article = getItem(position)
        holder.bind(article, onItemClickListener)
    }

//ViewHolder
class NewsViewHolder private constructor(itemview: View) : RecyclerView.ViewHolder(itemview){
    //Binding data
    fun bind(article: Article, onItemClickListener: ((Article)->Unit)?){
        ListItemBinding.bind(itemView).apply {
            tvSource.text = article.source.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            ivArticleImage.loadImage(article.urlToImage)
            root.setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }
    //Creating a viewHolder
    companion object{
        fun from(parent: ViewGroup): NewsViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemBinding.inflate(
                layoutInflater,
                parent,
                false
            )
            return NewsViewHolder(binding.root)
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

    private var onItemClickListener: ((Article)->Unit)? = null

    fun setOnItemClickListener(listener: (Article)->Unit){
        onItemClickListener = listener
    }
}