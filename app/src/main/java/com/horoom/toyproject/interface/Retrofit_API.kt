package com.horoom.toyproject.`interface`

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit_API {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://oceanit.synology.me:8002")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(infoInterface::class.java)

    fun getInstance(): infoInterface? {
        return api
    }
}
