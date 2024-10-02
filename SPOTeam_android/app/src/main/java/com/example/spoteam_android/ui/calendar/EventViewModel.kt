package com.example.spoteam_android.ui.calendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class EventViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    // 전체 이벤트를 저장할 리스트
    private var allEvents: List<Event> = emptyList()

    // 전체 이벤트 리스트를 업데이트
    fun updateEvents(newEvents: List<Event>) {
        allEvents = newEvents
    }

    // 새로운 이벤트를 추가하고, 추가된 이벤트가 있는 날짜에 맞는 이벤트를 다시 로드
    fun addEvent(event: Event) {
        EventRepository.addEvent(event)
        loadEvents(event.startYear, event.startMonth, event.startDay)
    }

    // 주어진 날짜에 맞는 이벤트를 필터링하여 _events에 반영
    fun loadEvents(year: Int, month: Int, day: Int) {
        _events.value = filterEventsByDate(allEvents, year, month, day)
    }

    // 날짜별로 이벤트를 필터링하는 메서드 (이벤트가 선택한 날짜 범위에 포함되는지 확인)
    private fun filterEventsByDate(events: List<Event>, year: Int, month: Int, day: Int): List<Event> {
        val selectedDate = Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)  // Calendar.MONTH는 0부터 시작하므로 month - 1
            set(Calendar.MILLISECOND, 0)  // 밀리초도 0으로 설정
        }.time

        return events.filter { event ->
            val eventStartDate = Calendar.getInstance().apply {
                set(event.startYear, event.startMonth - 1, event.startDay, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val eventEndDate = Calendar.getInstance().apply {
                set(event.endYear, event.endMonth - 1, event.endDay, 23, 59, 59)  // 하루의 끝 시간으로 설정
                set(Calendar.MILLISECOND, 999)
            }.time

            // 선택한 날짜가 이벤트 시작 날짜와 종료 날짜 사이에 있는지 확인
            selectedDate in eventStartDate..eventEndDate
        }.also {
        }
    }
}

