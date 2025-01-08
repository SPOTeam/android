package com.example.spoteam_android.ui.interestarea

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface InterestAreaApiService {
    @GET("/spot/search/studies/preferred-region/all")
    fun InterestArea(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("fee") fee: Int?
    ): Call<ApiResponse>

    @GET("/spot/search/studies/main/interested")
    fun getInterestedBestStudies(
    ): Call<ApiResponse>
}

interface InterestSpecificAreaApiService {
    @GET("/spot/search/studies/preferred-region/specific")
    fun InterestSpecificArea(
        @Query("regionCode") regionCode: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("fee") fee: Int?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
    ): Call<ApiResponse>
}

interface GetMemberInterestAreaApiService{
    @GET("/spot/members/region")
    fun GetInterestArea(
    ): Call<ApiResponse>
}

interface RecommendStudyApiService{
    @GET("/spot/search/studies/main/recommend")
    fun GetRecommendStudy(
    ): Call<ApiResponse>
}

interface RecruitingStudyApiService {
    @GET("/spot/search/studies/recruiting")
    fun GetRecruitingStudy(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("fee") fee: Int?
    ): Call<ApiResponse>
}

interface MyInterestStudyAllApiService {
    @GET("/spot/search/studies/interest-themes/all")
    fun GetMyInterestStudy(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("fee") fee: Int?
    ): Call<ApiResponse>
}

interface GetInterestCategoryApiService {
    @GET("/spot/members/theme")
    fun GetMyInterestStudy(
    ): Call<ApiResponse>
}

interface MyInterestStudySpecificApiService {
    @GET("/spot/search/studies/interest-themes/specific")
    fun GetMyInterestStudys(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("fee") fee: Int?,
        @Query("theme") theme : String?
    ): Call<ApiResponse>
}








