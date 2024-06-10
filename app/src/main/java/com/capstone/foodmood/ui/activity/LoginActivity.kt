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
import androidx.appcompat.app.AppCompatActivity
import com.capstone.foodmood.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            val text = "if don't have an account Register Here"
            val spannable = SpannableString(text)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                }
            }

            spannable.setSpan(
                ForegroundColorSpan(Color.BLUE),
                25, text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                clickableSpan,
                25, text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvRegister.text = spannable
            tvRegister.movementMethod = LinkMovementMethod.getInstance()

            btnLogin.setOnClickListener {
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            }
        }
    }
}