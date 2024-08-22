package com.example.spoteam_android.ui.community

import okhttp3.MultipartBody

/*********게시글 작성 Request********/
data class WriteContentRequest(
    val title: String,
    val content : String,
    val type: String,
    val anonymous : Boolean
)

/*********게시글 댓글 작성 Request********/
data class WriteCommentRequest(
    val content: String,
    val parentCommentId : Int,
    val anonymous : Boolean
)

/*********내 스터디 게시글 작성 Request********/
data class StudyWriteContentRequest(
    val isAnnouncement: Boolean,
    val theme: String,
    val title: String,
    val content: String,
    val images: List<MultipartBody.Part?>
)

/*********스터디 게시글 댓글 작성 Request********/
data class WriteStudyCommentRequest(
    val isAnonymous : Boolean,
    val content : String
)