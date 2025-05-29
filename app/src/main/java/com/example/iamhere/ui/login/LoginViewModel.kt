package com.example.iamhere.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val _userType = MutableLiveData("학생")  // 기본값: 학생
    val userType: LiveData<String> = _userType

    fun setUserType(type: String) {
        _userType.value = type
    }
}

