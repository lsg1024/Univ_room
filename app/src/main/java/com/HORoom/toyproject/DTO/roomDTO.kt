package com.HORoom.toyproject.DTO

import com.google.gson.annotations.SerializedName

data class roomDTO(
    @SerializedName("room")
    val result : ArrayList<room_result>
    )

data class room_result(

    @SerializedName("room_pk")
    var room_pk : Int,

    @SerializedName("roomName")
    var roomName : String,

    @SerializedName("location")
    var location : String,

    @SerializedName("price1")
    var price1 : Int,

    @SerializedName("price2")
    var price2: Int,

    @SerializedName("deposit")
    var deposit: Int,

    @SerializedName("latitude")
    var latitude: Double,

    @SerializedName("longitude")
    var longitude: Double,

    @SerializedName("category")
    var category: Int,

    @SerializedName("heart_user")
    var heart_user : String?
)

data class roomHeart(
    @SerializedName("result")
    val result : Boolean
)
