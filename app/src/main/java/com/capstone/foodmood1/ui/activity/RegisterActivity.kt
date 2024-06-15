package com.capstone.foodmood1.ui.activity

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.foodmood1.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private var showPassword = false
    private var showConfirmPassword = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        with(binding) {
            setContentView(root)
            btnRegister.isEnabled = false
            btnPassword.visibility = View.GONE
            btnConfirmPassword.visibility = View.GONE

            etName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btnRegister.isEnabled =
                        etName.text.toString().trim()
                            .isNotEmpty() && etEmail.text.toString()
                            .isNotEmpty() && etEmail.error == null && etPassword.text.toString()
                            .isNotEmpty() && etPassword.error == null && etConfirmPassword.text.toString()
                            .isNotEmpty() && etConfirmPassword.error == null
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            etEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btnRegister.isEnabled =
                        etName.text.toString().trim()
                            .isNotEmpty() && etEmail.text.toString()
                            .isNotEmpty() && etEmail.error == null && etPassword.text.toString()
                            .isNotEmpty() && etPassword.error == null && etConfirmPassword.text.toString()
                            .isNotEmpty() && etConfirmPassword.error == null
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            etPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        btnPassword.visibility = View.GONE
                    } else {
                        btnPassword.visibility = View.VISIBLE
                    }

                    btnRegister.isEnabled =
                        etName.text.toString().trim()
                            .isNotEmpty() && etEmail.text.toString()
                            .isNotEmpty() && etEmail.error == null && etPassword.text.toString()
                            .isNotEmpty() && etPassword.error == null && etConfirmPassword.text.toString()
                            .isNotEmpty() && etConfirmPassword.error == null
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            etConfirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        btnConfirmPassword.visibility = View.GONE
                    } else {
                        btnConfirmPassword.visibility = View.VISIBLE
                    }

                    btnRegister.isEnabled =
                        etName.text.toString().trim()
                            .isNotEmpty() && etEmail.text.toString()
                            .isNotEmpty() && etEmail.error == null && etPassword.text.toString()
                            .isNotEmpty() && etPassword.error == null && etConfirmPassword.text.toString()
                            .isNotEmpty() && etConfirmPassword.error == null
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            btnPassword.setOnClickListener {
                showPassword = !showPassword

                if (showPassword) {
                    ivPasswordOff.visibility = View.GONE
                    ivPasswordOn.visibility = View.VISIBLE
                    etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                } else {
                    ivPasswordOff.visibility = View.VISIBLE
                    ivPasswordOn.visibility = View.GONE
                    etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                }
            }

            btnConfirmPassword.setOnClickListener {
                showConfirmPassword = !showConfirmPassword

                if (showConfirmPassword) {
                    ivConfirmPasswordOff.visibility = View.GONE
                    ivConfirmPasswordOn.visibility = View.VISIBLE
                    etConfirmPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                } else {
                    ivConfirmPasswordOff.visibility = View.VISIBLE
                    ivConfirmPasswordOn.visibility = View.GONE
                    etConfirmPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                }
            }

            val text = "if you have account you can Login here!"
            val spannable = SpannableString(text)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    finish()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }

            spannable.setSpan(
                ForegroundColorSpan(Color.BLUE),
                28, text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                clickableSpan,
                28, text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvLogin.text = spannable
            tvLogin.movementMethod = LinkMovementMethod.getInstance()
            tvLogin.highlightColor = Color.TRANSPARENT

            btnRegister.setOnClickListener {

                val progressDialog = ProgressDialog.show(this@RegisterActivity, null, "Harap tunggu")
                progressDialog.show()

                if (etEmail.text.toString().isEmpty() || etPassword.text.toString().isEmpty()) {
                    progressDialog.dismiss()
                    Toast.makeText(this@RegisterActivity, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener(this@RegisterActivity) {
                    progressDialog.dismiss()
                    if (it.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Daftar akun berhasil", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Gagal mendaftar akun", Toast.LENGTH_SHORT).show()
                        Log.e("mgrlog", it.exception.toString())
                    }
                }
            }
        }
    }
}