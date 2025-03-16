package com.example.platepals.adapter

import OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.example.platepals.databinding.PostListRowBinding
import com.example.platepals.model.Post

class PostViewHolder(
    private val binding: PostListRowBinding,
    listener: OnItemClickListener?
    ): RecyclerView.ViewHolder(binding.root) {

    private var post: Post? = null

        init {
            itemView.setOnClickListener {
                listener?.onItemClick(post)
            }
        }

        fun bind(post: Post?, position: Int) {
            this.post = post
            binding.postRowTitle.text = post?.title
            binding.postRowAuthor.text = post?.author
            binding.postRowRating.text = "${post?.rating ?: 0} | ${post?.ratingCount ?: 0} Reviews"
        }
    }