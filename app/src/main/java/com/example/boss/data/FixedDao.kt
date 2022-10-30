package com.example.boss.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FixedDao {
    @Insert
    fun insertFixed(fixed : FixedSchedule)

    @Delete
    fun deleteFixed(fixed: FixedSchedule)

    @Update
    fun updateFixed(fixedSchedule: FixedSchedule)

    @Query("SELECT * FROM fixed_schedule_table WHERE fixed_day = :day ORDER BY fixed_startH ASC")
    fun getDayFixed(day : Int) : List<FixedSchedule>

    @Query("SELECT * FROM fixed_schedule_table WHERE fixedId= :id")
    fun getFixed(id : Int) : FixedSchedule

    @Query("DELETE FROM fixed_schedule_table WHERE fixedId= :id")
    fun deleteID(id:Int)
}