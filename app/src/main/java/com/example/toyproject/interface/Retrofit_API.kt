package com.example.toyproject.`interface`

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit_API {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://210.119.104.114:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(infoInterface::class.java)

    fun getInstance(): infoInterface? {
        return api
    }
}