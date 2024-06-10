package com.capstone.foodmood.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.foodmood.adapter.FoodAdapter
import com.capstone.foodmood.adapter.MyFoodAdapter
import com.capstone.foodmood.data.FoodData
import com.capstone.foodmood.databinding.ActivityResepkuBinding

@Suppress("DEPRECATION")
class ResepkuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResepkuBinding
    private lateinit var adapter: MyFoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN).also {
            window.decorView.systemUiVisibility = it
        }

        window.statusBarColor = Color.TRANSPARENT
        binding = ActivityResepkuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = MyFoodAdapter(this)
        val data = ArrayList<FoodData>()
        data.add(FoodData("data1", "img1", 0))
        data.add(FoodData("data2", "img2", 0))
        data.add(FoodData("data1", "img1", 0))
        data.add(FoodData("data2", "img2", 0))
        data.add(FoodData("data1", "img1", 0))
        data.add(FoodData("data2", "img2", 0))
        data.add(FoodData("data1", "img1", 0))
        data.add(FoodData("data2", "img2", 0))
        data.add(FoodData("data1", "img1", 0))
        data.add(FoodData("data2", "img2", 0))
        data.add(FoodData("data1", "img1", 0))
        data.add(FoodData("data2", "img2", 0))
        adapter.setList(data)

        adapter.setOnItemClickCallBack(object : MyFoodAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: FoodData) {
                val intent = Intent(this@ResepkuActivity, DetailActivity::class.java)
                intent.putExtra("ownRecipe", true)
                startActivity(intent)
            }
        })

        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@ResepkuActivity)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter

            ivBack.setOnClickListener {
                finish()
            }
        }
    }
}