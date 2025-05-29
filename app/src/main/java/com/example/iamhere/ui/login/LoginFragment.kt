package com.example.iamhere.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.iamhere.MainActivity
import com.example.iamhere.R
import com.example.iamhere.model.LoginRequest
import com.example.iamhere.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val studentButton = view.findViewById<Button>(R.id.studentButton)
        val professorButton = view.findViewById<Button>(R.id.professorButton)
        val idEditText = view.findViewById<EditText>(R.id.editTextId)
        val pwEditText = view.findViewById<EditText>(R.id.editTextPassword)
        val loginButton = view.findViewById<Button>(R.id.loginButton)

        studentButton.setOnClickListener {
            viewModel.setUserType("학생")
            Toast.makeText(context, "학생 로그인 선택됨", Toast.LENGTH_SHORT).show()
        }

        professorButton.setOnClickListener {
            viewModel.setUserType("교수")
            Toast.makeText(context, "교수 로그인 선택됨", Toast.LENGTH_SHORT).show()
        }

        loginButton.setOnClickListener {
            Log.d("LoginFragment", "로그인 버튼 눌림")
            val id = idEditText.text.toString().trim()
            val pw = pwEditText.text.toString().trim()
            val userType = viewModel.userType.value ?: "학생"

            // ✅ 테스트 계정 검사
            if (id == "admin" && pw == "admin123") {
                Toast.makeText(requireContext(), "관리자 로그인 성공", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                // 실제 로그인 로직을 여기에 추가 가능
                val loginRequest = LoginRequest(id, pw, userType)
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.loginApi.login(loginRequest)
                        val token = response.accessToken
                        if (token != null) {
                            val prefs = requireContext().getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
                            prefs.edit().putString("access_token", token).apply()
                            Toast.makeText(context, "$userType 로그인 성공", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Toast.makeText(context, "토큰이 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
