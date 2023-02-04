package com.example.toyproject.`interface`

import com.example.toyproject.DTO.*
import retrofit2.Call
import retrofit2.http.*

interface infoInterface {

    @POST("login")
    fun login(
        @Body login_data: login_data,
    ) : Call<loginDTO>


    @GET("room")
    fun getRoom(@Header("u_pk") u_pk: Int,@Query("sort") sort : String, @Query("category") category: String): Call<roomDTO>

    @GET("room/noAll")
    fun getPlace(@Query("sort") sort : String?): Call<roomDTO>

    @GET("room/search")
    fun getSearch(@Query("rN") rN : String?,@Query("sort") sort : String) : Call<roomDTO>

    @GET("room/commentView")
    fun getComment(@Query("r_pk") r_pk : Int?) : Call<detailDTO>

    @GET("room/heartlist")
    fun getHeartList(@Header("u_pk") u_pk: Int) : Call<roomDTO>

    @POST("room/heart")
    fun postHeart(@Header("u_pk")u_pk: Int, @Header("r_pk") r_pk: Int) : Call<roomHeart>

    @POST("room/comment")
    fun postComment(@Body detail_comment: detail_comment) : Call<detail_result_comment>

    @POST("room/commentreport")
    fun postReport(@Header("u_pk") u_pk : Int, @Header("r_pk") r_pk: Int, @Body report_comment: report_comment) : Call<postReport>

}