package com.example.iamhere.model

data class LoginRequest(
    val login_id: String,
    val password: String,
    val userType: String
)
