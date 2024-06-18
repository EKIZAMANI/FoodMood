package com.c241.ps341.fomo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.c241.ps341.fomo.R
import com.c241.ps341.fomo.api.response.FoodDataItem
import com.c241.ps341.fomo.databinding.ItemFoodBinding

class FoodAdapter(private val context: Context?, private var isBookmark: Boolean) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    private val list = ArrayList<FoodDataItem>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    fun getList(): List<FoodDataItem> {
        return list
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(data: List<FoodDataItem>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FoodDataItem) {
            binding.apply {
                cvItem.setOnClickListener {
                    onItemClickCallBack?.onItemClicked(data)
                }

                btnBookmark.setOnClickListener {
                    isBookmark = !isBookmark

                    if (isBookmark) {
                        btnBookmark.setImageResource(R.drawable.ic_bookmark_on)
                        Toast.makeText(
                            context,
                            "Food recipe has been saved on bookmark",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        btnBookmark.setImageResource(R.drawable.ic_bookmark_off)
                        Toast.makeText(
                            context,
                            "Food recipe has been unsaved on bookmark",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                ivPhoto.setImageResource(R.drawable.example)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodAdapter.FoodViewHolder {
        val view = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder((view))
    }

    override fun onBindViewHolder(holder: FoodAdapter.FoodViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickCallBack {
        fun onItemClicked(data: FoodDataItem)
    }
}