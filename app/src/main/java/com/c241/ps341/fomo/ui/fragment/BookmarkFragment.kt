package com.c241.ps341.fomo.ui.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c241.ps341.fomo.adapter.MyFoodAdapter
import com.c241.ps341.fomo.api.response.FoodDataItem
import com.c241.ps341.fomo.databinding.FragmentBookmarkBinding
import com.c241.ps341.fomo.ui.activity.DetailActivity
import com.c241.ps341.fomo.ui.model.BookmarkViewModel
import com.c241.ps341.fomo.ui.model.MainViewModel
import com.c241.ps341.fomo.ui.model.UserViewModel
import com.c241.ps341.fomo.ui.model.ViewModelFactory
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class BookmarkFragment : Fragment(), MyFoodAdapter.OnDeleteClickCallback {
    private var _binding: FragmentBookmarkBinding? = null
    private lateinit var adapter: MyFoodAdapter
    private val binding get() = _binding!!
    private lateinit var viewModel: BookmarkViewModel
    private lateinit var mainModel: MainViewModel
    private lateinit var userModel: UserViewModel
    private var bookmarkId: List<Int?> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        adapter = MyFoodAdapter(requireContext(), false, this)
        mainModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[BookmarkViewModel::class.java]
        userModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[UserViewModel::class.java]
        val root: View = binding.root

        adapter.setOnItemClickCallBack(object : MyFoodAdapter.OnItemClickCallBack {
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
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.setHasFixedSize(true)

            lifecycleScope.launch {
                progressBar.visibility = View.VISIBLE
                viewModel.getFoodBookmarks(userModel.getToken())
                mainModel.getFoods(userModel.getToken())
                val id = userModel.getId()

                viewModel.foods.observe(viewLifecycleOwner) { foodData ->
                    val filterId = foodData.filter { it.userId == id }
                    bookmarkId = filterId.map { it.id }
                    val foodId = filterId.map { it.foodId }

                    mainModel.foods.observe(viewLifecycleOwner) { allFoods ->
                        val bookmarkedFoods = allFoods.filter { it.id in foodId }
                        progressBar.visibility = View.GONE
                        adapter.setList(bookmarkedFoods)

                        if (adapter.itemCount == 0) {
                            tvEmpty.visibility = View.VISIBLE
                        } else {
                            tvEmpty.visibility = View.GONE
                        }

                        recyclerView.adapter = adapter
                    }
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDeleteClicked(data: FoodDataItem, position: Int) {
        AlertDialog.Builder(requireContext()).setTitle("Hapus item")
            .setMessage("Apakah anda ingin menghapus item ini dari bookmark?")
            .setCancelable(true)
            .setPositiveButton("IYA") { _, _ ->
                val progressDialog = ProgressDialog.show(requireContext(), null, "Harap tunggu")
                lifecycleScope.launch {
                    bookmarkId[position]
                        ?.let { viewModel.deleteBookmark(userModel.getToken(), it) }
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.getFoodBookmarks(userModel.getToken())
                    mainModel.getFoods(userModel.getToken())
                    val id = userModel.getId()

                    viewModel.foods.observe(viewLifecycleOwner) { foodData ->
                        val filterId = foodData.filter { it.userId == id }
                        bookmarkId = filterId.map { it.id }
                        val foodId = filterId.map { it.foodId }

                        mainModel.foods.observe(viewLifecycleOwner) { allFoods ->
                            val bookmarkedFoods = allFoods.filter { it.id in foodId }
                            binding.progressBar.visibility = View.GONE
                            adapter.setList(bookmarkedFoods)

                            if (adapter.itemCount == 0) {
                                binding.tvEmpty.visibility = View.VISIBLE
                            }

                            progressDialog.dismiss()
                            binding.recyclerView.adapter = adapter
                        }
                    }
                }

                Toast.makeText(requireContext(), "Recipe has been deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("TIDAK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }
}