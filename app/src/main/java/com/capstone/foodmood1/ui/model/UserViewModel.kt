package com.capstone.foodmood1.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.foodmood1.data.local.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserViewModel(private val pref:UserPreferences): ViewModel() {
    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun setToken(value: String) {
        viewModelScope.launch {
            pref.setToken(value)
        }
    }

    suspend fun getName(): String {
        return pref.getName().first()
    }

    fun setName(value: String) {
        viewModelScope.launch {
            pref.setName(value)
        }
    }

    suspend fun getEmail(): String {
        return pref.getEmail().first()
    }

    fun setEmail(value: String) {
        viewModelScope.launch {
            pref.setEmail(value)
        }
    }

    suspend fun getPhoto(): String {
        return pref.getPhoto().first()
    }

    fun setPhoto(value: String) {
        viewModelScope.launch {
            pref.setPhoto(value)
        }
    }
}