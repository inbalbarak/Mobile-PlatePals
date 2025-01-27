package com.idz.colman24class2.adapter

import OnItemClickListener
import PostViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.platepals.databinding.PostListRowBinding
import com.example.platepals.model.Post

class PostsRecyclerAdapter(private var posts: List<Post>?): RecyclerView.Adapter<PostViewHolder>() {

        var listener: OnItemClickListener? = null

        fun set(posts: List<Post>?) {
            this.posts = posts
        }

        override fun getItemCount(): Int = posts?.size ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val inflator = LayoutInflater.from(parent.context)
            val binding = PostListRowBinding.inflate(inflator, parent, false)
            return PostViewHolder(binding, listener)
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            holder.bind(posts?.get(position), position)
        }
    }