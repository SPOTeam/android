package com.example.spoteam_android.search

import RetrofitClient.getAuthToken
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentSearchBinding
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val recentSearches = mutableListOf<String>()
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var studyViewModel: StudyViewModel
    private lateinit var recommendBoardAdapter: BoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // 최근 검색어 로드
        loadRecentSearches()

        val chipGroup = binding.chipGroup2

// ChipGroup의 모든 자식 Chip의 클릭 이벤트를 비활성화
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.isClickable = false
            chip.isFocusable = false
            chip.isCheckable = false
        }

        recommendBoardAdapter = BoardAdapter(
            ArrayList(),
            onItemClick = { selectedItem ->
                Log.d("SearchFragment", "이벤트 클릭: ${selectedItem.title}")
                studyViewModel.setStudyData(
                    selectedItem.studyId,
                    selectedItem.imageUrl,
                    selectedItem.introduction
                )

                // Fragment 전환
                val fragment = DetailStudyFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onLikeClick = { selectedItem, likeButton ->
//                toggleLikeStatus(selectedItem, likeButton)
            }
        )

        binding.recommendationBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendBoardAdapter
        }

        val memberId = getMemberId(requireContext())
        fetchRecommendStudy(memberId)

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // 검색어를 SharedPreferences에 저장
                    saveSearchQuery(it)

                    // Chip 추가
                    addSearchChip(it)

                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()

                    // 키워드를 번들로 전달
                    val bundle = Bundle().apply {
                        putString("search_keyword", it)
                    }

                    // SearchResultFragment로 이동
                    val fragment = SearchResultFragment().apply {
                        arguments = bundle
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return binding.root
    }

    private fun addSearchChip(query: String) {

        val chip = Chip(requireContext()).apply {
            text = query
            isCloseIconVisible = false
            isClickable = false
            isFocusable = false
            isChecked = true
            setEnsureMinTouchTargetSize(false)

            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)  // 텍스트 크기 (14sp)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_chip_text))
        }


        chip.setChipDrawable(
            ChipDrawable.createFromAttributes(
                requireContext(), null, 0, R.style.find_ChipStyle
            )
        )

        chip.isCloseIconVisible = false
        chip.isCheckable = false
        binding.chipGroup.addView(chip, 0)
    }

    private fun saveSearchQuery(query: String) {
        val sharedPreferences = requireContext().getSharedPreferences("RecentSearches", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // 기존에 저장된 검색어 목록을 가져옴
        val searches = sharedPreferences.getStringSet("searches", LinkedHashSet()) ?: LinkedHashSet()

        Log.d("saveSearchQuery()","$searches")

        // 이미 존재하는 검색어는 제거 (중복 방지)
        searches.remove(query)

        // 검색어를 맨 앞에 추가
        searches.add(query)

        // 만약 검색어가 10개를 초과하면 오래된 검색어 제거
        if (searches.size > 10) {
            val iterator = searches.iterator()
            iterator.next() // 첫 번째 검색어 (가장 오래된 것)
            iterator.remove()
        }

        // 업데이트된 검색어 목록을 다시 저장
        editor.putStringSet("searches", searches)
        editor.apply()
    }

    private fun loadRecentSearches() {

        val sharedPreferences = requireContext().getSharedPreferences("RecentSearches", Context.MODE_PRIVATE)
        val searches = sharedPreferences.getStringSet("searches", LinkedHashSet()) ?: LinkedHashSet()
        Log.d("loadRecentSearches","$searches")
        recentSearches.clear()
        recentSearches.addAll(searches)

        // 최신 검색어들을 Chip 형태로 UI에 추가
        recentSearches.forEach { query ->
            addSearchChip(query)
        }
    }

    private fun fetchRecommendStudy(memberId: Int) {
        RetrofitClient.GetRSService.GetRecommendStudy(
            authToken = getAuthToken(),
            memberId = memberId,
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val boardItems = response.body()?.result?.content?.map { study ->
                        BoardItem(
                            studyId = study.studyId,
                            title = study.title,
                            goal = study.goal,
                            introduction = study.introduction,
                            memberCount = study.memberCount,
                            heartCount = study.heartCount,
                            hitNum = study.hitNum,
                            maxPeople = study.maxPeople,
                            studyState = study.studyState,
                            themeTypes = study.themeTypes,
                            regions = study.regions,
                            imageUrl = study.imageUrl,
                            liked = study.liked
                        )
                    } ?: emptyList()

                    if (boardItems.isNotEmpty()) {
                        recommendBoardAdapter.updateList(boardItems)
                        binding.recommendationBoard.visibility = View.VISIBLE
                    } else {
                        binding.recommendationBoard.visibility = View.GONE
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("SearchFragment", "연결 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.d("SearchFragment", "API 호출 실패: ${t.message}")
            }
        })
    }

    private fun getMemberId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        return if (currentEmail != null) {
            sharedPreferences.getInt("${currentEmail}_memberId", -1)
        } else {
            -1
        }
    }

}

