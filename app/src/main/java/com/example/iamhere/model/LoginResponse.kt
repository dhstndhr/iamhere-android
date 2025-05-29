package com.example.iamhere.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("user_id") //not login_id!
    val userId: String,

    @SerializedName("today_attendance")
    val today_attendance: TodayLecture? // Optional 처리
)
