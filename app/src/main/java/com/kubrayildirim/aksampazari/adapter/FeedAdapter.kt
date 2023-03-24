package com.kubrayildirim.aksampazari.adapter

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kubrayildirim.aksampazari.data.model.Product
import com.kubrayildirim.aksampazari.databinding.FeedItemRowBinding
import com.kubrayildirim.aksampazari.util.cutDownTime

class FeedAdapter(private val feedList: List<Product>) :
    ListAdapter<Product, FeedAdapter.FeedViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = FeedItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    inner class FeedViewHolder(private val binding: FeedItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                tvFeedItemDescription.paintFlags = binding.tvFeedItemDescription.paintFlags or STRIKE_THRU_TEXT_FLAG
                tvFeedItemPrice.text = product.last_price
                tvFeedItemTitle.text = product.name
                tvFeedItemDescription.text = product.first_price
                tvFeedItemResturanName.text = product.restaurant_name
                Glide.with(itemView)
                    .load(product.photo_url)
                    .centerCrop()
                    .into(ivProfileImage)
                // update time
                val handler = Handler()
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        time.text = cutDownTime(product.timestamp.toDate().time)
                        handler.postDelayed(this, 1000)
                    }
                }, 1000)
                //time.text = cutDownTime(product.timestamp.toDate().time)

            }
        }
    }

    override fun getItemCount(): Int {
        return feedList.size
    }


    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.last_price == newItem.last_price
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(feedList[position])
    }
}