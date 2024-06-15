package com.capstone.foodmood1.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.foodmood1.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val name = intent.getStringExtra("extra_name").toString()
        val email = intent.getStringExtra("extra_email").toString()

        with(binding) {
            etName.setText(name)
            etEmail.setText(email)
            btnSubmit.setOnClickListener {
                Toast.makeText(
                    this@EditActivity,
                    "Profile has been edited",
                    Toast.LENGTH_SHORT
                ).show()
            }

            ivBack.setOnClickListener {
                finish()
            }
        }
    }
}