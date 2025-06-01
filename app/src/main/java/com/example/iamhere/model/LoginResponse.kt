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

    @SerializedName("student_number")
    val studentNumber: String?,  // ✅ 학번

    @SerializedName("role")
    val role: String?           // ✅ 사용자 역할 (학생 / 교수)

)

