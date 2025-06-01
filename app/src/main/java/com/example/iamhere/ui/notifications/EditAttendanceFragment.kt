package com.example.iamhere.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iamhere.R
import com.example.iamhere.model.AttendanceRecord
import com.example.iamhere.network.AdminApi
import com.example.iamhere.network.RetrofitClient
import com.example.iamhere.network.RetrofitClient.adminApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditAttendanceFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EditAttendanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.myRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ✅ 초기 빈 어댑터 설정
        adapter = EditAttendanceAdapter(emptyList()) { _, _ -> }
        recyclerView.adapter = adapter

        // 이후 retrofit으로 데이터 받아오면 교체
        //val retrofit = Retrofit.Builder()
        //    .baseUrl("http://192.168.219.109/")  // 실서버 주소
        //    .addConverterFactory(GsonConverterFactory.create())
         //   .build()

        //val api = retrofit.create(AdminApi::class.java)

        RetrofitClient.adminApi.getAttendances().enqueue(object : Callback<List<AttendanceRecord>> {
            override fun onResponse(
                call: Call<List<AttendanceRecord>>,
                response: Response<List<AttendanceRecord>>
            ) {
                if (response.isSuccessful) {
                    val records = response.body() ?: emptyList()
                    adapter = EditAttendanceAdapter(records) { record, newStatus ->
                        adminApi.updateAttendance(record.attendance_id, mapOf("status" to newStatus))
                            .enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "수정 완료", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "수정 실패", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Toast.makeText(context, "오류: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(context, "데이터 로드 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AttendanceRecord>>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

        view.findViewById<View>(R.id.btn_admin_home).setOnClickListener {
            findNavController().navigate(R.id.navigation_admin)
        }
    }
}
