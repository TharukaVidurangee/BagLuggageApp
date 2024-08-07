package com.example.aeroluggage // Change to your actual package name

import RoomDataItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("Easypass_revamp/Login/GetLoginValidationForStoragRoom")  //for the login authentication
    fun login(
        @Field("StaffNo") staffNo: String,
        @Field("StaffPassword") staffPassword: String
    ): Call<UserModel>

    @GET("Easypass_revamp/Home/GetStorageRoomList") // Ensure this is the correct endpoint
    fun getStorageRoomList(): Call<List<RoomDataItem>>

    @POST("")     //To sync data from the local SQLite DB to the central DB
    fun syncData(@Body syncData: SyncData): Call<SyncData>
}













