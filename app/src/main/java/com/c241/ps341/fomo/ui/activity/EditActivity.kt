@file:Suppress("DEPRECATION")

package com.c241.ps341.fomo.ui.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.c241.ps341.fomo.databinding.ActivityEditBinding
import com.c241.ps341.fomo.ui.model.UserViewModel
import com.c241.ps341.fomo.ui.model.ViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var userModel: UserViewModel
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityEditBinding.inflate(layoutInflater)
        userModel = ViewModelProvider(this, ViewModelFactory(this))[UserViewModel::class.java]
        db = Firebase.database
        val name = intent.getStringExtra("extra_name").toString()
        val email = intent.getStringExtra("extra_email").toString()

        with(binding) {
            setContentView(root)
            etName.setText(name)
            etEmail.setText(email)

            btnSubmit.setOnClickListener {
                updateProfile(etName.text.toString(), etEmail.text.toString())
            }

            ivBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun updateProfile(name: String, email: String) {
        lifecycleScope.launch {
            val id: String = userModel.getId()
            val progressDialog = ProgressDialog.show(this@EditActivity, null, "Harap tunggu")
            val userRef = db.reference.child("user").child(id)
            val user = mapOf("name" to name, "email" to email)

            userRef.updateChildren(user).addOnCompleteListener { task ->
                progressDialog.dismiss()

                if (task.isSuccessful) {
                    userModel.setName(name)
                    userModel.setEmail(email)
                    Toast.makeText(
                        this@EditActivity,
                        "Profile updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@EditActivity,
                        "Failed to update profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}