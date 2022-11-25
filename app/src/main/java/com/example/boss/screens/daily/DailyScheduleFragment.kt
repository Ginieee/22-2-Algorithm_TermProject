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
import com.example.boss.data.entity.OrderedSchedule
import com.example.boss.databinding.FragmentDailyScheduleBinding
import com.example.boss.screens.daily.adapter.DailyFixedScheduleRVAdapter
import com.example.boss.screens.daily.adapter.DailyScheduleRVAdapter
import org.joda.time.DateTime
import kotlin.math.min
import kotlin.math.sign

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

    var useValidTime : Int = 0

    private val arg : DailyScheduleFragmentArgs by navArgs()
    lateinit var date : DateTime
    private var day : Int = 0

    private val dailyFixedScheduleRVAdapter = DailyFixedScheduleRVAdapter()
    private var fixed : ArrayList<FixedSchedule> = ArrayList<FixedSchedule>()

    private val dailyTodoRVAdapter = DailyScheduleRVAdapter()
    private var daily : ArrayList<DailySchedule> = ArrayList<DailySchedule>()
    private var orderedDaily : ArrayList<OrderedSchedule> = ArrayList<OrderedSchedule>()

    var now_fixed = 0
    var fixed_num = 0
    private var now_time_H : Int = 0
    private var now_time_M : Int = 0

    var result_start_H = arrayListOf<String>()
    var result_start_M = arrayListOf<String>()
    var result_end_H =  arrayListOf<String>()
    var result_end_M =  arrayListOf<String>()
    var result_work = arrayListOf<Int>()

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

        clickHandler()
        setItemClickListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        orderedDaily.clear()
        setAdapter()
    }

    private fun clickHandler() {
        binding.dailyAddBtn.setOnClickListener {
            val action = DailyScheduleFragmentDirections.actionDailyScheduleFragmentToAddDailyFragment(arg.year, arg.month, arg.date, arg.day, false, -1)
            findNavController().navigate(action)
        }
    }

    private fun setAdapter(){
        binding.dailyFixedRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.dailyFixedRv.adapter = dailyFixedScheduleRVAdapter

        binding.dailyTodoRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.dailyTodoRv.adapter = dailyTodoRVAdapter

        getFixedAndDailyFromDB()
    }

    private fun setItemClickListener() {
        dailyTodoRVAdapter.setMyItemClickListener(object : DailyScheduleRVAdapter.MyItemClickListener {
            override fun onSendId(id: Int) {
                val action = DailyScheduleFragmentDirections.actionDailyScheduleFragmentToAddDailyFragment(arg.year, arg.month, arg.date, arg.day, true, id)
                findNavController().navigate(action)
            }
        })
    }

    private fun getFixedAndDailyFromDB(){
        var forFixedDB : Thread = Thread {
            fixed = db.fixedDao.getDayFixed(day) as ArrayList
            dailyFixedScheduleRVAdapter.addFixed(fixed)
            requireActivity().runOnUiThread {
                dailyFixedScheduleRVAdapter.notifyDataSetChanged()
            }
        }
        forFixedDB.start()

        try {
            forFixedDB.join()
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }
        fixed_num = fixed.size
        getValidTime()

        var forDailyDB : Thread = Thread {
            daily = db.dailyDao.getDateDaily(arg.year, arg.month, arg.date) as ArrayList
//            dailyTodoRVAdapter.addDaily(daily)
//            requireActivity().runOnUiThread {
//                dailyTodoRVAdapter.notifyDataSetChanged()
//            }
        }
        forDailyDB.start()

        try {
            forDailyDB.join()
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }
        print_daily_work()
        dailyTodoRVAdapter.addDaily(orderedDaily)
        dailyTodoRVAdapter.notifyDataSetChanged()

    }


    private fun getPref(){
        startH = pref.getString(SLEEPSTARTH,"00")!!
        startM = pref.getString(SLEEPSTARTM,"00")!!
        endH = pref.getString(SLEEPENDH,"00")!!
        endM = pref.getString(SLEEPENDM,"00")!!

        now_time_H = endH.toInt();
        now_time_M = endM.toInt();
    }

    private fun sumSleepTime(){
        var today = 24 * 60 - (startH.toInt() * 60 + startM.toInt())
        var nextday = endH.toInt() * 60 + endM.toInt()
        sleepTime = today + nextday
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
        useValidTime = validTime
        Log.d("SUM_VALID", validTime.toString())
        var hour = validTime / 60
        var min = validTime % 60

        if (hour < 10) validH = "0" + hour
        else validH = hour.toString()

        if (min < 10) validM = "0" + min
        else validM = min.toString()

        binding.dailyScheduleValidTime.text = validH + "시간 " + validM + "분"
    }

    //getDailyFromDB에서 데이터 받아오기 끝난 다음에 실행해야 됨
    private fun print_daily_work(){
        sort_daily()
        put_important_result()
        put_result()

        Log.d("RESULT_DAILY", daily.toString())
        Log.d("RESULT_WORK", result_work.toString())
        Log.d("RESULT_START_H", result_start_H.toString())
        Log.d("RESULT_START_M", result_start_M.toString())
        Log.d("RESULT_END_H", result_end_H.toString())
        Log.d("RESULT_END_M", result_end_M.toString())

//        for (i in 0 until result_work.size) {
//            var original = daily.get(result_work.get(i))
//            var ordered : OrderedSchedule = OrderedSchedule(
//                original.dailyId,
//                original.name,
//                result_start_H.get(i),
//                result_start_M.get(i),
//                result_end_H.get(i),
//                result_end_M.get(i),
//                100)
//            orderedDaily.add(ordered)
//        }
    }

    //daily에 데일리 스케줄 저장되어있음
    //dailyId, day, year, month, date, name, important, timeH, timeM, deadlineMonth, deadlineDate
    private fun sort_daily() {
        for(i in 0 until daily.size) {
            var min_weight = find_weight(daily.get(i))
            var min_index = i

            for (j in i until daily.size) {
                var temp_weight = find_weight(daily.get(j))
                if (min_weight > temp_weight) {
                    min_weight = temp_weight
                    min_index = j
                }
            }

            var temp_work = daily.get(min_index)
            daily.set(min_index, daily.get(i))
            daily.set(i, temp_work)
        }
    }

    private fun put_important_result() {
        var i = 0

        while (i < daily.size && !isSleep()) {
            var work = time_to_minute(daily.get(i))
            var work_H = work / 60
            var work_M = work % 60
            var now_fixed_num = now_fixed
            if (now_fixed_num < fixed.size) {
                if (result_start_H.size > result_end_H.size) {
                    result_end_H.add(now_time_H.toString())
                    result_end_M.add(now_time_M.toString())
                }

                if (daily.get(i).important == true) {
                    result_start_H.add(now_time_H.toString())
                    result_start_M.add(now_time_M.toString())
                    result_work.add(i)

                    doDaily(work_H, work_M)

                    if (isConflict()) {
                        for (k in now_fixed_num until fixed_num) {
                            if (fixed.get(k).startH.toInt() < now_time_H || (fixed.get(k).startH.toInt() == now_time_H && fixed.get(k).startM.toInt() < now_time_M)) {
                                if (isSleep()) {
                                    result_end_H.add(startH)
                                    result_end_M.add(startM)
                                    break
                                }

                                result_end_H.add(fixed.get(k).startH)
                                result_end_M.add(fixed.get(k).startM)

                                result_start_H.add(fixed.get(k).endH)
                                result_start_M.add(fixed.get(k).endM)
                                result_work.add(i)

                                updateNowTime(k)
                                now_fixed += 1
                            }
                            else {
                                if (isSleep()) {
                                    result_end_H.add(startH)
                                    result_end_M.add(startM)
                                    break
                                }

                                result_end_H.add(now_time_H.toString())
                                result_end_M.add(now_time_M.toString())
                                break
                            }
                        }
                    }
                    else {
                        result_end_H.add(now_time_H.toString())
                        result_end_M.add(now_time_M.toString())
                    }
                }
                i += 1
            }
        }

        if (result_start_H.size > result_end_H.size) {
            result_end_H.add(now_time_H.toString())
            result_end_M.add(now_time_M.toString())
        }
    }

    private fun put_result() {
        var i = 0

        while (i < daily.size && !isSleep()) {
            var work = time_to_minute(daily.get(i))
            var work_H = work / 60
            var work_M = work % 60
            var now_fixed_num = now_fixed
            if (now_fixed_num < fixed.size) {
                if (result_start_H.size > result_end_H.size) {
                    result_end_H.add(now_time_H.toString())
                    result_end_M.add(now_time_M.toString())
                }

                if (daily.get(i).important == false) {
                    result_start_H.add(now_time_H.toString())
                    result_start_M.add(now_time_M.toString())
                    result_work.add(i)

                    doDaily(work_H, work_M)

                    if (isConflict()) {
                        for (k in now_fixed_num until fixed_num) {
                            if (fixed.get(k).startH.toInt() < now_time_H || (fixed.get(k).startH.toInt() == now_time_H && fixed.get(k).startM.toInt() < now_time_M)) {
                                if (isSleep()) {
                                    result_end_H.add(startH)
                                    result_end_M.add(startM)
                                    break
                                }

                                result_end_H.add(fixed.get(k).startH)
                                result_end_M.add(fixed.get(k).startM)

                                result_start_H.add(fixed.get(k).endH)
                                result_start_M.add(fixed.get(k).endM)
                                result_work.add(i)

                                updateNowTime(k)
                                now_fixed += 1
                            }
                            else {
                                if (isSleep()) {
                                    result_end_H.add(startH)
                                    result_end_M.add(startM)
                                    break
                                }

                                result_end_H.add(now_time_H.toString())
                                result_end_M.add(now_time_M.toString())
                                break
                            }
                        }
                    }
                    else {
                        result_end_H.add(now_time_H.toString())
                        result_end_M.add(now_time_M.toString())
                    }
                }
                i += 1
            }
        }

        if (result_start_H.size > result_end_H.size) {
            result_end_H.add(now_time_H.toString())
            result_end_M.add(now_time_M.toString())
        }
    }



    //일정 진행시간이 수면시간을 넘어가는지 판단, 넘어가면 true
    private fun isSleep() : Boolean {
        if (now_time_H > startH.toInt() || (now_time_H == startH.toInt() && now_time_M > startM.toInt())) {
            return true
        }
        return false
    }

    private fun doDaily(hour : Int, minute : Int) {
        if (now_time_M + minute >= 60) {
            now_time_H = now_time_H + hour + 1
            now_time_M = now_time_M + minute - 60
        } else {
            now_time_H = now_time_H + hour
            now_time_M = now_time_M + minute
        }
    }

    private fun isConflict() : Boolean {
        if (now_time_H > fixed.get(now_fixed).startH.toInt() || (now_time_H == fixed.get(now_fixed).startH.toInt() && now_time_M > fixed.get(now_fixed).startM.toInt())) {
            return true
        }
        return false
    }

    private fun updateNowTime(i : Int) {
        if (now_time_M - fixed.get(i).startM.toInt() + fixed.get(i).endM.toInt() >= 60) {
            now_time_H = now_time_H - fixed.get(i).startH.toInt() + fixed.get(i).endH.toInt() + 1
            now_time_M = now_time_M - fixed.get(i).startM.toInt() + fixed.get(i).endM.toInt() - 60
        } else {
            now_time_H = now_time_H - fixed.get(i).startH.toInt() + fixed.get(i).endH.toInt()
            now_time_M = now_time_M - fixed.get(i).startM.toInt() + fixed.get(i).endM.toInt()
        }
    }

    //오늘날짜 : date.month 이런 식 (date에 있음)
    //일정의 데드라인 달 : deadlineMonth
    private fun find_weight(dailySchedule: DailySchedule) : Int {
        var sample_weight = 0
        sample_weight += if ((dailySchedule.deadlineMonth.toInt() - date.monthOfYear) >= 0 ) (dailySchedule.deadlineMonth.toInt() - date.monthOfYear) else 12 - (dailySchedule.deadlineMonth.toInt() - date.monthOfYear)
        sample_weight += if ((dailySchedule.deadlineDate.toInt() - date.dayOfMonth) >= 0) (dailySchedule.deadlineDate.toInt() - date.dayOfMonth) else 31 - (dailySchedule.deadlineDate.toInt() - date.dayOfMonth)
        sample_weight *= 1000
        sample_weight += dailySchedule.timeH.toInt() * 60 + dailySchedule.timeM.toInt()
        return sample_weight
    }

    private fun time_to_minute(i : DailySchedule) : Int {
        return i.timeH.toInt() * 60 + i.timeM.toInt()
    }
}