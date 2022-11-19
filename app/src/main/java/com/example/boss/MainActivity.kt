package com.example.boss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
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
    }
}