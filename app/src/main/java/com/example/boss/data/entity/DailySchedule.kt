package com.example.boss.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "daily_schedule_table")
data class DailySchedule(
    @PrimaryKey(autoGenerate = true)
    var dailyId : Int = 0,

    @ColumnInfo(name = "daily_day")
    var day : Int = 0,

    @ColumnInfo(name = "daily_year")
    var year : Int = 0,

    @ColumnInfo(name = "daily_month")
    var month : Int = 0,

    @ColumnInfo(name = "daily_date")
    var date : Int = 0,

    @ColumnInfo(name = "daily_name")
    var name : String = "",

    @ColumnInfo(name = "daily_important")
    var important : Boolean = false,

    @ColumnInfo(name = "daily_timeH")
    var timeH : String = "",

    @ColumnInfo(name = "daily_timeM")
    var timeM : String = "",

    @ColumnInfo(name = "daily_deadline_month")
    var deadlineMonth : String = "",

    @ColumnInfo(name = "daily_deadline_date")
    var deadlineDate : String = "",

    @ColumnInfo(name = "daily_left_time")
    var left : Int = 1440
)
