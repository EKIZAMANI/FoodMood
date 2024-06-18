package com.c241.ps341.fomo.ui.fragment

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.c241.ps341.fomo.R
import com.c241.ps341.fomo.adapter.FoodAdapter
import com.c241.ps341.fomo.api.response.FoodDataItem
import com.c241.ps341.fomo.databinding.FragmentHomeBinding
import com.c241.ps341.fomo.ui.activity.DetailActivity
import com.c241.ps341.fomo.ui.activity.UploadActivity
import com.c241.ps341.fomo.ui.model.HomeViewModel
import com.c241.ps341.fomo.ui.model.UserViewModel
import com.c241.ps341.fomo.ui.model.ViewModelFactory
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FoodAdapter
    private lateinit var viewModel: HomeViewModel
    private lateinit var userModel: UserViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = FoodAdapter(context, false)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        userModel = ViewModelProvider(requireActivity(), ViewModelFactory(requireContext()))[UserViewModel::class.java]
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        activity?.window?.statusBarColor = Color.TRANSPARENT
        val root: View = binding.root

        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            lifecycleScope.launch {
                viewModel.getFoods(userModel.getToken())
            }

            viewModel.foods.observe(requireActivity()) {
                progressBar.visibility = View.GONE
                adapter.setList(it)
                recyclerView.adapter = adapter
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
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { _, _, _ ->
                searchBar.setText(searchView.text)
                searchView.hide()
                findNavController().navigate(R.id.action_start_to_result)
                true
            }

            setCategoryOnClick(btnCategory1)
            setCategoryOnClick(btnCategory2)
            setCategoryOnClick(btnCategory3)
            setCategoryOnClick(btnCategory4)
            setCategoryOnClick(btnCategory5)
            setCategoryOnClick(btnCategory6)

            btnUpload.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), 1
                    )
                } else {
                    showOptions()
                }
            }

            scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                val isDarkMode =
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

                if (scrollY > 680) {
                    activity?.window?.decorView?.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                if (!isDarkMode) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
                    if (isDarkMode) {
                        animateStatusBarColorChange(activity?.window, Color.parseColor("#121212"))
                    } else {
                        animateStatusBarColorChange(activity?.window, Color.WHITE)
                    }
                } else {
                    activity?.window?.decorView?.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    animateStatusBarColorChange(activity?.window, Color.TRANSPARENT)
                }
            }
        }

        return root
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                showOptions()
            } else {
                // Handle permission denied case
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                val intent = Intent(activity, UploadActivity::class.java)
                intent.putExtra("imageUri", imageUri.toString())
                startActivity(intent)
            } else {
                val extras = data?.extras
                val imageBitmap = extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    val path = MediaStore.Images.Media.insertImage(
                        requireContext().contentResolver,
                        imageBitmap,
                        "Title",
                        null
                    )
                    val uri = Uri.parse(path)
                    val intent = Intent(activity, UploadActivity::class.java)
                    intent.putExtra("imageUri", uri.toString())
                    startActivity(intent)
                }
            }
        }
    }

    private fun setCategoryOnClick(view: View) {
        view.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("viewId", view.id)
            }
            findNavController().navigate(R.id.action_start_to_result, bundle)
        }
    }

    private fun showOptions() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val chooser = Intent.createChooser(intent, "Pilih gambar")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent, captureIntent))
        startActivityForResult(chooser, 1)
    }

    private fun animateStatusBarColorChange(window: Window?, toColor: Int) {
        window?.let {
            val fromColor = it.statusBarColor
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
            colorAnimation.duration = 300
            colorAnimation.addUpdateListener { animator ->
                it.statusBarColor = animator.animatedValue as Int
            }
            colorAnimation.start()
        }
    }
}
