package com.example.boss.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_time_table")
data class SleepTime(
    @PrimaryKey(autoGenerate = true)
    var sleepId : Int = 0,

    @ColumnInfo(name = "sleep_start_hour")
    var startH : String = "",

    @ColumnInfo(name = "sleep_start_min")
    var startM : String = "",

    @ColumnInfo(name = "sleep_end_hour")
    var endH : String = "",

    @ColumnInfo(name = "sleep_end_min")
    var endM : String = "",
)
