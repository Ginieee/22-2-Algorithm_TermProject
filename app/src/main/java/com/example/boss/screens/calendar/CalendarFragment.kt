package com.example.boss.screens.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.boss.MainActivity
import com.example.boss.R
import com.example.boss.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentCalendarBinding>(inflater,
            R.layout.fragment_calendar, container, false)

        calendarAdapter = CalendarAdapter(context as MainActivity)

        binding.calendar.adapter = calendarAdapter
        binding.calendar.setCurrentItem(CalendarAdapter.START_POSITION, false)

        return binding.root
    }

}