package com.example.mvvmnetwork.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnetwork.databinding.HeaderBinding
import com.example.mvvmnetwork.databinding.ListItemBinding
import com.example.mvvmnetwork.databinding.ListItemWsBinding
import com.example.mvvmnetwork.extenstions.loadImage
import com.mvvmnews.api.models.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_HEADER =0
private const val ITEM_VIEW_TYPE_ARTICLE = 1
private const val ITEM_VIEW_TYPE_WITHOUT_SOURCE = 2
class NewsListAdapter() : ListAdapter<DataItem,RecyclerView.ViewHolder>
    (NewsDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType){
            ITEM_VIEW_TYPE_HEADER-> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ARTICLE->NewsViewHolder.from(parent)
            ITEM_VIEW_TYPE_WITHOUT_SOURCE-> WsViewHolder.from(parent)
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
            is WsViewHolder->{
                val item = getItem(position) as DataItem.WithoutSourceItem
                //Data and click listener
                holder.bind(item.article, onItemClickListener)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DataItem.Header-> ITEM_VIEW_TYPE_HEADER
            is DataItem.ArticleItem -> ITEM_VIEW_TYPE_ARTICLE
            is DataItem.WithoutSourceItem -> ITEM_VIEW_TYPE_WITHOUT_SOURCE
        }
    }

    val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addHeaderAndSubmitList(list: List<Article>?){
        adapterScope.launch {
            val articles = when(list){
                null-> listOf(DataItem.Header)
                else-> {
                    val items = mutableListOf<DataItem>()
                    for (article in list){
                        if (article.source.id == null){
                            items.add(DataItem.WithoutSourceItem(article))
                        } else {
                            items.add(DataItem.ArticleItem(article))
                        }
                    }
                    listOf(DataItem.Header)+items
                }
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
    class WsViewHolder private constructor(itemview: View) : RecyclerView.ViewHolder(itemview){
        //Binding data
        fun bind(article: Article,clickListener: ((Article)->Unit)?){
            ListItemWsBinding.bind(itemView).apply {
                tvSource.text = article.source.name
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvPublishedAt.text = article.publishedAt
                ivArticleImage.loadImage(article.urlToImage)
                root.setOnClickListener {
                    clickListener?.let {
                        it(article)
                    }
                }
            }
        }
        //Creating a viewHolder
        companion object{
            fun from(parent: ViewGroup): WsViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemWsBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return WsViewHolder(binding.root)
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

    data class WithoutSourceItem(val article: Article): DataItem(){
        override val id = article.url
    }
    abstract val id: String
}