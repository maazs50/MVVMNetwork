package com.example.mvvmnetwork.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnetwork.databinding.HeaderBinding
import com.example.mvvmnetwork.databinding.ListItemBinding
import com.example.mvvmnetwork.extenstions.loadImage
import com.mvvmnews.api.models.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_HEADER =0
private const val ITEM_VIEW_TYPE_ARTICLE = 1
class NewsListAdapter() : ListAdapter<DataItem,RecyclerView.ViewHolder>
    (NewsDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

       return when(viewType){
        ITEM_VIEW_TYPE_HEADER-> HeaderViewHolder.from(parent)
        ITEM_VIEW_TYPE_ARTICLE->NewsViewHolder.from(parent)
        else-> throw ClassCastException("Unknown viewType $viewType")
       }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is NewsViewHolder->{
                val item = getItem(position) as DataItem.ArticleItem
                //Data and click listener
                holder.bind(item.article, onItemClickListener)
            }
            is HeaderViewHolder->{

            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DataItem.ArticleItem -> ITEM_VIEW_TYPE_ARTICLE
            is DataItem.Header-> ITEM_VIEW_TYPE_HEADER
        }
    }

    val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addHeaderAndSubmitList(list: List<Article>?){
        adapterScope.launch {
            val articles = when(list){
                null-> listOf(DataItem.Header)
                else-> listOf(DataItem.Header)+ list.map { DataItem.ArticleItem(it) }
            }
            withContext(Dispatchers.Main){
                submitList(articles)
            }
        }
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

    class HeaderViewHolder private constructor(itemview: View) : RecyclerView.ViewHolder(itemview){
        //Creating a viewHolder
        companion object{
            fun from(parent: ViewGroup): HeaderViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaderBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return HeaderViewHolder(binding.root)
            }
        }
    }
    class NewsDiffUtilCallback: DiffUtil.ItemCallback<DataItem>(){
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

    }

    private var onItemClickListener: ((Article)->Unit)? = null

    fun setOnItemClickListener(listener: (Article)->Unit){
        onItemClickListener = listener
    }
}

sealed class DataItem{
    data class ArticleItem(val article: Article): DataItem() {
        override val id = article.url
    }

    object Header : DataItem(){
        override val id = "a"
    }
    abstract val id: String
}