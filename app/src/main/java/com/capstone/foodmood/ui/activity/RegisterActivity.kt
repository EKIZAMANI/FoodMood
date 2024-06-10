package com.capstone.foodmood.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.foodmood.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            val text = "if have account Login Here"
            val spannable = SpannableString(text)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    finish()
                }
            }

            spannable.setSpan(
                ForegroundColorSpan(Color.BLUE),
                16, text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                clickableSpan,
                16, text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvLogin.text = spannable
            tvLogin.movementMethod = LinkMovementMethod.getInstance()

            btnRegister.setOnClickListener {
                finish()
            }
        }
    }
}