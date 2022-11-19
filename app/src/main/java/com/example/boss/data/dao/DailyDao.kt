package com.example.boss.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.boss.data.entity.DailySchedule

@Dao
interface DailyDao {
    @Insert
    fun insertDaily(daily : DailySchedule)

    @Delete
    fun deleteDaily(daily : DailySchedule)

    @Update
    fun updateDaily(daily : DailySchedule)

    @Query("SELECT * FROM daily_schedule_table WHERE daily_day = :day ORDER BY dailyId ASC")
    fun getDayDaily(day : String) : List<DailySchedule>

    @Query("SELECT * FROM daily_schedule_table WHERE dailyId= :id")
    fun getDaily(id : Int) : DailySchedule

    @Query("DELETE FROM fixed_schedule_table WHERE fixedId= :id")
    fun deleteID(id:Int)
}