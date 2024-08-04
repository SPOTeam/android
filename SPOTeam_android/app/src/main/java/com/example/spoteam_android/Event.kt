package com.example.spoteam_android

data class Event(
    val id: Int,
    val title: String,
    val startYear: Int,
    val startMonth: Int,
    val startDay: Int,
    val startHour: Int,
    val startMinute: Int,
    val endYear: Int,
    val endMonth: Int,
    val endDay: Int,
    val endHour: Int,
    val endMinute: Int,
){
    val startDateTime: String
        get() = String.format("%02d-%02d %02d:%02d ~ ", startMonth, startDay, startHour, startMinute)

    val endDateTime: String
        get() = String.format("%02d-%02d %02d:%02d", endMonth, endDay, endHour, endMinute)
}
