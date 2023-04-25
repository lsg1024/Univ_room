package com.horoom.toyproject.DTO

import com.google.gson.annotations.SerializedName

data class loginDTO (
    @SerializedName("result")
    val login_result : login_result
    )

data class login_result(
    val user_pk : Int
    )

data class login_data(
    @SerializedName("userId")
    val id : String,

    @SerializedName("userPassword")
    val pw : String
)

data class checkDTO(
    @SerializedName("result")
    val check_result : Boolean
)