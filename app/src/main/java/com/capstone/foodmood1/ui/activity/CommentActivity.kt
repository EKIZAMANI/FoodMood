package com.capstone.foodmood1.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.capstone.foodmood1.R
import com.capstone.foodmood1.databinding.ActivityCommentBinding
import com.capstone.foodmood1.databinding.ActivityMainBinding

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){

            ivBack.setOnClickListener {
                finish()
            }
        }

    }
}