package com.example.toyproject

import com.example.toyproject.`interface`.Retrofit_API
import retrofit2.Retrofit

class MainRepository constructor(private val retrofit : Retrofit_API) {

    fun getRooms() = retrofit.getInstance()


}