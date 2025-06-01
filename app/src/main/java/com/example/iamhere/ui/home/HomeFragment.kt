package com.example.iamhere.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.iamhere.R
import com.example.iamhere.model.Statistics
import com.example.iamhere.model.TodayLecture
import com.example.iamhere.network.RetrofitClient
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var todayCard: LinearLayout
    private lateinit var statsCard: LinearLayout
    private lateinit var pieChart: PieChart
    private lateinit var todayLectureTextView: TextView
    //이름,학번
    private lateinit var userNameTextView: TextView
    private lateinit var userIdTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        Log.d("HomeFragment", "onCreateView 호출됨")
        todayCard = view.findViewById(R.id.todayCard)
        statsCard = view.findViewById(R.id.statsCard)
        pieChart = view.findViewById(R.id.pieChart)

        userNameTextView = view.findViewById(R.id.userNameText)
        userIdTextView = view.findViewById(R.id.userIdText)

        val prefs = requireContext().getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val name = prefs.getString("user_name", "이름 없음")
        val studentId = prefs.getString("student_number", "학번 없음")

        // TextView에 설정
        userNameTextView.text = name
        userIdTextView.text = studentId

        view.findViewById<MaterialButton>(R.id.todayButton).setOnClickListener {
            flipCard(true)
        }
        view.findViewById<MaterialButton>(R.id.statsButton).setOnClickListener {
            flipCard(false)
            statsCard.postDelayed({ drawPieChart() }, 600)
        }

        view.findViewById<MaterialButton>(R.id.todayButton2).setOnClickListener {
            flipCard(true)
        }
        view.findViewById<MaterialButton>(R.id.statsButton2).setOnClickListener {
            flipCard(false)
            statsCard.postDelayed({ drawPieChart() }, 600)
        }

        todayLectureTextView = view.findViewById(R.id.todayLectureText)
        loadTodayLecture()


        return view
    }

    private fun flipCard(showFront: Boolean) {
        val scale = resources.displayMetrics.density
        todayCard.cameraDistance = 8000 * scale
        statsCard.cameraDistance = 8000 * scale

        if (showFront) {
            todayCard.visibility = View.VISIBLE
            statsCard.animate().rotationY(90f).setDuration(300).withEndAction {
                statsCard.visibility = View.GONE
                statsCard.rotationY = 0f
                todayCard.rotationY = -90f
                todayCard.animate().rotationY(0f).setDuration(300).start()
            }.start()
        } else {
            statsCard.visibility = View.VISIBLE
            todayCard.animate().rotationY(-90f).setDuration(300).withEndAction {
                todayCard.visibility = View.GONE
                todayCard.rotationY = 0f
                statsCard.rotationY = 90f
                statsCard.animate().rotationY(0f).setDuration(300).start()
            }.start()
        }
    }

    private fun drawPieChart() {
        //val userId = 1 // 실제 앱에서는 로그인 정보로 대체

        RetrofitClient.attendanceApi.getStatistics().enqueue(object : Callback<Statistics> {
            override fun onResponse(call: Call<Statistics>, response: Response<Statistics>) {
                if (response.isSuccessful) {
                    val stats = response.body() ?: return
                    val total = stats.total_lectures.toFloat().takeIf { it != 0f } ?: 1f

                    val entries = listOf(
                        PieEntry(stats.attended / total * 100f, "출석"),
                        PieEntry(stats.late / total * 100f, "지각"),
                        PieEntry(stats.missed / total * 100f, "결석")
                    )

                    val dataSet = PieDataSet(entries, "").apply {
                        colors = listOf(Color.GREEN, Color.YELLOW, Color.RED)
                        sliceSpace = 3f
                    }

                    pieChart.apply {
                        data = PieData(dataSet).apply {
                            setValueTextSize(14f)
                            setValueTextColor(Color.BLACK)
                        }
                        description.isEnabled = false
                        setDrawEntryLabels(false)
                        legend.orientation = Legend.LegendOrientation.HORIZONTAL
                        legend.isWordWrapEnabled = true
                        legend.textSize = 14f
                        invalidate()
                    }
                } else {
                    Log.e("통계 API", "응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Statistics>, t: Throwable) {
                Log.e("통계 API", "연결 실패: ${t.message}")
            }
        })
    }

    private fun loadTodayLecture() {
        //val userId = 1

        RetrofitClient.attendanceApi.getTodayLecture().enqueue(object : Callback<TodayLecture> {
            override fun onResponse(call: Call<TodayLecture>, response: Response<TodayLecture>) {
                if (response.isSuccessful) {
                    val lecture = response.body() ?: return

                    val title = lecture.title.trim()
                    val isEmpty = lecture.day.isBlank() || lecture.time.isBlank()

                    if (title == "오늘은 수업 없음" || isEmpty) {
                        todayLectureTextView.text = title
                    } else {
                        val formatted = getString(
                            R.string.lecture_info,
                            title,
                            lecture.day.trim(),
                            lecture.time.trim()
                        )
                        todayLectureTextView.text = formatted
                    }

                } else {
                    todayLectureTextView.text = "출석 카드 정보 로딩 실패"
                    Log.e("강의 API", "응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TodayLecture>, t: Throwable) {
                todayLectureTextView.text = "서버 연결 실패"
                Log.e("강의 API", "연결 실패: ${t.message}")
            }
        })
    }




}
