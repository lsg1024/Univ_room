package com.example.toyproject.`interface`

import com.example.toyproject.DTO.loginDTO
import com.example.toyproject.DTO.login_data
import com.example.toyproject.DTO.roomDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface infoInterface {

    @POST("login")
    fun login(
        @Body login_data: login_data,
    ) : Call<loginDTO>

//    @GET("room/All")
//    fun getRoom(): Call<roomDTO>

    @GET("room/All")
    fun getRoom(@Query("sort") sort : String?): Call<roomDTO>

    @GET("room/search")
    fun getSearch(@Query("rN") rN : String?) : Call<roomDTO>

    @GET("room/noAll")
    fun getPlace(@Query("sort") sort : String?): Call<roomDTO>
}