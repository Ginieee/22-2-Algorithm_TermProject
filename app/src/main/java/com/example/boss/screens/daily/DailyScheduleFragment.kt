package com.example.boss.screens.daily

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boss.MainActivity
import com.example.boss.R
import com.example.boss.data.ScheduleDatabase
import com.example.boss.data.entity.DailySchedule
import com.example.boss.data.entity.FixedSchedule
import com.example.boss.databinding.FragmentDailyScheduleBinding
import com.example.boss.screens.daily.adapter.DailyFixedScheduleRVAdapter
import com.example.boss.screens.daily.adapter.DailyScheduleRVAdapter
import org.joda.time.DateTime

class DailyScheduleFragment : Fragment() {

    lateinit var mContext : Context
    lateinit var mActivity : MainActivity

    private lateinit var binding : FragmentDailyScheduleBinding
    lateinit var db : ScheduleDatabase

    lateinit var pref : SharedPreferences
    lateinit var edit : SharedPreferences.Editor

    private val SLEEP : String = "SLEEP"
    private val SLEEPSTARTH : String = "SleepStartH"
    private val SLEEPSTARTM : String = "SleepStartM"
    private val SLEEPENDH : String = "SleepEndH"
    private val SLEEPENDM : String = "SleepEndM"

    private var dateTitle : String = "YYYY.MM.DD (E)"

    var startH = ""
    var startM = ""
    var endH = ""
    var endM = ""

    var sleepTime : Int = 0
    var fixedTime : Int = 0
    var validTime : Int = 0
    var validH : String = ""
    var validM : String = ""

    private val arg : DailyScheduleFragmentArgs by navArgs()
    lateinit var date : DateTime
    private var day : Int = 0

    private val dailyFixedScheduleRVAdapter = DailyFixedScheduleRVAdapter()
    private var fixed : ArrayList<FixedSchedule> = ArrayList<FixedSchedule>()

    private val dailyTodoRVAdapter = DailyScheduleRVAdapter()
    private var daily : ArrayList<DailySchedule> = ArrayList<DailySchedule>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
            mActivity = activity as MainActivity
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentDailyScheduleBinding>(inflater, R.layout.fragment_daily_schedule, container, false)

        db = ScheduleDatabase.getInstance(requireContext())!!

        date = DateTime(arg.year, arg.month, arg.date, 0,0)
        day = arg.day
        dateTitle = date.toString("yyyy.MM.dd (E)")
        binding.dailyDate.text = dateTitle

        pref = mContext.getSharedPreferences(SLEEP, MODE_PRIVATE)
        edit = pref.edit()

        getPref()
        setFixedAdapter()

        clickHandler()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setDailyAdapter()
    }

    private fun clickHandler() {
        binding.dailyAddBtn.setOnClickListener {
            val action = DailyScheduleFragmentDirections.actionDailyScheduleFragmentToAddDailyFragment(arg.year, arg.month, arg.date, arg.day)
            findNavController().navigate(action)
        }
    }

    private fun setDailyAdapter() {
        binding.dailyTodoRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.dailyTodoRv.adapter = dailyTodoRVAdapter

        getDailyFromDB()
    }

    private fun setFixedAdapter(){
        binding.dailyFixedRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.dailyFixedRv.adapter = dailyFixedScheduleRVAdapter

        getFixedFromDB()
    }

    private fun getDailyFromDB() {
        var forDB : Thread = Thread {
            daily = db.dailyDao.getDateDaily(arg.year, arg.month, arg.date) as ArrayList
            dailyTodoRVAdapter.addDaily(daily)
            requireActivity().runOnUiThread {
                dailyTodoRVAdapter.notifyDataSetChanged()
            }
        }
        forDB.start()
    }

    private fun getFixedFromDB(){
        var forDB : Thread = Thread {
            fixed = db.fixedDao.getDayFixed(day) as ArrayList
            dailyFixedScheduleRVAdapter.addFixed(fixed)
            requireActivity().runOnUiThread {
                dailyFixedScheduleRVAdapter.notifyDataSetChanged()
            }
        }
        forDB.start()

        try {
            forDB.join()
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }
        getValidTime()
    }


    private fun getPref(){
        startH = pref.getString(SLEEPSTARTH,"00")!!
        startM = pref.getString(SLEEPSTARTM,"00")!!
        endH = pref.getString(SLEEPENDH,"00")!!
        endM = pref.getString(SLEEPENDM,"00")!!
    }

    private fun sumSleepTime(){
        sleepTime = (endH.toInt() * 60 + endM.toInt()) - (startH.toInt() * 60 + startM.toInt())
        Log.d("SUM_SLEEP", sleepTime.toString())
        //Log.d("SLEEPCHECK", sleepTime.toString() + "총 이거고 시간은 " + startH.toInt() + "시" + startM.toInt() + "분")
    }

    private fun sumFixedTime() {
        fixedTime = 0
        var time : Int = 0
        for (i in fixed) {
            Log.d("SUM_EACH_FIXED_I", i.toString())
            time = (i.endH.toInt() * 60 + i.endM.toInt()) - (i.startH.toInt() * 60 + i.startM.toInt())
            fixedTime = fixedTime + time
            Log.d("SUM_EACH_FIXED", fixedTime.toString())
        }
        Log.d("SUM_FIXED", fixedTime.toString())

    }

    private fun getValidTime() {
        sumSleepTime()
        sumFixedTime()

        validTime = (24 * 60) - (sleepTime + fixedTime)
        Log.d("SUM_VALID", validTime.toString())
        var hour = validTime / 60
        var min = validTime % 60

        if (hour < 10) validH = "0" + hour
        else validH = hour.toString()

        if (min < 10) validM = "0" + min
        else validM = min.toString()

        binding.dailyScheduleValidTime.text = validH + "시간 " + validM + "분"
    }
}