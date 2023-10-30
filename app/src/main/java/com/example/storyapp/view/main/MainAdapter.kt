package com.example.storyapp.view.main

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.response.main.ListStoryItem
import com.example.storyapp.databinding.ItemRowBinding

class MainAdapter : ListAdapter<ListStoryItem, MainAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private var onItemClick: ((ListStoryItem, ActivityOptionsCompat?) -> Unit)? = null

    fun setOnItemClickListener(listener: (ListStoryItem, ActivityOptionsCompat?) -> Unit) {
        onItemClick = listener
    }

    class MyViewHolder(val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: ListStoryItem) {
            binding.tvUsername.text = "${user.name}"
            binding.theStory.text = "${user.description}"
            Glide.with(binding.root).load(user.photoUrl).into(binding.imgItemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)

        holder.itemView.setOnClickListener {
            val imageView = holder.binding.imgItemImage
            val nameView = holder.binding.tvUsername
            val descriptionView = holder.binding.theStory

            val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                holder.itemView.context as Activity,
                androidx.core.util.Pair(imageView, "profile"),
                androidx.core.util.Pair(nameView, "name"),
                androidx.core.util.Pair(descriptionView, "description")
            )

            onItemClick?.invoke(user, optionsCompat)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}