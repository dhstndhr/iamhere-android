package com.example.iamhere.network

import com.example.iamhere.model.LoginRequest
import com.example.iamhere.model.LoginResponse
import retrofit2.Call

import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("login") // 실제 서버 주소 경로에 맞게 수정
    suspend fun login(@Body request: LoginRequest): LoginResponse //suspend로 비동기 처리

}