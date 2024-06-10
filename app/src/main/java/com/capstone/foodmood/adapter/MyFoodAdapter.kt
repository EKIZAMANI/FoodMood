package com.capstone.foodmood.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.capstone.foodmood.data.FoodData
import com.capstone.foodmood.databinding.ItemMyfoodBinding

class MyFoodAdapter(private val context: Context) :
    RecyclerView.Adapter<MyFoodAdapter.FoodViewHolder>() {
    private val list = ArrayList<FoodData>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(data: ArrayList<FoodData>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(val binding: ItemMyfoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FoodData) {
            binding.apply {
                cvItem.setOnClickListener {
                    onItemClickCallBack?.onItemClicked(data)
                }

                btnDelete.setOnClickListener {
                    AlertDialog.Builder(context).setTitle("Hapus item")
                        .setMessage("Apakah anda ingin menghapus item ini?")
                        .setCancelable(true).setPositiveButton(
                            "IYA",
                            DialogInterface.OnClickListener { _, _ ->
                                Toast.makeText(
                                    context,
                                    "Recipe has been deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }).setNegativeButton(
                            "TIDAK",
                            DialogInterface.OnClickListener { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            })
                        .show()
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
        val view = ItemMyfoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder((view))
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickCallBack {
        fun onItemClicked(data: FoodData)
    }
}