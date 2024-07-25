//package com.example.spoteam_android
//
//import android.R
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import androidx.navigation.NavController
//import com.example.spoteam_android.databinding.ActivityMainBinding
//import com.example.spoteam_android.ui.category.CategoryFragment
//import com.example.spoteam_android.ui.category.StudyFragment
//import com.example.spoteam_android.ui.mypage.BookmarkFragment
//import com.example.spoteam_android.ui.mypage.MyPageFragment
//
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var navController: NavController
//    private lateinit var recentSearchAdapter: RecentSearchAdapter
//    private lateinit var recentSearchList: MutableList<String>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        init()
//        switchFragment()
//    }
//    private fun init() {
//        val commitAllowingStateLoss = supportFragmentManager.beginTransaction()
//            .replace(R.id.main_frm, HouseFragment())
//            .commitAllowingStateLoss()
//
//        binding.mainBnv.setOnItemSelectedListener{ item ->
//            when (item.itemId) {
//
//    //              HomeFragment
//                R.id.navigation_home -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, SearchFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//    //              categoryFragment
//                R.id.navigation_category -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, CategoryFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//    //              StudyFragment
//                R.id.navigation_study -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, StudyFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//
//    //              찜Fragment
//                R.id.navigation_bookmark -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, BookmarkFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//
//    //              MypageFragment
//                R.id.navigation_mypage -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, MyPageFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//            }
//            false
//            }
//
//    }
//    fun switchFragment() {
//        // Fragment 전환
//        val searchFragment: Fragment = SearchFragment()
//        val args = Bundle()
//        args.putString("key", "value")
//        searchFragment.arguments = args
//
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.main_frm, searchFragment)
////        transaction.addToBackStack(null) // 뒤로 가기 버튼을 누를 때 이전 Fragment로 돌아가려면 추가
//        transaction.commit()
//    }
//
//}

package com.example.spoteam_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.NavController
import com.example.spoteam_android.databinding.ActivityMainBinding
import com.example.spoteam_android.ui.category.CategoryFragment
import com.example.spoteam_android.ui.category.StudyFragment
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.community.WriteContentFragment
import com.example.spoteam_android.ui.mypage.BookmarkFragment
import com.example.spoteam_android.ui.mypage.MyPageFragment
import com.example.spoteam_android.ui.study.RegisterStudyFragment
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomSheetView: View
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var navController: NavController
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var recentSearchList: MutableList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // 다른 아무 화면 클릭시 스터디 화면 사라지도록
        binding.root.setOnTouchListener { _, _ ->
            showStudyFrameLayout(false)
            true
        }

        // BottomSheetDialog 초기화
        bottomSheetView = layoutInflater.inflate(R.layout.fragment_write_content, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        binding.mainFloatingButton.setOnClickListener {
            val writeContentFragment = WriteContentFragment()
            writeContentFragment.show(supportFragmentManager, writeContentFragment.tag)
        }

        init()
        isOnCommunityHome(HouseFragment())
    }

    private fun init() {
        // 초기 화면 설정: 기본으로 HouseFragment를 보이도록 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HouseFragment())
            .commitAllowingStateLoss()

        // BottomNavigationView의 아이템 선택 리스너 설정
        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    showFragment(HouseFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    isOnCommunityHome(HouseFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_category -> {
                    showFragment(CategoryFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    isOnCommunityHome(CategoryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_study -> {
                    // StudyFragment로의 전환 없이 FrameLayout의 visibility만 변경
                    showStudyFrameLayout(true)
                    isOnCommunityHome(StudyFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_bookmark -> {
                    showFragment(BookmarkFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    isOnCommunityHome(BookmarkFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_mypage -> {
                    showFragment(MyPageFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    isOnCommunityHome(MyPageFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }

        setupButtonListeners()
    }

    // Fragment 교체 메서드
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .commitAllowingStateLoss()
    }


    // activity_main_study_fl FrameLayout의 visibility를 설정하는 메서드
    private fun showStudyFrameLayout(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        findViewById<View>(R.id.activity_main_study_fl).visibility = visibility
    }

    private fun setupButtonListeners() {
        // ImageButton 클릭 리스너 설정
        findViewById<View>(R.id.activity_main_mystudy_ib).setOnClickListener {
            showFragment(StudyFragment())
            showStudyFrameLayout(false) // StudyFragment를 보이도록 하되 FrameLayout은 숨김
        }

        findViewById<View>(R.id.activity_main_registerstudy_ib).setOnClickListener {
            showFragment(RegisterStudyFragment())
            showStudyFrameLayout(false) // RegisterFragment를 보이도록 하되 FrameLayout은 숨김
        }
    }
    
    fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainFrm.id, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

        fun isOnCommunityHome(fragment: Fragment) {
            if (fragment is CommunityHomeFragment) {
                binding.mainFloatingButton.visibility = View.VISIBLE
            } else {
                binding.mainFloatingButton.visibility = View.GONE
            }
        }


}

    
//대니/김빈

//         init()
//     }

//     private fun init() {
//         supportFragmentManager.beginTransaction()
//             .replace(binding.mainFrm.id, HouseFragment())
//             .commitAllowingStateLoss()

//         binding.mainBnv.setOnItemSelectedListener { item ->
//             when (item.itemId) {
//                 R.id.navigation_home -> {
//                     supportFragmentManager.beginTransaction()
//                         .replace(binding.mainFrm.id, HouseFragment())
//                         .commitAllowingStateLoss()
//                     true
//                 }
//                 R.id.navigation_category -> {
//                     supportFragmentManager.beginTransaction()
//                         .replace(binding.mainFrm.id, CategoryFragment())
//                         .commitAllowingStateLoss()
//                     true
//                 }
//                 R.id.navigation_study -> {
//                     supportFragmentManager.beginTransaction()
//                         .replace(binding.mainFrm.id, StudyFragment())
//                         .commitAllowingStateLoss()
//                     true
//                 }
//                 R.id.navigation_bookmark -> {
//                     supportFragmentManager.beginTransaction()
//                         .replace(binding.mainFrm.id, BookmarkFragment())
//                         .commitAllowingStateLoss()
//                     true
//                 }
//                 R.id.navigation_mypage -> {
//                     supportFragmentManager.beginTransaction()
//                         .replace(binding.mainFrm.id, MyPageFragment())
//                         .commitAllowingStateLoss()
//                     true
    
    
