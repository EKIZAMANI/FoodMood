package com.capstone.foodmood1.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.foodmood1.R
import com.capstone.foodmood1.databinding.ActivityDetailBinding

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN).also {
            window.decorView.systemUiVisibility = it
        }

        window.statusBarColor = Color.TRANSPARENT
        binding = ActivityDetailBinding.inflate(layoutInflater)
        var isBookmark = false // read value from api
        val isOwn: Boolean = intent.getBooleanExtra("ownRecipe", false) // get the value from checking in API
        Log.d("activity1", "Received ownRecipe extra with value: $isOwn")
        setContentView(binding.root)

        with(binding) {
            ivBack.setOnClickListener {
                finish()
            }
            btnSubmit.setOnClickListener {
                startActivity(Intent(this@DetailActivity, CommentActivity::class.java))
            }
            if (isOwn) {
                ivBookmark.setImageResource(R.drawable.ic_delete)

                btnBookmark.setOnClickListener {
                    AlertDialog.Builder(this@DetailActivity).setTitle("Hapus item")
                        .setMessage("Apakah anda ingin menghapus item ini?")
                        .setCancelable(true).setPositiveButton(
                            "IYA",
                            DialogInterface.OnClickListener { _, _ ->
                                Toast.makeText(
                                    this@DetailActivity,
                                    "Recipe has been deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }).setNegativeButton(
                            "TIDAK",
                            DialogInterface.OnClickListener { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            })
                        .show()
                }
            } else {
                btnBookmark.setOnClickListener {
                    isBookmark = !isBookmark
                    if (isBookmark) {
                        ivBookmark.setImageResource(R.drawable.ic_bookmark2_on)
                        Toast.makeText(
                            this@DetailActivity,
                            "Food recipe has been saved on bookmark",
                            Toast.LENGTH_SHORT
                        ).show()
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