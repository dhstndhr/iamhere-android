package com.example.iamhere.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iamhere.R
import com.example.iamhere.model.AdminSummary
import com.example.iamhere.model.Alert
import com.example.iamhere.network.AdminApi
import com.example.iamhere.network.RetrofitClient
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationsFragment : Fragment() {

    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adminLayout = view.findViewById<View>(R.id.adminLayout)  // ✅ 전체 관리자 영역 레이아웃
        adminLayout.visibility = View.GONE  // ✅ 초기엔 숨김 처리

        //val retrofit = Retrofit.Builder()
        //    .baseUrl("http://127.0.0.1:8000/")  // ✅ 실제 서버 주소로 수정
        //    .addConverterFactory(GsonConverterFactory.create())
        //    .build()

        //val api = retrofit.create(AdminApi::class.java)

        // ✅ 출석 요약 정보 로드
        RetrofitClient.adminApi.getSummary().enqueue(object : Callback<AdminSummary> {
            override fun onResponse(call: Call<AdminSummary>, response: Response<AdminSummary>) {
                if (response.isSuccessful) {
                    val summary = response.body()
                    summary?.let {
                        view.findViewById<TextView>(R.id.dateText).text = it.lecture_date
                        view.findViewById<TextView>(R.id.lectureText).text = it.lecture_info
                        view.findViewById<TextView>(R.id.totalCountText).text = "총 인원: ${it.total}명"
                        view.findViewById<Chip>(R.id.attendedChip).text = "출석자 수: ${it.attended}명"
                        view.findViewById<Chip>(R.id.lateChip).text = "지각자 수: ${it.late}명"
                        view.findViewById<Chip>(R.id.absentChip).text = "결석자 수: ${it.absent}명"
                    }
                    adminLayout.visibility = View.VISIBLE  // ✅ 성공했을 때만 관리자 영역 보여줌
                } else {
                    if (response.code() == 403) {
                        Toast.makeText(
                            context,
                            "교수가 아닙니다. 관리자 페이지에 접근할 수 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    } else {
                        Toast.makeText(context, "요약 정보 수신 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AdminSummary>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

        // ✅ RecyclerView 연결
        alertRecyclerView = view.findViewById(R.id.alertRecyclerView)
        alertRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ✅ 관리자 알림 API 호출
        RetrofitClient.adminApi.getAlerts().enqueue(object : Callback<List<Alert>> {
            override fun onResponse(call: Call<List<Alert>>, response: Response<List<Alert>>) {
                if (response.isSuccessful) {
                    val alerts = response.body() ?: emptyList()
                    alertAdapter = AlertAdapter(alerts.toMutableList()) { position ->
                        alertAdapter.removeItem(position)
                    }
                    alertRecyclerView.adapter = alertAdapter
                } else {
                    Toast.makeText(context, "알림 데이터를 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Alert>>, t: Throwable) {
                Toast.makeText(context, "알림 로딩 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // ✅ 출석 수정 버튼
        val editButton = view.findViewById<Button>(R.id.btn_edit_attendance)
        editButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_edit_attendance)
        }
    }
}