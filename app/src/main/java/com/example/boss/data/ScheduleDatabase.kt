package com.example.boss.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FixedSchedule::class], version = 1, exportSchema = false)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract val fixedDao : FixedDao

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


//        private var instance : ScheduleDatabase? = null
//
//        @Synchronized
//        fun getInstance(context : Context) : ScheduleDatabase? {
//            if(instance == null){
//                synchronized(ScheduleDatabase::class){
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        ScheduleDatabase::class.java,
//                        "schedule_database"
//                    ).build()
//                }
//            }
//            return instance
//        }
    }
}