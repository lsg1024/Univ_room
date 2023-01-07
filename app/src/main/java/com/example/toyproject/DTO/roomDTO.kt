package com.example.toyproject.DTO

import com.google.gson.annotations.SerializedName

data class roomDTO(
    @SerializedName("room")
    val result : ArrayList<room_result>
    )

data class room_result(
    @SerializedName("roomName")
    val roomName : String,

    @SerializedName("location")
    val location : String,

    @SerializedName("price1")
    val price1 : Int,

    @SerializedName("price2")
    val price2: Int,

    @SerializedName("deposit")
    val deposit: Int
)
