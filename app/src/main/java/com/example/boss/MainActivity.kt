package com.example.boss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.boss.data.ScheduleDatabase
import com.example.boss.databinding.ActivityMainBinding
import com.example.boss.screens.calendar.CalendarFragment
import com.example.boss.screens.fixed.FixedFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    lateinit var db : ScheduleDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        db = ScheduleDatabase.getInstance(this)!!

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, CalendarFragment())
            .commit()

        clickHandler()
    }

    private fun clickHandler(){
        binding.mainCalendarBtn.setOnClickListener {
            setBtnBackground(true)
            navigation(binding.mainCalendarBtn)
        }
        binding.mainFixedBtn.setOnClickListener {
            setBtnBackground(false)
            navigation(binding.mainFixedBtn)
        }
    }

    private fun navigation(item:TextView){
        when(item.id){
            R.id.main_calendar_btn -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, CalendarFragment())
                    .commit()
            }
            R.id.main_fixed_btn -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, FixedFragment())
                    .commit()
            }
        }
    }

    private fun setBtnBackground(isCalendar:Boolean){
        if(isCalendar){
            binding.mainCalendarBtn.setBackgroundResource(R.drawable.round_rect_green)
            binding.mainCalendarBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.mainFixedBtn.setBackgroundResource(R.drawable.round_rect_white)
            binding.mainFixedBtn.setTextColor(ContextCompat.getColor(this, R.color.MainGreen))
        }
        else {
            binding.mainCalendarBtn.setBackgroundResource(R.drawable.round_rect_white)
            binding.mainCalendarBtn.setTextColor(ContextCompat.getColor(this, R.color.MainGreen))
            binding.mainFixedBtn.setBackgroundResource(R.drawable.round_rect_green)
            binding.mainFixedBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }
}