package com.example.boss.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.boss.data.dao.DailyDao
import com.example.boss.data.dao.FixedDao
import com.example.boss.data.entity.*

@Database(entities = [FixedSchedule::class, DailySchedule::class], version = 1, exportSchema = false)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract val fixedDao : FixedDao
    abstract val dailyDao : DailyDao

    companion object {

        @Volatile
        private var INSTANCE : ScheduleDatabase? = null

        fun getInstance(context: Context) : ScheduleDatabase {
            synchronized(this){
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ScheduleDatabase::class.java,
                        "schedule_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}