package com.example.spoteam_android.ui.study

import StudyAdapter
import StudyApiService
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.StudyItem
import com.example.spoteam_android.StudyResponse
import com.example.spoteam_android.databinding.FragmentStudyBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StudyFragment : Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!
    private lateinit var studyAdapter: StudyAdapter
    private var itemList = ArrayList<StudyItem>()
    private var currentPage = 0
    private val size = 4 // 페이지당 항목 수

    // Retrofit API Service
    private lateinit var studyApiService: StudyApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = getRetrofit()
        studyApiService = retrofit.create(StudyApiService::class.java)

        // 어댑터 설정
        studyAdapter = StudyAdapter(ArrayList())

        // RecyclerView 설정
        binding.fragmentStudyRv.apply {
            adapter = studyAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        // 페이지 전환 버튼 설정
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchStudyData() // 이전 페이지 데이터를 가져옴
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < getTotalPages() - 1) {
                currentPage++
                Log.d("StudyFragment", "Next Page Clicked: currentPage = $currentPage")
                fetchStudyData() // 다음 페이지 데이터를 가져옴
            }
        }

        // 초기 데이터 로드
        fetchStudyData()
    }

    private fun getRetrofit(): Retrofit {
        val authToken = "eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6NywidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzIzNTk5NTMzLCJleHAiOjE3MjM2ODU5MzN9.ha6TKUawUA1iBopNeRMKnL-2Ktj_xf1lsNP4pVxI9Bg"
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun fetchStudyData() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)

            if (memberId != -1) {
                studyApiService.getStudies(memberId.toString(), currentPage, size).enqueue(object : Callback<StudyResponse> {
                    override fun onResponse(call: Call<StudyResponse>, response: Response<StudyResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { studyResponse ->
                                itemList.clear()
                                itemList.addAll(studyResponse.result.content.map {
                                    StudyItem(
                                        title = it.title,
                                        introduction = it.introduction,
                                        studyTO = 10,
                                        memberCount = it.memberCount,
                                        heartCount = it.heartCount,
                                        hitNum = it.hitNum
                                    )
                                })

                                // RecyclerView 업데이트
                                studyAdapter.updateList(itemList)
                                Log.d("StudyFragment", "List updated with new data.")

                                // 페이지 UI 업데이트
                                updatePageNumberUI(studyResponse.result.totalPages)
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<StudyResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePageNumberUI(totalPages: Int) {
        binding.currentPage.text = "${currentPage + 1} / $totalPages"

        // 이전 페이지 버튼 활성화/비활성화
        binding.previousPage.isEnabled = currentPage > 0
        binding.previousPage.setTextColor(resources.getColor(
            if (currentPage > 0) R.color.active_color else R.color.disabled_color,
            null
        ))

        // 다음 페이지 버튼 활성화/비활성화
        binding.nextPage.isEnabled = currentPage < totalPages - 1
        binding.nextPage.setTextColor(resources.getColor(
            if (currentPage < totalPages - 1) R.color.active_color else R.color.disabled_color,
            null
        ))
    }


    private fun getTotalPages(): Int {
        return (itemList.size + size - 1) / size // 올바른 페이지 수 계산
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

