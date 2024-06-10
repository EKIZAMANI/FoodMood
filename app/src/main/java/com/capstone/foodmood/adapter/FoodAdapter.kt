package com.capstone.foodmood.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.capstone.foodmood.R
import com.capstone.foodmood.data.FoodData
import com.capstone.foodmood.databinding.ItemFoodBinding

class FoodAdapter(private val context: Context?, private var isBookmark: Boolean) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    private val list = ArrayList<FoodData>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setOnItemClickCallBack (onItemClickCallBack: OnItemClickCallBack){
        this.onItemClickCallBack = onItemClickCallBack
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(data: ArrayList<FoodData>){
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: FoodData) {
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
                Glide.with(itemView)
                    .load(data.photo)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivPhoto)
                tvTitle.text = data.title
                tvBookmark.text = data.bookmark.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder((view))
    }
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickCallBack{
        fun onItemClicked(data: FoodData)
    }
}