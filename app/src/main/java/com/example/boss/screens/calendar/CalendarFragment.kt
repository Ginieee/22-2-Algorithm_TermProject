package com.example.boss.screens.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
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

        showTopVisible(true)

        calendarAdapter = CalendarAdapter(context as MainActivity)

        binding.calendar.adapter = calendarAdapter
        binding.calendar.setCurrentItem(CalendarAdapter.START_POSITION, false)

        binding.mainFixedBtn.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_fixedFragment)
        }

        return binding.root
    }

    private fun showTopVisible(bool : Boolean) {
        val topLayout : TextView = requireActivity().findViewById(R.id.toolbar_title)
        if (bool == true) {
            topLayout.visibility = View.VISIBLE
        } else {
            topLayout.visibility = View.GONE
        }
    }

}