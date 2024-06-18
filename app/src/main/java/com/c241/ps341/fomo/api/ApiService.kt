package com.c241.ps341.fomo.api

import com.c241.ps341.fomo.api.request.BookmarkRequest
import com.c241.ps341.fomo.api.response.BookmarkPostResponse
import com.c241.ps341.fomo.api.response.BookmarkResponse
import com.c241.ps341.fomo.api.response.FoodResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("foods")
    suspend fun getFoods(): FoodResponse

    @GET("bookmarks")
    suspend fun getBookmarks(): BookmarkResponse

    @DELETE("bookmarks/{id}")
    suspend fun deleteBookmark(@Path("id") id: Int): BookmarkResponse

    @POST("bookmarks")
    suspend fun postBookmark(@Body request: BookmarkRequest): BookmarkPostResponse
}