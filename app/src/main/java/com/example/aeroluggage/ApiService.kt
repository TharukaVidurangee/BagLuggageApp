package com.example.aeroluggage // Change to your actual package name

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("Easypass_revamp/Login/GetLoginValidationForStoragRoom")
    fun login(
        @Field("StaffNo") staffNo: String,
        @Field("StaffPassword") staffPassword: String
    ): Call<UserModel>
}
