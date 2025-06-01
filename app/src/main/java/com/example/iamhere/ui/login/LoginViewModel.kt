package com.example.iamhere.ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iamhere.model.LoginResponse
import com.example.iamhere.network.LoginApi
import com.example.iamhere.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel (application: Application) : AndroidViewModel(application) {


    // JWT 포함된 Retrofit 클라이언트로 loginApi 생성
    private val prefs = application.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private val loginApi = RetrofitClient.getClient(prefs).create(LoginApi::class.java)

    // 로그인 결과 LiveData
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult


    private val _userType = MutableLiveData("학생")  // 기본값: 학생
    val userType: LiveData<String> = _userType

    fun setUserType(type: String) {
        _userType.value = type
    }
    fun login(id: String, password: String) {
        //_userType.value = userType
        // 코루틴으로 비동기 처리
        viewModelScope.launch {
            try {
                val response =
                    loginApi.login(com.example.iamhere.model.LoginRequest(id, password))
                // Retrofit suspend 함수는 Response 래핑 안 하면 성공 시 바로 결과 반환됨
                prefs.edit().putString("access_token", response.accessToken).apply()
                _loginResult.postValue(Result.success(response))
            } catch (e: Exception) {
                _loginResult.postValue(Result.failure(e))
            }
        }
    }
}

