package com.capstone.foodmood1.data.repository

import android.util.Log
import com.capstone.foodmood1.data.UserData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun createUser(userId: String, name: String, password: String, email: String, photoUrl: String) {
        val user = UserData(name, password, email, photoUrl)
        database.child("users").child(userId).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("Firebase", "User created")
                } else {
                    Log.e("Firebase", "Error to create user")
                }
            }
    }
}