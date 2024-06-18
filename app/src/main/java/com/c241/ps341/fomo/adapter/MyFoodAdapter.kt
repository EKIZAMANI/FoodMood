package com.c241.ps341.fomo.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.c241.ps341.fomo.api.response.FoodDataItem
import com.c241.ps341.fomo.data.FoodData
import com.c241.ps341.fomo.databinding.ItemMyfoodBinding
import com.c241.ps341.fomo.ui.fragment.BookmarkFragment

class MyFoodAdapter(
    private val context: Context,
    private val isOwner: Boolean,
    private val onDeleteClickCallback: OnDeleteClickCallback?
) :
    RecyclerView.Adapter<MyFoodAdapter.FoodViewHolder>() {
    private val list = ArrayList<FoodDataItem>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(data: List<FoodDataItem>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(val binding: ItemMyfoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FoodDataItem, position: Int) {
            binding.apply {
                cvItem.setOnClickListener {
                    onItemClickCallBack?.onItemClicked(data)
                }

                if (!isOwner) {
                    bookmark.visibility = View.GONE
                }

                btnDelete.setOnClickListener {
                    onDeleteClickCallback?.onDeleteClicked(data, position)
                }

//                Glide.with(itemView)
//                    .load(data.photo)
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .centerCrop()
//                    .into(ivPhoto)
                tvTitle.text = data.foodName
//                tvBookmark.text = data.bookmark.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = ItemMyfoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder((view))
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickCallBack {
        fun onItemClicked(data: FoodDataItem)
    }

    interface OnDeleteClickCallback {
        fun onDeleteClicked(data: FoodDataItem, position: Int)
    }
}