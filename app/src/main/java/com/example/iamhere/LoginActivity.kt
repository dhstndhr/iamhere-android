package com.example.iamhere

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.iamhere.ui.login.LoginFragment

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // 로그인 레이아웃 연결

        // 처음 실행 시 LoginFragment 띄우기
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commit()
    }

}

