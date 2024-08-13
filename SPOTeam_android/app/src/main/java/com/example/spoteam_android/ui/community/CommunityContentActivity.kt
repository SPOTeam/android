package com.example.spoteam_android.ui.community

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.AlertInfo
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityCommunityContentBinding
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter.Companion.ENROLL
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter.Companion.LIVE
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter.Companion.UPDATE
import com.example.spoteam_android.ui.alert.CheckAppliedStudyFragment
import com.example.spoteam_android.ui.community.contentComment.ContentCommentMultiViewRVAdapter
import com.example.spoteam_android.ui.mypage.ExitStudyPopupFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommunityContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityContentBinding
    var memberId : Int = -1
    var parentCommentId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val postId = intent.getStringExtra("postInfo")

        // SharedPreferences 사용
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)

        // 현재 로그인된 사용자 정보를 로그
        memberId = if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
        Log.d("SharedPreferences", "MemberId: $memberId")

        binding = ActivityCommunityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.communityPrevIv.setOnClickListener{
            finish()
        }

        binding.communityContentMoreIv.setOnClickListener{
            showPopupMenu(it)
        }

        binding.applyCommentIv.setOnClickListener {
            submitComment(postId!!)
        }

        if (postId != null) {
            fetchContentInfo(postId)
        }
    }

    private fun submitComment(postId : String) {
        val isAnonymous = binding.writeCommentAnonymous.isChecked
        val commentContent = binding.writeCommentContentEt.text.toString().trim()

        if (commentContent.isEmpty()) {
            Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val requestBody = WriteCommentRequest(
            content = commentContent,
            parentCommentId = parentCommentId,
            anonymous = isAnonymous
        )

        // 서버에 댓글 전송
        sendCommentToServer(postId, requestBody)
    }

    private fun sendCommentToServer(postId: String, requestBody: WriteCommentRequest) {
        CommunityRetrofitClient.instance.postContentComment(postId.toInt(), memberId, requestBody)
            .enqueue(object : Callback<WriteCommentResponse> {
                override fun onResponse(
                    call: Call<WriteCommentResponse>,
                    response: Response<WriteCommentResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        Log.d("WriteComment", "${response.body()!!.result}")
                        parentCommentId = 0 // postCommentID 초기화

                        // 어댑터의 상태 초기화
                        resetAdapterState()
                        fetchContentInfo(postId)

                    } else {
                        Log.d("WriteComment", response.body()!!.message)
                    }
                }

                override fun onFailure(call: Call<WriteCommentResponse>, t: Throwable) {
                    Toast.makeText(this@CommunityContentActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        val exit = ExitStudyPopupFragment(view.context)
        val report = ReportContentFragment(view.context)
        val fragmentManager = (view.context as AppCompatActivity).supportFragmentManager
        inflater.inflate(R.menu.menu_community_home_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_report -> {
                    report.start(fragmentManager)
                    true
                }
                R.id.edit_content -> {
                    exit.start() // 편집하기로 수정
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }


    private fun fetchContentInfo(postId : String) {
        CommunityRetrofitClient.instance.getContentInfo(postId)
            .enqueue(object : Callback<ContentResponse> {
                override fun onResponse(
                    call: Call<ContentResponse>,
                    response: Response<ContentResponse>
                ) {
                    if (response.isSuccessful) {
                        val contentResponse = response.body()
                        if (contentResponse?.isSuccess == "true") {
                            val contentInfo = contentResponse.result
                            val commentInfo = contentInfo.commentResponses.comments
                            Log.d("Content", "items: $contentInfo")
                            Log.d("Comment", "items: $commentInfo")
                            if (contentInfo != null) {
                                initContentInfo(contentInfo)

                                val sortedComments = sortComments(commentInfo)
                                initMultiViewRecyclerView(sortedComments)
                            }
                        } else {
                            showError(contentResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ContentResponse>, t: Throwable) {
                    Log.e("CommunityContentActivity", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initContentInfo(contentInfo: ContentInfo) {
        binding.communityContentWriterTv.text =contentInfo.writer
        binding.communityContentDateTv.text = formatWrittenTime(contentInfo.writtenTime)
        binding.communityContentSaveNumTv.text = contentInfo.scrapCount.toString()
        binding.communityContentTitleTv.text = contentInfo.title
        binding.communityContentContentTv.text = contentInfo.content
        binding.communityContentLikeNumTv.text = contentInfo.likeCount.toString()
        binding.communityContentContentNumTv.text = contentInfo.commentCount.toString()
        binding.communityContentViewNumTv.text = contentInfo.viewCount.toString()
    }

    private fun initMultiViewRecyclerView(commentInfo: List<CommentsInfo>) {
        binding.contentCommentRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val dataRVAdapter = ContentCommentMultiViewRVAdapter(commentInfo)
        binding.contentCommentRv.adapter = dataRVAdapter

        dataRVAdapter.itemClick = object :ContentCommentMultiViewRVAdapter.ItemClick {
            override fun onItemClick(view: View, position: Int, parentId: Int) {
                parentCommentId = parentId
            }
        }
    }

    // 어댑터 상태 초기화 함수
    private fun resetAdapterState() {
        val adapter = binding.contentCommentRv.adapter as? ContentCommentMultiViewRVAdapter
        adapter?.resetClickedState() // 어댑터 내부의 clickedState 초기화
    }

    private fun sortComments(commentInfo: List<CommentsInfo>): List<CommentsInfo> {
        val sortedList = mutableListOf<CommentsInfo>()

        // 먼저 부모 댓글을 추가
        val parentComments = commentInfo.filter { it.parentCommentId == 0 }
        parentComments.forEach { parentComment ->
            // 부모 댓글을 리스트에 추가
            sortedList.add(parentComment)
            // 그 부모 댓글에 해당하는 대댓글을 추가
            val replies = commentInfo.filter { it.parentCommentId == parentComment.commentId }
            sortedList.addAll(replies)
        }
        Log.d("sortedList", "$sortedList")

        return sortedList
    }

    private fun formatWrittenTime(writtenTime: String): String {
        val formats = arrayOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        )
        var date: Date? = null
        for (format in formats) {
            try {
                date = format.parse(writtenTime)
                break
            } catch (e: ParseException) {
                Log.d("DateParseException", "${e.message}")
            }
        }
        return if (date != null) {
            SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(date)
        } else {
            writtenTime // 원본 문자열 반환
        }
    }
}