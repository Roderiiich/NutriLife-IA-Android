package com.example.NutriLife.Services

import com.example.NutriLife.data.RequestBody
import com.example.NutriLife.data.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/v1beta/models/gemini-1.5-flash-latest:generateContent")
    suspend fun askToGemini(
        @Query("key") apiKey: String,
        @Body requestBody: RequestBody
    ): Response<ResponseData>
}