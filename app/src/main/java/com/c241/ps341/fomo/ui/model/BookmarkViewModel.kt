package com.c241.ps341.fomo.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c241.ps341.fomo.api.ApiConfig
import com.c241.ps341.fomo.api.response.BookmarkDataItem
import kotlinx.coroutines.launch
import retrofit2.HttpException

class BookmarkViewModel : ViewModel() {
    private val _foods = MutableLiveData<List<BookmarkDataItem>>()
    val foods: LiveData<List<BookmarkDataItem>> get() = _foods
    var message: String = ""

    fun getFoodBookmarks(token: String) {
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getBookmarks()
                _foods.value = response.data as List<BookmarkDataItem>?
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }

    fun deleteBookmark(token: String, id: Int) {
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.deleteBookmark(id)
                message = response.message.toString()
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }
}