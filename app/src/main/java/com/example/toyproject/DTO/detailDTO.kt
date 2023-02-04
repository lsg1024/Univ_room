package com.example.toyproject.DTO

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName

data class detailDTO(
    @SerializedName("room")
    val rm_result : ArrayList<detail_data>
)

data class detail_data(

    @SerializedName("c_pk")
    val c_pk : Int,

    @SerializedName("r_pk")
    val r_pk : Int,

    @SerializedName("star")
    val star : Float,

    @SerializedName("comment")
    val comment : String,

    @SerializedName("date")
    val date : String
)

data class detail_result_comment(
    @SerializedName("result")
    val result : Boolean
)

//data class comment_result(
//    val result : Boolean
//)

data class detail_comment(
    @SerializedName("r_pk")
    val r_pk : Int,

    @SerializedName("u_pk")
    val u_pk : Int,

    @SerializedName("star")
    val star : Float,

    @SerializedName("comment")
    val comment : String,
)

data class postReport(
    @SerializedName("result")
    val result : String
)

data class report_comment(
    @SerializedName("comment")
    val comment: String
)
