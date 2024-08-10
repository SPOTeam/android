package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.checklist.CheckListCategoryActivity
import com.example.spoteam_android.databinding.ActivityStartLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class StartLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                val message = when (error.toString()) {
                    AuthErrorCause.AccessDenied.toString() -> "접근이 거부 됨(동의 취소)"
                    AuthErrorCause.InvalidClient.toString() -> "유효하지 않은 앱"
                    AuthErrorCause.InvalidGrant.toString() -> "인증 수단이 유효하지 않아 인증할 수 없는 상태"
                    AuthErrorCause.Misconfigured.toString() -> "설정이 올바르지 않음(android key hash)"
                    AuthErrorCause.ServerError.toString() -> "서버 내부 에러"
                    AuthErrorCause.Unauthorized.toString() -> "앱이 요청 권한이 없음"
                    else -> "기타 에러"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                sendTokenToServer(token.accessToken)
            }
        }

        binding.loginwithkakaoBt.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        binding.loginwithspotBt.setOnClickListener {
            val intent = Intent(this, CheckListCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendTokenToServer(accessToken: String) {
        Log.d("Token", "전송할 액세스 토큰: $accessToken")  // 액세스 토큰을 로그에 출력

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/") // 서버의 URL로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(KaKaoApiService::class.java)

        api.getUserInfo(accessToken).enqueue(object : Callback<YourResponse> {
            override fun onResponse(call: Call<YourResponse>, response: Response<YourResponse>) {
                if (response.isSuccessful) {
                    val userInfo = response.body()?.result
                    if (userInfo != null) {
                        Log.d("UserInfo", "memberId: ${userInfo.memberId}, email: ${userInfo.email}")

                        // 여기서 memberId와 email을 사용해 필요한 작업 수행
                        // SharedPreferences에 저장 로직 추가 예정 - fred

                        val intent = Intent(this@StartLoginActivity, CheckListCategoryActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Log.e("Token", "토큰 전송 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<YourResponse>, t: Throwable) {
                Log.e("Token", "토큰 전송 실패: ${t.message}")
            }
        })
    }
}

// Retrofit 관련 인터페이스 정의
interface KaKaoApiService {
    @GET("/spot/members/sign-in/kakao") // 엔드포인트 경로로 변경
    fun getUserInfo(@Query("accessToken") accessToken: String): Call<YourResponse>
}

// 토큰 정보 담기
data class Tokens(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long
)

// 서버 응답 결과 담기
data class Result(
    val tokens: Tokens,
    val email: String,
    val memberId: Int
)

// 서버 응답 담기
data class YourResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
)
