package com.example.spoteam_android.ui.study

import ApplyStudyDialog
import StudyApiService
import StudyViewModel
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.DetailStudyHomeAdapter
import com.example.spoteam_android.HouseFragment
import com.example.spoteam_android.IsAppliedResponse
import com.example.spoteam_android.MemberResponse
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.R
import com.example.spoteam_android.RecentAnnounceResponse
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.SceduleItem
import com.example.spoteam_android.ScheduleListResponse
import com.example.spoteam_android.databinding.FragmentDetailStudyHomeBinding
import com.example.spoteam_android.ui.mypage.PurposeUploadComplteDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailStudyHomeFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyHomeBinding
    private lateinit var profileAdapter: DetailStudyHomeProfileAdapter
    private lateinit var scheduleAdapter: DetailStudyHomeAdapter
    private val page = 0
    private val size = 4
    val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailStudyHomeBinding.inflate(inflater, container, false)

        setupViews()


        // ViewModel에서 studyId를 관찰하고 변경될 때마다 fetchStudyMembers 호출
        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            Log.d("DetailStudyHomeFragment", "Received studyId from ViewModel: $studyId")
            if (studyId != null) {
                fetchIsApplied(studyId)
                fetchStudySchedules(studyId)
                fetchStudyMembers(studyId)
                fetchRecentAnnounce(studyId)
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fragmentDetailStudyHomeRegisterBt.setOnClickListener {
            showCompletionDialog()
        }

        return binding.root
    }

    private fun setupViews() {
        //프로필 업데이트
        profileAdapter = DetailStudyHomeProfileAdapter(ArrayList())
        binding.fragmentDetailStudyHomeProfileRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentDetailStudyHomeProfileRv.adapter = profileAdapter

        //스케쥴 업데이트
        scheduleAdapter = DetailStudyHomeAdapter(ArrayList())
        binding.fragmentDetailStudyHomeScheduleRv.layoutManager = LinearLayoutManager(requireContext())
        binding.fragmentDetailStudyHomeScheduleRv.adapter = scheduleAdapter

        studyViewModel.studyIntroduction.observe(viewLifecycleOwner) { introduction ->
            binding.fragmentDetailStudyHomeIntroduceTv.text = introduction
        }
    }

    private fun fetchStudyMembers(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentMemberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        api.getStudyMembers(studyId).enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { memberResponse ->
                        val members = memberResponse.result?.members ?: emptyList()
                        members.forEach { member ->
                            Log.d("StudyMembers", "Member ID: ${member.memberId}, Nickname: ${member.nickname}, Profile Image: ${member.profileImage ?: "NULL"}")
                        }


                        // 프로필 어댑터 업데이트
                        profileAdapter.updateList(members.map {
                            ProfileItem(profileImage = it.profileImage, nickname = it.nickname)
                        })

                        // 멤버 ID 리스트에서 현재 사용자의 ID와 비교
                        val memberIds = members.map { it.memberId }
                        val isMember = memberIds.contains(currentMemberId)

                        // maxPeople과 memberCount 값을 가져오기 위해 ViewModel을 옵저빙
                        val maxPeople = studyViewModel.maxPeople.value
                        val memberCount = studyViewModel.memberCount.value

                        // 현재 사용자가 멤버인지 확인하거나, 최대 인원을 초과한 경우 버튼 숨김 처리
                        val shouldHideButton = isMember || (maxPeople != null && memberCount != null && memberCount >= maxPeople)
                        binding.fragmentDetailStudyHomeRegisterBt.visibility = if (shouldHideButton) View.GONE else View.VISIBLE


                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch study members", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun fetchIsApplied(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        api.getIsApplied(studyId).enqueue(object : Callback<IsAppliedResponse> {
            override fun onResponse(call: Call<IsAppliedResponse>, response: Response<IsAppliedResponse>) {
                if (response.isSuccessful) {
                    val isApplied = response.body()?.result?.applied ?: false
                    updateRegisterButton(isApplied)
                } else {
                    Toast.makeText(requireContext(), "Failed to check application status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<IsAppliedResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRegisterButton(isApplied: Boolean) {
        val button = binding.fragmentDetailStudyHomeRegisterBt


        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE  // 사각형 형태
            cornerRadius = 16f  // 둥근 모서리 반경 (픽셀 단위)
        }

        if (isApplied) {
            button.isEnabled = false
            button.text = "신청완료"

            backgroundDrawable.setColor(ContextCompat.getColor(requireContext(), R.color.white))
            backgroundDrawable.setStroke(3, ContextCompat.getColor(requireContext(), R.color.button_disabled_text)) // 테두리 설정

            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.button_disabled_text))
        } else {
            button.isEnabled = true
            button.text = "신청하기"

            backgroundDrawable.setColor(ContextCompat.getColor(requireContext(), R.color.white))
            backgroundDrawable.setStroke(3, ContextCompat.getColor(requireContext(), R.color.button_enabled_text)) // 테두리 설정

            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.MainColor_01))
        }

        button.background = backgroundDrawable
    }



    private fun fetchStudySchedules(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        api.getStudySchedules(studyId, page, size).enqueue(object : Callback<ScheduleListResponse> {
            override fun onResponse(call: Call<ScheduleListResponse>, response: Response<ScheduleListResponse>) {
                if (response.isSuccessful) {
                    val scheduleListResponse = response.body()
                    val schedules = scheduleListResponse?.result?.schedules

                    if (schedules.isNullOrEmpty()) {
                        // Schedules가 없을 때
                        binding.fragmentDetailStudyHomeScheduleRv.visibility = View.GONE
                    } else {
                        // 현재 날짜 이후의 일정만 필터링
                        val validSchedules = schedules.filter { schedule ->
                            val scheduleDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(schedule.staredAt)
                            scheduleDate?.after(Calendar.getInstance().time) ?: false
                        }

                        if (validSchedules.isEmpty()) {
                            binding.fragmentDetailStudyHomeScheduleRv.visibility = View.GONE
                        } else {
                            binding.fragmentDetailStudyHomeScheduleRv.visibility = View.VISIBLE

                            val scheduleItems = validSchedules.map { schedule ->
                                SceduleItem(
                                    dday = calculateDday(schedule.staredAt),
                                    day = formatDate(schedule.staredAt),
                                    scheduleContent = schedule.title,
                                    concreteTime = formatTime(schedule.staredAt),
                                    place = schedule.location
                                )
                            }
                            scheduleAdapter.updateList(ArrayList(scheduleItems))
                        }
                    }
                } else {
                    // 응답이 실패한 경우
                    Toast.makeText(requireContext(), "Failed to fetch study schedules", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ScheduleListResponse>, t: Throwable) {
                // 네트워크 실패 등
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchRecentAnnounce(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        api.getRecentAnnounce(studyId).enqueue(object : Callback<RecentAnnounceResponse> {
            override fun onResponse(call: Call<RecentAnnounceResponse>, response: Response<RecentAnnounceResponse>) {
                if (response.isSuccessful) {
                    val announceResponse = response.body()
                    val recentAnnounce = announceResponse?.result

                    if (recentAnnounce?.title.isNullOrEmpty()) {
                        binding.fragmentDetailStudyHomeTitleTv.text = "최근 공지가 없습니다"
                    } else {
                        binding.fragmentDetailStudyHomeTitleTv.text = recentAnnounce?.title ?: "최근 공지가 없습니다"
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load announcement: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecentAnnounceResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Null 체크를 추가한 D-day 변경 로직
    private fun calculateDday(staredAt: String?): String {
        if (staredAt.isNullOrEmpty()) {
            return "N/A" // 기본값 또는 에러 메시지
        }

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val scheduleDateTime = formatter.parse(staredAt)

        // 자정을 기준으로 하는 날짜 계산을 위해, 시간, 분, 초를 0으로 설정
        //왜냐하면 이걸 안하면 다음주 토요일 오후 3시에 약속이있어도 지금 시각이 오후 4시면 디데이 오류
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDateTime
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val scheduleDate = calendar.time

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        val todayDate = today.time

        val diff = scheduleDate.time - todayDate.time
        val daysLeft = (diff / (1000 * 60 * 60 * 24)).toInt()

        return if (daysLeft > 0) {
            "D-${daysLeft}"
        } else if (daysLeft == 0) {
            "D-day"
        } else {
            "D+${-daysLeft}"
        }
    }

    // Null 체크를 추가한 날짜 변경 로직
    private fun formatDate(startedAt: String?): String {
        if (startedAt.isNullOrEmpty()) {
            return "N/A" // 기본값 또는 에러 메시지
        }

        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("M/d (EEE)", Locale.getDefault())

        val date = inputFormatter.parse(startedAt)
        return outputFormatter.format(date)
    }

    // Null 체크를 추가한 시간 변경 로직
    private fun formatTime(staredAt: String?): String {
        if (staredAt.isNullOrEmpty()) {
            return "N/A" // 기본값 또는 에러 메시지
        }

        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("M/d h:mma", Locale.US) // Locale.US를 사용하여 AM/PM을 영어로 유지

        val date = inputFormatter.parse(staredAt)
        return outputFormatter.format(date)
    }

    private fun showCompletionDialog() {
        val dialog = ApplyStudyDialog(requireContext(), this)
        dialog.start {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frm, HouseFragment())
            transaction.addToBackStack(null)  // 백스택에 추가하여 뒤로 가기 가능하게 함
            transaction.commit()
        }
    }
}
