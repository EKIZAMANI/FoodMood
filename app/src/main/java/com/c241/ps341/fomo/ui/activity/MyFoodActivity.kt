package com.c241.ps341.fomo.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.c241.ps341.fomo.adapter.MyFoodAdapter
import com.c241.ps341.fomo.api.response.FoodDataItem
import com.c241.ps341.fomo.data.FoodData
import com.c241.ps341.fomo.databinding.ActivityMyFoodBinding

@Suppress("DEPRECATION")
class MyFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyFoodBinding
    private lateinit var adapter: MyFoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN).also {
            window.decorView.systemUiVisibility = it
        }

        window.statusBarColor = Color.TRANSPARENT
        binding = ActivityMyFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = MyFoodAdapter(this, true, null)
//        val data = ArrayList<FoodData>()
//        data.add(FoodData("data1", "img1", 0))
//        data.add(FoodData("data2", "img2", 0))
//        data.add(FoodData("data1", "img1", 0))
//        data.add(FoodData("data2", "img2", 0))
//        data.add(FoodData("data1", "img1", 0))
//        data.add(FoodData("data2", "img2", 0))
//        data.add(FoodData("data1", "img1", 0))
//        data.add(FoodData("data2", "img2", 0))
//        data.add(FoodData("data1", "img1", 0))
//        data.add(FoodData("data2", "img2", 0))
//        data.add(FoodData("data1", "img1", 0))
//        data.add(FoodData("data2", "img2", 0))
//        adapter.setList(data)

        adapter.setOnItemClickCallBack(object : MyFoodAdapter.OnItemClickCallBack {
                override fun onItemClicked(data: FoodDataItem) {
                val intent = Intent(this@MyFoodActivity, DetailActivity::class.java)
                intent.putExtra("ownRecipe", true)
                startActivity(intent)
            }
        })

        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@MyFoodActivity)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter

            ivBack.setOnClickListener {
                finish()
            }
        }
    }
}