package com.example.boss.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fixed_schedule_table")
data class FixedSchedule(
    @PrimaryKey(autoGenerate = true)
    var fixedId : Int = 0,

    @ColumnInfo(name = "fixed_day")
    var day : Int = 0,

    @ColumnInfo(name = "fixed_name")
    var name : String = "",

    @ColumnInfo(name = "fixed_startH")
    var startH : String = "",

    @ColumnInfo(name = "fixed_startM")
    var startM : String = "",

    @ColumnInfo(name = "fixed_endH")
    var endH : String = "",

    @ColumnInfo(name = "fixed_endM")
    var endM : String = ""
)
