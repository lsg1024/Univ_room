package com.example.toyproject.`interface`

import com.example.toyproject.DTO.loginDTO
import com.example.toyproject.DTO.login_data
import com.example.toyproject.DTO.roomDTO
import com.example.toyproject.DTO.room_result
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface infoInterface {

    @POST("login")
    fun login(
        @Body login_data: login_data
    ) : Call<loginDTO>

    @GET("room")
    fun getRoom(): Call<roomDTO>
}