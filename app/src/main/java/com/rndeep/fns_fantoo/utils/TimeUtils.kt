package com.rndeep.fns_fantoo.utils

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.Exception


object TimeUtils {

    //밀리초 일 경우
    fun diffTimeWithCurrentTime(time: Long): String {
        //현재 시간
        var timeString = "Unknown"
        try {
            val currentDate = Date().time
            val diffTime = currentDate - time

            val hours = (diffTime / (1000 * 60 * 60)).toInt()
            val mins = ((diffTime / (1000 * 60)) % 60).toInt()
            val sec = ((diffTime / 1000) % 60).toInt()

            if (hours >= 24) {
                timeString = "${hours % 24} 일 전"
            } else if (hours > 1) {
                timeString = "${hours} 시간 전"
            } else if (mins > 1) {
                timeString = "${mins} 분 전"
            } else if (sec < 10) {
                timeString = "${sec} 초 전"
            } else {
                timeString = "방금 전"
            }
        } catch (e: Exception) {

        }
        return timeString
    }

    //특정 포맷
    @SuppressLint("SimpleDateFormat")
    fun diffTimeWithCurrentTime(time: String?,
                                timeFormat: String = "yyyy-MM-dd'T'HH:mm:ss"): String {
        val oldFormat = SimpleDateFormat(timeFormat)
        //시간선 맞추기
        oldFormat.timeZone = TimeZone.getDefault()
        //바꿀 포맷
        val currentYearFormat = SimpleDateFormat("MM월 dd일")
        val diffYearFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        //리턴값
        var newDate = "Unknown"
        try {
            val oldDate = oldFormat.parse(time)
            val currentDate = Date()
            val curDateMatchFormat = oldFormat.parse(oldFormat.format(currentDate))
            val checkMinOrSec = (curDateMatchFormat.time) - oldDate.time
            if (checkMinOrSec / 1000 < 4) {
                newDate = "방금 전"
            } else if (checkMinOrSec / 1000 < 60) {
                newDate = "${(checkMinOrSec / 1000)} 초 전"
            } else if (checkMinOrSec / (60 * 1000) < 60) {
                newDate = "${(checkMinOrSec / (60 * 1000))} 분 전"
            } else if (checkMinOrSec / (60 * 60 * 1000) < 24) {
                newDate = "${checkMinOrSec / (60 * 60 * 1000)} 시간 전"
            } else if (checkMinOrSec / (24 * 60 * 60 * 1000) < 8) {
                newDate = "${checkMinOrSec / (24 * 60 * 60 * 1000)} 일 전"
            } else {
                val calendar = GregorianCalendar()
                calendar.time=oldDate
                val postYear= calendar.get(Calendar.YEAR)
                calendar.time=currentDate
                val currentYear= calendar.get(Calendar.YEAR)
                if(postYear==currentYear){
                    newDate = currentYearFormat.format(oldDate)
                }else{
                    newDate = diffYearFormat.format(oldDate)
                }
            }

        } catch (e: Exception) {
        }
        return newDate
    }
    fun changeTimeStringFormat(
        originDate: String,
        changeFormat: String,
        originFormat: String = "yyyy-MM-dd'T'HH:mm:ss"
    ) :String {
        val oldFormat = SimpleDateFormat(originFormat)
        //시간선 맞추기
        oldFormat.timeZone = TimeZone.getDefault()
        //바꿀 포맷
        val currentYearFormat = SimpleDateFormat(changeFormat)
        val oldDate = oldFormat.parse(originDate)
        var newDate=originDate
        try{
            newDate = currentYearFormat.format(oldDate)
        }catch (e :Exception){

        }
        return newDate
    }

    fun getCurrentTime(timezone:String):Long{
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat()
        cal.time = Date()
        if(timezone.isNotEmpty()) {
            sdf.timeZone = TimeZone.getTimeZone(timezone)
        }
        return sdf.parse(sdf.format(cal.time)).time
    }

    fun yearMonthDayDotString(time: Long): String {
        return getLocalDateTimeFromEpochMilli(time).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }

    fun getYearMonthFromLocalDateTime(localDateTime: LocalDateTime): String {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }

    // such as '2011-12-03' to epoch milli.
    fun getEpochMilliTimeFromDate(date: String): Long {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    // Returns the ISO date formatter that prints/parses a date-time without an offset, such as '2011-12-03T10:15:30'
    fun getParsedLocalDateTime(date: String?): LocalDateTime {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    fun getCurrentLocalDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }

    fun getLocalDateTimeFromEpochMilli(time: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
    }

}