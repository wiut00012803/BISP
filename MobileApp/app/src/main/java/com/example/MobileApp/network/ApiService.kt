package com.example.MobileApp.network

import com.example.MobileApp.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("api/login/")
    fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("api/accounts/register/")
    fun register(
        @Field("email") email: String,
        @Field("password") pwd: String,
        @Field("confirm_password") conf: String
    ): Call<Void>

    @GET("api/cards/")
    fun getCards(@Header("Authorization") token: String): Call<List<Card>>

    @GET("api/source-options/")
    fun getSourceOptions(
        @Header("Authorization") token: String,
        @Query("dest_card_number") dest: String
    ): Call<List<SourceOption>>

    @POST("api/transfer/")
    fun transfer(
        @Header("Authorization") token: String,
        @Body req: TransferRequest
    ): Call<TransferResponse>
}