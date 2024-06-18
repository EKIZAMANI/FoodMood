package com.c241.ps341.fomo.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c241.ps341.fomo.R
import com.c241.ps341.fomo.adapter.FoodAdapter
import com.c241.ps341.fomo.api.response.FoodDataItem
import com.c241.ps341.fomo.databinding.FragmentFoodBinding
import com.c241.ps341.fomo.ui.activity.DetailActivity
import com.c241.ps341.fomo.ui.model.MainViewModel
import com.c241.ps341.fomo.ui.model.UserViewModel
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class FoodFragment : Fragment() {
    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FoodAdapter
    private lateinit var mainModel: MainViewModel
    private lateinit var userModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        adapter = FoodAdapter(context, false)
        mainModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        userModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        val root: View = binding.root
        val categoryId = arguments?.getInt("viewId")
        var category = ""
        when (categoryId) {
            R.id.btn_category1 -> {
                category = "ayam mati"
            }

            R.id.btn_category2 -> {
                category = "ikan"
            }

            R.id.btn_category3 -> {
                category = "telur"
            }

            R.id.btn_category4 -> {
                category = "tahu"
            }

            R.id.btn_category5 -> {
                category = "udang"
            }

            R.id.btn_category6 -> {
                category = "tempe"
            }
        }

        adapter.setOnItemClickCallBack(object :
            FoodAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: FoodDataItem) {
                Intent(activity, DetailActivity::class.java).also {
                    it.putExtra("extra_foodname", data.foodName)
                    it.putExtra("extra_image", data.image)
                    it.putExtra("extra_ingredients", data.ingredients)
                    it.putExtra("extra_steps", data.steps)
                    it.putExtra("extra_userid", data.userId)
                    it.putExtra("extra_id", data.id)
                    startActivity(it)
                }
            }
        })

        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            lifecycleScope.launch {
                mainModel.getFoods(userModel.getToken())
            }

            mainModel.foods.observe(requireActivity()) {
                val list = it.filter { data -> data.category == category }
                progressBar.visibility = View.GONE
                adapter.setList(list)
                recyclerView.adapter = adapter
            }

            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { _, _, _ ->
                searchBar.setText(searchView.text)
                searchView.hide()
                Toast.makeText(activity, searchView.text, Toast.LENGTH_SHORT).show()
                false
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}