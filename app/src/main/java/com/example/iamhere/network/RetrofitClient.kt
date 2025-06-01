package com.example.iamhere.network

import AuthInterceptor
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
object RetrofitClient {

    //private const val BASE_URL = "http://34.64.121.178:8000/"  // ← 예시 GCP IP

    //private const val BASE_URL = "http://10.0.2.2:8000/"  //실제 디바이스의 경우 네트워크의 로컬 ip가필요
    private const val BASE_URL = "http://192.168.219.109:8000/"
    fun getClient(prefs: SharedPreferences,baseUrl : String = BASE_URL): Retrofit {
        val authInterceptor = AuthInterceptor(prefs)


        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // ⬅ JWT 토큰 자동 추가
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    lateinit var loginApi: LoginApi
    lateinit var attendanceApi: AttendanceApi
    lateinit var adminApi: AdminApi

    fun initApis(prefs: SharedPreferences) {
        val retrofit = getClient(prefs)
        loginApi = retrofit.create(LoginApi::class.java)
        attendanceApi = retrofit.create(AttendanceApi::class.java)
        adminApi = retrofit.create(AdminApi::class.java)
    }

}
/*
val attendanceApi: AttendanceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AttendanceApi::class.java)
    }
    // loginApi 초기화
    val loginApi: LoginApi by lazy {
        Retrofit.Builder()  // Retrofit 객체 다시 생성
            .baseUrl(BASE_URL)  // 기본 URL 설정
            .addConverterFactory(GsonConverterFactory.create())  // GsonConverterFactory 추가
            .build()
            .create(LoginApi::class.java)  // LoginApi 인터페이스 구현
    }
}
*/