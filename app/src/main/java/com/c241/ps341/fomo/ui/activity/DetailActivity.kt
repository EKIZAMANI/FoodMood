package com.c241.ps341.fomo.ui.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.c241.ps341.fomo.R
import com.c241.ps341.fomo.databinding.ActivityDetailBinding
import com.c241.ps341.fomo.ui.model.BookmarkViewModel
import com.c241.ps341.fomo.ui.model.MainViewModel
import com.c241.ps341.fomo.ui.model.UserViewModel
import com.c241.ps341.fomo.ui.model.ViewModelFactory
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var userModel: UserViewModel
    private lateinit var mainModel: MainViewModel
    private lateinit var bookmarkModel: BookmarkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN).also {
            window.decorView.systemUiVisibility = it
        }

        window.statusBarColor = Color.TRANSPARENT
        binding = ActivityDetailBinding.inflate(layoutInflater)
        userModel = ViewModelProvider(this, ViewModelFactory(this))[UserViewModel::class.java]
        mainModel = ViewModelProvider(this)[MainViewModel::class.java]
        bookmarkModel = ViewModelProvider(this)[BookmarkViewModel::class.java]
        var isBookmark = false // read value from api
//        val isOwn: Boolean =
//            intent.getBooleanExtra("ownRecipe", false) // get the value from checking in API
        val foodName = intent.getStringExtra("extra_foodname")
        val image = intent.getStringExtra("extra_image")
        val ingredients = intent.getStringExtra("extra_ingredients")
        val steps = intent.getStringExtra("extra_steps")
        val userId = intent.getStringExtra("extra_userid")
        val id = intent.getIntExtra("extra_id", 0)
        var isOwn = false

        lifecycleScope.launch {
            isOwn = userId == userModel.getId()
        }

        with(binding) {
            setContentView(root)

            ivBack.setOnClickListener {
                finish()
            }

            if (isOwn) {
                ivBookmark.setImageResource(R.drawable.ic_delete)

                btnBookmark.setOnClickListener {
                    AlertDialog.Builder(this@DetailActivity).setTitle("Hapus item")
                        .setMessage("Apakah anda ingin menghapus item ini?")
                        .setCancelable(true).setPositiveButton("IYA") { _, _ ->
                            Toast.makeText(
                                this@DetailActivity,
                                "Recipe has been deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }.setNegativeButton("TIDAK") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                        .show()
                }
            } else {
                tvName.text = foodName
                tvIngredient.text = ingredients
                tvStep.text = steps

                lifecycleScope.launch {
                    bookmarkModel.getFoodBookmarks(userModel.getToken())
                    val uid = userModel.getId()

                    bookmarkModel.foods.observe(this@DetailActivity) {
                        val myBookmark = it.filter { data -> data.userId == uid }
                        val checkBookmark = myBookmark.filter { data -> data.foodId == id }

                        if (checkBookmark.isNotEmpty()) {
                            isBookmark = true
                            ivBookmark.setImageResource(R.drawable.ic_bookmark2_on)
                        }
                    }
                }

                btnBookmark.setOnClickListener {
                    isBookmark = !isBookmark
                    if (isBookmark) {
                        val progressDialog = ProgressDialog.show(this@DetailActivity, null, "Harap tunggu")
                        lifecycleScope.launch {
                            mainModel.postBookmark(userModel.getToken(), id)
                        }

                        mainModel.bookmarkPostMsg.observe(this@DetailActivity) {
                            progressDialog.dismiss()
                            ivBookmark.setImageResource(R.drawable.ic_bookmark2_on)
                            Toast.makeText(
                                this@DetailActivity,
                                "Food recipe has been saved on bookmark",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        ivBookmark.setImageResource(R.drawable.ic_bookmark2_off)
                        Toast.makeText(
                            this@DetailActivity,
                            "Food recipe has been unsaved on bookmark",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}