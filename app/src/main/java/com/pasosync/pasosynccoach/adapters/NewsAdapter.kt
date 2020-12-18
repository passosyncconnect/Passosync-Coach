package com.pasosync.pasosynccoach.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.models.Article
import kotlinx.android.synthetic.main.news_layout.view.*

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemview:View):RecyclerView.ViewHolder(itemview)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    private var onItemClickListener: ((Article) -> Unit)? = null
    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.news_layout,parent,false
            )
        )

    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article=differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
tvArticleTitle.text=article.title

            setOnClickListener {
                onItemClickListener?.let { it(article)}
            }
        }

    }

    override fun getItemCount(): Int {

return differ.currentList.size
    }
}