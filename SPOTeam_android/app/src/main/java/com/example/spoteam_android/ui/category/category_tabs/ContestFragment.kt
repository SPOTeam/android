package com.example.spoteam_android.ui.category.category_tabs

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.LikeResponse
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCategoryStudyContentBinding
import com.example.spoteam_android.ui.community.CategoryStudyDetail
import com.example.spoteam_android.ui.community.CategoryStudyResponse
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import com.example.spoteam_android.ui.study.DetailStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContestFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentCategoryStudyContentBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var studyApiService: StudyApiService

    private var currentPage = 0
    private val size = 4 // 페이지당 항목 수
    private var totalPages = 0
    private var isLoading = false
    private var selectedCategory: String = "ALL"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryStudyContentBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        binding.contentFilterSp.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.contentFilterSp.adapter = adapter
        }

        // 어댑터 초기화 및 클릭 리스너 설정
        val categoryStudyAdapter = CategoryStudyContentRVAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        })

        binding.communityCategoryContentRv.adapter = categoryStudyAdapter

        categoryStudyAdapter.setItemClickListener(object : CategoryStudyContentRVAdapter.OnItemClickListener {
            override fun onItemClick(data: CategoryStudyDetail) {
                // DetailStudyFragment로 이동
                studyViewModel.setStudyData(data.studyId, data.imageUrl, data.introduction)

                val detailStudyFragment = DetailStudyFragment()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailStudyFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        })
        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(requireContext())

        setupPageNavigationButtons()

        fetchBestCommunityContent("공모전", currentPage, size, selectedCategory)

        return binding.root
    }

    private fun setupPageNavigationButtons() {
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchBestCommunityContent("공모전", currentPage, size, selectedCategory)
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                fetchBestCommunityContent("공모전", currentPage, size, selectedCategory)
            }
        }
    }

    private fun fetchBestCommunityContent(theme: String, page: Int, size: Int, sortBy: String) {
        isLoading = true
        CommunityRetrofitClient.instance.getCategoryStudy(theme, page, size, sortBy)
            .enqueue(object : Callback<CategoryStudyResponse> {
                override fun onResponse(
                    call: Call<CategoryStudyResponse>,
                    response: Response<CategoryStudyResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { categoryStudyResponse ->
                            if (categoryStudyResponse.isSuccess == "true") {
                                val contentList = categoryStudyResponse.result?.content ?: emptyList()
                                totalPages = categoryStudyResponse.result.totalPages

                                val totalElements = categoryStudyResponse.result.totalElements
                                binding.contentCountTv.text = totalElements.toString()

                                if (contentList.isNotEmpty()) {
                                    updateRecyclerView(contentList)
                                    binding.emptyTv.visibility = View.GONE
                                } else if (currentPage == 0) {
                                    binding.emptyTv.visibility = View.VISIBLE
                                }

                                updatePageNumberUI()
                            }
                        }
                    } else {
                        showLog(response.code().toString())
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<CategoryStudyResponse>, t: Throwable) {
                    showLog(t.message)
                    isLoading = false
                }
            })
    }

    private fun updateRecyclerView(contentList: List<CategoryStudyDetail>) {
        val adapter = binding.communityCategoryContentRv.adapter as? CategoryStudyContentRVAdapter
        adapter?.updateList(contentList)
    }

    private fun updatePageNumberUI() {
        binding.currentPage.text = (currentPage + 1).toString()

        binding.previousPage.isEnabled = currentPage > 0
        binding.previousPage.setTextColor(resources.getColor(
            if (currentPage > 0) R.color.active_color else R.color.disabled_color,
            null
        ))

        binding.nextPage.isEnabled = currentPage < totalPages - 1
        binding.nextPage.setTextColor(resources.getColor(
            if (currentPage < totalPages - 1) R.color.active_color else R.color.disabled_color,
            null
        ))
    }

    private fun showLog(message: String?) {
        Toast.makeText(requireContext(), "ContestFragment: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position).toString()
        selectedCategory = when (selectedItem) {
            "전체" -> "ALL"
            "모집중" -> "RECRUITING"
            "모집완료" -> "COMPLETED"
            "조회수순" -> "HIT"
            "관심순" -> "LIKED"
            else -> "ALL"
        }
        currentPage = 0
        fetchBestCommunityContent("공모전", currentPage, size, selectedCategory)
    }

    private fun toggleLikeStatus(studyItem: CategoryStudyDetail, likeButton: ImageView) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val memberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        if (memberId != -1) {
            studyApiService.toggleStudyLike(studyItem.studyId, memberId)
                .enqueue(object : Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { likeResponse ->
                                // 서버에서 반환된 상태에 따라 하트 아이콘 및 CategoryStudyDetail의 liked 상태 업데이트
                                val newStatus = likeResponse.result.status
                                studyItem.liked = newStatus == "LIKE"
                                val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                                likeButton.setImageResource(newIcon)

                                // heartCount 즉시 증가 또는 감소
                                studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1

                                // 변경된 항목을 어댑터에 알림
                                val adapter = binding.communityCategoryContentRv.adapter as CategoryStudyContentRVAdapter
                                val position = adapter.dataList.indexOf(studyItem)
                                if (position != -1) {
                                    adapter.notifyItemChanged(position)
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "찜 상태 업데이트 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(requireContext(), "회원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedCategory = "ALL"
    }
}
