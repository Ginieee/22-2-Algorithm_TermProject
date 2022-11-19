package com.example.boss.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "daily_schedule_table")
data class DailySchedule(
    @PrimaryKey(autoGenerate = true)
    var dailyId : Int = 0,

    @ColumnInfo(name = "daily_day")
    var day : String = "",

    @ColumnInfo(name = "daily_name")
    var name : String = "",

    @ColumnInfo(name = "important")
    var important : Boolean = false,

    @ColumnInfo(name = "timeH")
    var timeH : String = "",

    @ColumnInfo(name = "timeM")
    var timeM : String = "",

    @ColumnInfo(name = "deadline_month")
    var deadlineMonth : String = "",

    @ColumnInfo(name = "deadline_date")
    var deadlineDate : String = "",
)
