package com.c241.ps341.fomo.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c241.ps341.fomo.api.ApiConfig
import com.c241.ps341.fomo.api.response.FoodDataItem
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel : ViewModel() {
    private val _foods = MutableLiveData<List<FoodDataItem>>()
    val foods: LiveData<List<FoodDataItem>> get() = _foods

    fun getFoods(token: String) {
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getFoods()
                _foods.value = response.data as List<FoodDataItem>?
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }
}