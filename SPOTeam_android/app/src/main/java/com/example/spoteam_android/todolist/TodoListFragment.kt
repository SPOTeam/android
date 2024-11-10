package com.example.spoteam_android.todolist

import StudyViewModel
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentTodoListBinding
import com.example.spoteam_android.ui.calendar.EventAdapter
import com.example.spoteam_android.ui.calendar.EventViewModel
import java.util.Calendar

class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding
    private lateinit var todoViewModel: TodoViewModel
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var dateAdapter: DateAdapter
    private lateinit var myTodoAdapter: TodoAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var eventAdapter: EventAdapter
    private var selectedDate: String = "" // 선택된 날짜를 저장할 변수
    private var selectedImageView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)

        val studyId = studyViewModel.studyId.value ?: 0

        val apiService = RetrofitInstance.retrofit.create(TodoApiService::class.java)
        val repository = TodoRepository(apiService)
        val factory = TodoViewModelFactory(repository, studyId)
        todoViewModel = ViewModelProvider(this, factory).get(TodoViewModel::class.java)

        // 오늘 날짜로 selectedDate 초기화
        val calendar = Calendar.getInstance()
        selectedDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"

        // 이벤트 RecyclerView 초기화
        binding.eventrecyclerviewto.apply {
            layoutManager = LinearLayoutManager(requireContext())
            eventAdapter = EventAdapter(emptyList(), { /* 클릭 이벤트 처리 (필요시 추가) */ }, true)
            adapter = eventAdapter
        }

        // 투두 리스트 어댑터 초기화
        myTodoAdapter = TodoAdapter(requireContext(), mutableListOf()) { content ->
            todoViewModel.addTodoItem(studyId, content, selectedDate)
        }

        binding.rvMyTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myTodoAdapter
        }

        // 날짜 선택 RecyclerView 설정
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDates.layoutManager = layoutManager

        val dates = (1..31).map { DateItem(it.toString(), it.toString() == calendar.get(Calendar.DAY_OF_MONTH).toString()) }
        dateAdapter = DateAdapter(dates) { date ->
            selectedDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-$date"
            todoViewModel.onDateChanged(selectedDate)
        }
        binding.rvDates.adapter = dateAdapter

        fetchTodoList(studyId)

        todoViewModel.todoListResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.result?.content?.let { todos: List<TodoTask> ->
                // 받은 할 일 목록이 있을 경우 RecyclerView 갱신
                val todoContents = todos.map { it.content }.reversed() // 역순으로 정렬
                myTodoAdapter.updateData(todoContents)
            } ?: run {
                // 받은 결과가 없거나 비어 있을 경우 빈 리스트로 RecyclerView 갱신
                myTodoAdapter.updateData(emptyList()) // 빈 리스트 전달하여 RecyclerView 초기화
            }
        })

        setupRecyclerViews(studyId)

        // ViewTreeObserver를 사용하여 날짜 목록 가운데로 스크롤
        binding.rvDates.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.rvDates.viewTreeObserver.removeOnGlobalLayoutListener(this)
                scrollToTodayPosition()
            }
        })


        // imgbtnPlusTodolist 버튼 클릭 리스너
        binding.imgbtnPlusTodolist.setOnClickListener {
            myTodoAdapter.addTodo()
            binding.rvMyTodoList.scrollToPosition(myTodoAdapter.itemCount - 1)
        }



        val userImageViews = listOf(
            binding.imvStudyowner,
            binding.imvStudyone1,
            binding.imvStudyone2,
            binding.imvStudyone3,
            binding.imvStudyone4,
            binding.imvStudyone5,
            binding.imvStudyone6,
            binding.imvStudyone7,
            binding.imvStudyone8,
            binding.imvStudyone9,
            binding.imvStudyone10
        ).filterNotNull()



        userImageViews.forEach { imageView ->
            imageView.setOnClickListener {
                highlightImageView(imageView)
            }
        }

        val cbTodo1 = binding.cbTodo1
        val cbTodo2 = binding.cbTodo2
        val cbTodo3 = binding.cbTodo3

        binding.cbTodo1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbTodo1.paintFlags = cbTodo3.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                cbTodo1.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            } else {
                cbTodo1.paintFlags = cbTodo3.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                cbTodo1.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        }
        binding.cbTodo2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbTodo2.paintFlags = cbTodo3.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                cbTodo2.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            } else {
                cbTodo2.paintFlags = cbTodo3.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                cbTodo2.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        }



        binding.cbTodo3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbTodo3.paintFlags = cbTodo3.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                cbTodo3.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            } else {
                cbTodo3.paintFlags = cbTodo3.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                cbTodo3.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        }

        return binding.root
    }

    private fun fetchTodoList(studyId: Int) {
        // selectedDate의 형식을 yyyy-MM-dd로 보장
        val formattedDate = formatToDate(selectedDate)
        todoViewModel.fetchTodoList(studyId, page = 0, size = 10, date = formattedDate)
    }


    private fun formatToDate(date: String): String {
        val parts = date.split("-")
        return if (parts.size == 3) {
            String.format("%04d-%02d-%02d", parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } else {
            date // 이미 포맷이 맞는 경우 그대로 반환
        }
    }

    private fun setupRecyclerViews(studyId: Int) {
        val calendar = Calendar.getInstance()
        selectedDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"

        // 날짜 RecyclerView 설정
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDates.layoutManager = layoutManager

        val dates = (1..31).map { DateItem(it.toString(), it.toString() == calendar.get(Calendar.DAY_OF_MONTH).toString()) }
        dateAdapter = DateAdapter(dates) { date ->
            selectedDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-$date"
            todoViewModel.onDateChanged(selectedDate)
        }
        binding.rvDates.adapter = dateAdapter
    }

    private fun scrollToTodayPosition() {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        val todayPosition = dateAdapter.dates.indexOfFirst { it.date == today }

        if (todayPosition != -1) {
            val childView = binding.rvDates.getChildAt(0)
            val offset = binding.rvDates.width / 2 - (childView?.width ?: 0) / 2
            layoutManager.scrollToPositionWithOffset(todayPosition, offset)
        }
    }


    private fun highlightImageView(imageView: ImageView) {
        selectedImageView?.setBackgroundResource(0)
        imageView.setBackgroundResource(R.drawable.selected_border_blue)  // 선택된 ImageView에 파란 테두리 설정
        selectedImageView = imageView  // 현재 선택된 ImageView 업데이트
    }
}