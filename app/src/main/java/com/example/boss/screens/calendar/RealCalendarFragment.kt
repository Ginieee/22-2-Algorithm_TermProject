package com.example.boss.screens.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.boss.MainActivity
import com.example.boss.R
import com.example.boss.databinding.FragmentRealCalendarBinding
import com.example.boss.utils.CalendarUtils.Companion.getMonthList
import org.joda.time.DateTime

class RealCalendarFragment : Fragment() {

    private var millis: Long = 0L

    private lateinit var binding : FragmentRealCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            millis = it.getLong(MILLIS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentRealCalendarBinding>(inflater, R.layout.fragment_real_calendar, container, false)

        binding.millis.text = DateTime(millis).toString("yyyy-MM")
        binding.calendarView.initCalendar(DateTime(millis), getMonthList(DateTime(millis)))

        return binding.root
    }

    companion object {

        private const val MILLIS = "MILLIS"

        fun newInstance(millis: Long) = RealCalendarFragment().apply {
            arguments = Bundle().apply {
                putLong(MILLIS, millis)
            }
        }
    }
}