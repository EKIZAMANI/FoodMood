package com.capstone.foodmood1.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.foodmood1.ui.activity.DetailActivity
import com.capstone.foodmood1.adapter.MyFoodAdapter
import com.capstone.foodmood1.data.FoodData
import com.capstone.foodmood1.databinding.FragmentBookmarkBinding
import com.capstone.foodmood1.ui.model.HomeViewModel

@Suppress("DEPRECATION")
class BookmarkFragment : Fragment() {
    private var _binding: FragmentBookmarkBinding? = null
    private lateinit var adapter: MyFoodAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN).also {
            activity?.window?.decorView?.systemUiVisibility = it
        }
        activity?.window?.statusBarColor = Color.TRANSPARENT
        adapter = MyFoodAdapter(requireContext())
        val root: View = binding.root
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
                startActivity(Intent(activity, DetailActivity::class.java))
            }
        })

        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}