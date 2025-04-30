package com.example.MobileApp.network

import com.example.MobileApp.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("api/login/")
    fun login(
        @Field("username") email: String, @Field("password") password: String
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
        @Header("Authorization") token: String, @Query("dest_card_number") dest: String
    ): Call<List<SourceOption>>

    @POST("api/transfer/")
    fun transfer(
        @Header("Authorization") token: String, @Body req: TransferRequest
    ): Call<TransferResponse>

    @GET("api/transactions/")
    fun getTransactions(
        @Header("Authorization") token: String
    ): Call<List<Transaction>>

    @GET("api/locations/")
    fun getLocations(
        @Header("Authorization") token: String, @Query("q") q: String
    ): Call<List<Location>>

    @FormUrlEncoded
    @POST("api/pay/")
    fun onSitePay(
        @Header("Authorization") token: String,
        @Field("card_number") card: String,
        @Field("location_id") locationId: Int,
        @Field("amount") amount: Double
    ): Call<OnSiteTransaction>

    @GET("api/pay/history/")
    fun getOnSiteHistory(
        @Header("Authorization") token: String
    ): Call<List<OnSiteTransaction>>
}