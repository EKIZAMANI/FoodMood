package com.capstone.foodmood.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.foodmood.ui.activity.DetailActivity
import com.capstone.foodmood.adapter.FoodAdapter
import com.capstone.foodmood.data.FoodData
import com.capstone.foodmood.databinding.FragmentResultBinding
import com.capstone.foodmood.ui.model.ResultViewModel

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val resultViewModel =
            ViewModelProvider(this)[ResultViewModel::class.java]

        _binding = FragmentResultBinding.inflate(inflater, container, false)
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN).also {
            activity?.window?.decorView?.systemUiVisibility = it
        }
        activity?.window?.statusBarColor = Color.TRANSPARENT
        adapter = FoodAdapter(context, false)
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
        adapter.setList(data)

        adapter.setOnItemClickCallBack(object : FoodAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: FoodData) {
                startActivity(Intent(activity, DetailActivity::class.java))
            }
        })

        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    searchBar.setText(searchView.text)
                    searchView.hide()
                    Toast.makeText(activity, searchView.text, Toast.LENGTH_SHORT).show()
                    false
                }
        }

//    val textView: TextView = binding.textHome
//    homeViewModel.text.observe(viewLifecycleOwner) {
//      textView.text = it
//    }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}