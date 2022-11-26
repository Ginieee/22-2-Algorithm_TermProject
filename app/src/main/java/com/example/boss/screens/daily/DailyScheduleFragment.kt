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

    var sleepStartH = ""
    var sleepStartM = ""
    var sleepEndH = ""
    var sleepEndM = ""

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
    private var useDaily : ArrayList<DailySchedule> = ArrayList<DailySchedule>()
    private var orderedDaily : ArrayList<OrderedSchedule> = ArrayList<OrderedSchedule>()

    private var now_fixed = 0
    private var fixed_num = 0
    private var sample_num = 0

    private var now_time_H = 0
    private var now_time_M = 0

    var sample_work = arrayListOf<Int>()

    var result_start_H = arrayListOf<String>()
    var result_start_M = arrayListOf<String>()
    var result_end_H = arrayListOf<String>()
    var result_end_M = arrayListOf<String>()
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
        initValue()
        setAdapter()
    }

    private fun initValue() {
        fixed.clear()
        fixed_num = 0
        daily.clear()
        sample_num = 0
        useDaily.clear()
        orderedDaily.clear()
        sample_work.clear()
        result_work.clear()
        result_start_H.clear()
        result_start_M.clear()
        result_end_H.clear()
        result_end_M.clear()
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

        now_time_H = sleepEndH.toInt()
        now_time_M = sleepEndM.toInt()

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
        addSleepOnStart()
        fixed_num = fixed.size
        getValidTime()


        var forDailyDB : Thread = Thread {
            daily = db.dailyDao.getDateDaily(arg.year, arg.month, arg.date) as ArrayList
        }
        forDailyDB.start()

        try {
            forDailyDB.join()
        } catch (e : InterruptedException) {
            e.printStackTrace()
        }
        Log.d("GET_DAILY", daily.toString())
        sample_num = daily.size
        useDaily.clear()
        useDaily.addAll(daily)
        putDailyNumToSampleWork()
        orderedDaily.clear()

        print_daily_work()
        //dailyToOrdered()
        dailyTodoRVAdapter.addDaily(orderedDaily)
        dailyTodoRVAdapter.notifyDataSetChanged()

    }

    private fun putDailyNumToSampleWork() {
        for (i in 0 until daily.size) {
            sample_work.add(i)
        }
        Log.d("SAMPLE_WORK_INIT", sample_work.toString())
    }


    private fun getPref(){
        sleepStartH = pref.getString(SLEEPSTARTH,"00")!!
        sleepStartM = pref.getString(SLEEPSTARTM,"00")!!
        sleepEndH = pref.getString(SLEEPENDH,"00")!!
        sleepEndM = pref.getString(SLEEPENDM,"00")!!
    }

    private fun sumSleepTime(){
        var today = 24 * 60 - (sleepStartH.toInt() * 60 + sleepStartM.toInt())
        var nextday = sleepEndH.toInt() * 60 + sleepEndM.toInt()
        sleepTime = today + nextday
        Log.d("SUM_SLEEP", sleepTime.toString())
        //Log.d("SLEEPCHECK", sleepTime.toString() + "총 이거고 시간은 " + startH.toInt() + "시" + startM.toInt() + "분")
    }

    private fun sumFixedTime() {
        fixedTime = 0
        var time : Int = 0
        for (i in 0 until fixed.size - 1) {
            Log.d("SUM_EACH_FIXED_I", i.toString())
            time = (fixed.get(i).endH.toInt() * 60 + fixed.get(i).endM.toInt()) - (fixed.get(i).startH.toInt() * 60 + fixed.get(i).startM.toInt())
            fixedTime = fixedTime + time
            Log.d("SUM_EACH_FIXED", time.toString())
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

    private fun addSleepOnStart() {
        fixed.add(FixedSchedule(100, day,"Sleep", sleepStartH, sleepStartM, sleepEndH, sleepEndM))
    }

    private fun find_weight(i : Int) : Int {
        var sample_weight = 0
        sample_weight += if ((daily.get(i).deadlineMonth.toInt() - date.monthOfYear) >= 0) (daily.get(i).deadlineMonth.toInt() - date.monthOfYear) else 12 - (daily.get(i).deadlineMonth.toInt() - date.monthOfYear)
        sample_weight += if ((daily.get(i).deadlineDate.toInt() - date.dayOfMonth) >= 0) (daily.get(i).deadlineDate.toInt() - date.dayOfMonth) else 31 - (daily.get(i).deadlineDate.toInt() - date.dayOfMonth)
        sample_weight *= 1000
        sample_weight += daily.get(i).timeH.toInt() * 60 + daily.get(i).timeM.toInt()
        return sample_weight
    }

    private fun sort_daily() {
        for (i in 0 until sample_num) {
            var min_weight = find_weight(i)
            var min_index = i

            for (j in i until sample_num) {
                var temp_weight = find_weight(j)
                if (min_weight > temp_weight) {
                    min_weight = temp_weight
                    min_index = j
                }
            }

            var temp_work = daily.get(min_index)
            daily.set(min_index, daily.get(i))
            daily.set(i, temp_work)

            var temp_sample = sample_work.get(min_index)
            sample_work.set(min_index, sample_work.get(i))
            sample_work.set(i, temp_sample)
        }
    }

    private fun doDaily(hour : Int, minute : Int) {
        if (now_time_M + minute >= 60) {
            now_time_H = now_time_H + hour + 1
            now_time_M = now_time_M + minute - 60
        }
        else {
            now_time_H = now_time_H + hour
            now_time_M = now_time_M + minute
        }
    }

    private fun isConflict() : Boolean {
        Log.d("ISCONFLICT", fixed.get(now_fixed).toString())
        Log.d("ISCONFLICT_NOW", now_time_H.toString() + "시 " + now_time_M.toString() + "분" )
        if (now_time_H > fixed.get(now_fixed).startH.toInt() || (now_time_H == fixed.get(now_fixed).startH.toInt() && now_time_M > fixed.get(now_fixed).startM.toInt())) {
            return true
        }
        return false
    }

    private fun updateNowTime(i : Int) {
        if (now_time_M - fixed.get(i).startM.toInt() + fixed.get(i).endM.toInt() >= 60) {
            now_time_H = now_time_H - fixed.get(i).startH.toInt() + fixed.get(i).endH.toInt() + 1
            now_time_M = now_time_M - fixed.get(i).startM.toInt() + fixed.get(i).endM.toInt() - 60
        }
        else {
            now_time_H = now_time_H - fixed.get(i).startH.toInt() + fixed.get(i).endH.toInt()
            now_time_M = now_time_M - fixed.get(i).startM.toInt() + fixed.get(i).endM.toInt()
        }
    }

    private fun isSleep() : Boolean {
        if (now_time_H > sleepStartH.toInt() || (now_time_H == sleepStartH.toInt() && now_time_M > sleepStartM.toInt())) {
            return true
        }
        return false
    }

    private fun put_important_result() {
        var i = 0
        var delete = false

        Log.d("PUT_IMPO_START_SN", sample_num.toString())
        while (i < sample_num && !isSleep()) {
            delete = false
            var work_H = daily.get(i).timeH.toInt()
            var work_M = daily.get(i).timeM.toInt()
            var now_fixed_num = now_fixed
            Log.d("PUT_IMFO_START_NFN", now_fixed_num.toString())

            if (result_start_H.size > result_end_H.size) {
                result_end_H.add(now_time_H.toString())
                result_end_M.add(now_time_M.toString())
                Log.d("PUT_IMPO_F_SH", result_start_H.toString())
                Log.d("PUT_IMPO_F_SM", result_start_M.toString())
                Log.d("PUT_IMPO_F_ENDH", result_end_H.toString())
                Log.d("PUT_IMPO_F_ENDM", result_end_M.toString())
                Log.d("PUT_IMPO_F_WORK", result_work.toString())
            }

            if (daily.get(i).important == true) {
                result_start_H.add(now_time_H.toString())
                result_start_M.add(now_time_M.toString())
                result_work.add(sample_work.get(i))
                Log.d("PUT_IMPO_S_SH", result_start_H.toString())
                Log.d("PUT_IMPO_S_SM", result_start_M.toString())
                Log.d("PUT_IMPO_S_ENDH", result_end_H.toString())
                Log.d("PUT_IMPO_S_ENDM", result_end_M.toString())
                Log.d("PUT_IMPO_S_WORK", result_work.toString())

                doDaily(work_H, work_M)

                if (isConflict()) {
                    for (k in now_fixed_num until fixed_num) {
                        if (fixed.get(k).startH.toInt() < now_time_H || (fixed.get(k).startH.toInt() == now_time_H && fixed.get(k).startM.toInt() < now_time_M)) {
                            if (isSleep()) {
                                result_end_H.add(sleepStartH.toString())
                                result_end_M.add(sleepStartM.toString())
                                Log.d("PUT_IMPO_T_SH", result_start_H.toString())
                                Log.d("PUT_IMPO_T_SM", result_start_M.toString())
                                Log.d("PUT_IMPO_T_ENDH", result_end_H.toString())
                                Log.d("PUT_IMPO_T_ENDM", result_end_M.toString())
                                Log.d("PUT_IMPO_T_WORK", result_work.toString())
                                break
                            }

                            result_end_H.add(fixed.get(k).startH)
                            result_end_M.add(fixed.get(k).startM)
                            Log.d("PUT_IMPO_Fo_SH", result_start_H.toString())
                            Log.d("PUT_IMPO_Fo_SM", result_start_M.toString())
                            Log.d("PUT_IMPO_Fo_ENDH", result_end_H.toString())
                            Log.d("PUT_IMPO_Fo_ENDM", result_end_M.toString())
                            Log.d("PUT_IMPO_Fo_WORK", result_work.toString())

                            result_start_H.add(fixed.get(k).endH)
                            result_start_M.add(fixed.get(k).endM)
                            result_work.add(sample_work.get(i))
                            Log.d("PUT_IMPO_Fi_SH", result_start_H.toString())
                            Log.d("PUT_IMPO_Fi_SM", result_start_M.toString())
                            Log.d("PUT_IMPO_Fi_ENDH", result_end_H.toString())
                            Log.d("PUT_IMPO_Fi_ENDM", result_end_M.toString())
                            Log.d("PUT_IMPO_Fi_WORK", result_work.toString())

                            updateNowTime(k)
                            now_fixed += 1

//                            if (now_fixed == fixed.size - 1) {
//                                result_end_H.add(now_time_H.toString())
//                                result_end_M.add(now_time_M.toString())
//                                Log.d("PUT_IMPO_FiT_SH", result_start_H.toString())
//                                Log.d("PUT_IMPO_FiT_SM", result_start_M.toString())
//                                Log.d("PUT_IMPO_FiT_ENDH", result_end_H.toString())
//                                Log.d("PUT_IMPO_FiT_ENDM", result_end_M.toString())
//                                Log.d("PUT_IMPO_FiT_WORK", result_work.toString())
//                                break
//                            }
                        }
                        else {
                            if (isSleep()) {
                                result_end_H.add(sleepStartH.toString())
                                result_end_M.add(sleepStartM.toString())
                                Log.d("PUT_IMPO_Si_SH", result_start_H.toString())
                                Log.d("PUT_IMPO_Si_SM", result_start_M.toString())
                                Log.d("PUT_IMPO_Si_ENDH", result_end_H.toString())
                                Log.d("PUT_IMPO_Si_ENDM", result_end_M.toString())
                                Log.d("PUT_IMPO_Si_WORK", result_work.toString())
                                break
                            }

                            result_end_H.add(now_time_H.toString())
                            result_end_M.add(now_time_M.toString())
                            Log.d("PUT_IMPO_Se_SH", result_start_H.toString())
                            Log.d("PUT_IMPO_Se_SM", result_start_M.toString())
                            Log.d("PUT_IMPO_Se_ENDH", result_end_H.toString())
                            Log.d("PUT_IMPO_Se_ENDM", result_end_M.toString())
                            Log.d("PUT_IMPO_Se_WORK", result_work.toString())
                            break
                        }
                    }
                }
                else {
                    result_end_H.add(now_time_H.toString())
                    result_end_M.add(now_time_M.toString())
                    Log.d("PUT_IMPO_Ei_SH", result_start_H.toString())
                    Log.d("PUT_IMPO_Ei_SM", result_start_M.toString())
                    Log.d("PUT_IMPO_Ei_ENDH", result_end_H.toString())
                    Log.d("PUT_IMPO_Ei_ENDM", result_end_M.toString())
                    Log.d("PUT_IMPO_Ei_WORK", result_work.toString())
                }

                daily.removeAt(i)
                delete = true
                sample_work.removeAt(i)
                sample_num -= 1
            }
            if (!delete) i += 1
        }


    }

    private fun put_result() {
        Log.d("PUT_RESULT_START_SN", sample_num.toString())
        for (i in 0 until sample_num) {
            var work_H = daily.get(i).timeH.toInt()
            var work_M = daily.get(i).timeM.toInt()
            var now_fixed_num = now_fixed
            Log.d("PUT_RESULT_START_NFN", now_fixed_num.toString())

            if (result_start_H.size > result_end_H.size) {
                result_end_H.add(now_time_H.toString())
                result_end_M.add(now_time_M.toString())
                Log.d("PUT_RESULT_F_SH", result_start_H.toString())
                Log.d("PUT_RESULT_F_SM", result_start_M.toString())
                Log.d("PUT_RESULT_F_ENDH", result_end_H.toString())
                Log.d("PUT_RESULT_F_ENDM", result_end_M.toString())
                Log.d("PUT_RESULT_F_WORK", result_work.toString())
            }

            result_start_H.add(now_time_H.toString())
            result_start_M.add(now_time_M.toString())
            result_work.add(sample_work.get(i))
            Log.d("PUT_RESULT_S_SH", result_start_H.toString())
            Log.d("PUT_RESULT_S_SM", result_start_M.toString())
            Log.d("PUT_RESULT_S_ENDH", result_end_H.toString())
            Log.d("PUT_RESULT_S_ENDM", result_end_M.toString())
            Log.d("PUT_RESULT_S_WORK", result_work.toString())

            doDaily(work_H, work_M)

            if (isConflict()) {
                for (k in now_fixed_num until fixed_num) {
                    if (fixed.get(k).startH.toInt() < now_time_H || (fixed.get(k).startH.toInt() == now_time_H && fixed.get(k).startM.toInt() < now_time_M)) {
                        if (isSleep()) {
                            result_end_H.add(sleepStartH.toString())
                            result_end_M.add(sleepStartM.toString())
                            Log.d("PUT_RESULT_T_SH", result_start_H.toString())
                            Log.d("PUT_RESULT_T_SM", result_start_M.toString())
                            Log.d("PUT_RESULT_T_ENDH", result_end_H.toString())
                            Log.d("PUT_RESULT_T_ENDM", result_end_M.toString())
                            Log.d("PUT_RESULT_T_WORK", result_work.toString())
                            break
                        }

                        result_end_H.add(fixed.get(k).startH)
                        result_end_M.add(fixed.get(k).startM)
                        Log.d("PUT_RESULT_Fo_SH", result_start_H.toString())
                        Log.d("PUT_RESULT_Fo_SM", result_start_M.toString())
                        Log.d("PUT_RESULT_Fo_ENDH", result_end_H.toString())
                        Log.d("PUT_RESULT_Fo_ENDM", result_end_M.toString())
                        Log.d("PUT_RESULT_Fo_WORK", result_work.toString())

                        result_start_H.add(fixed.get(k).endH)
                        result_start_M.add(fixed.get(k).endM)
                        result_work.add(sample_work.get(i))
                        Log.d("PUT_RESULT_Fi_SH", result_start_H.toString())
                        Log.d("PUT_RESULT_Fi_SM", result_start_M.toString())
                        Log.d("PUT_RESULT_Fi_ENDH", result_end_H.toString())
                        Log.d("PUT_RESULT_Fi_ENDM", result_end_M.toString())
                        Log.d("PUT_RESULT_Fi_WORK", result_work.toString())

                        updateNowTime(k)
                        now_fixed += 1
                        Log.d("PUT_RESULT_NOW_FIXED", now_fixed.toString())
                        Log.d("PUT_RESULT_FIXED_SIZE", fixed.size.toString())

//                        if (now_fixed == fixed.size - 1) {
//                            result_end_H.add(now_time_H.toString())
//                            result_end_M.add(now_time_M.toString())
//                            Log.d("PUT_RESULT_FiT_SH", result_start_H.toString())
//                            Log.d("PUT_RESULT_FiT_SM", result_start_M.toString())
//                            Log.d("PUT_RESULT_FiT_ENDH", result_end_H.toString())
//                            Log.d("PUT_RESULT_FiT_ENDM", result_end_M.toString())
//                            Log.d("PUT_RESULT_FiT_WORK", result_work.toString())
//                            break
//                        }
                    }
                    else {
                        if (isSleep()) {
                            result_end_H.add(sleepStartH.toString())
                            result_end_M.add(sleepStartM.toString())
                            Log.d("PUT_RESULT_Si_SH", result_start_H.toString())
                            Log.d("PUT_RESULT_Si_SM", result_start_M.toString())
                            Log.d("PUT_RESULT_Si_ENDH", result_end_H.toString())
                            Log.d("PUT_RESULT_Si_ENDM", result_end_M.toString())
                            Log.d("PUT_RESULT_Si_WORK", result_work.toString())
                            break
                        }

                        result_end_H.add(now_time_H.toString())
                        result_end_M.add(now_time_M.toString())
                        Log.d("PUT_RESULT_Se_SH", result_start_H.toString())
                        Log.d("PUT_RESULT_Se_SM", result_start_M.toString())
                        Log.d("PUT_RESULT_Se_ENDH", result_end_H.toString())
                        Log.d("PUT_RESULT_Se_ENDM", result_end_M.toString())
                        Log.d("PUT_RESULT_Se_WORK", result_work.toString())
                        break
                    }
                }
            }
            else {
                result_end_H.add(now_time_H.toString())
                result_end_M.add(now_time_M.toString())
                Log.d("PUT_RESULT_Ei_SH", result_start_H.toString())
                Log.d("PUT_RESULT_Ei_SM", result_start_M.toString())
                Log.d("PUT_RESULT_Ei_ENDH", result_end_H.toString())
                Log.d("PUT_RESULT_Ei_ENDM", result_end_M.toString())
                Log.d("PUT_RESULT_Ei_WORK", result_work.toString())
            }
        }
    }

    private fun print_daily_work() {
        sort_daily()
        Log.d("SORT_DAILY_DAILY", daily.toString())
        Log.d("SORT_DAILY_WORK", sample_work.toString())
        Log.d("SORT_DAILY_RESULT", result_work.toString())
        put_important_result()
        Log.d("PUT_IMPORTANT_RESULT", result_work.toString())
        put_result()
        Log.d("PUT_RESULT", result_work.toString())
        Log.d("PUT_RESULT_USE_DAILY", useDaily.toString())

        orderedDaily.clear()
        for (i in 0 until result_work.size) {
            if (!(result_start_H.get(i).equals(result_end_H.get(i)) && result_start_M.get(i).equals(result_end_M.get(i)))) {
                var origin = useDaily.get(result_work.get(i))
                var ordered = OrderedSchedule(origin.dailyId, origin.name, result_start_H.get(i), result_start_M.get(i), result_end_H.get(i), result_end_M.get(i), 100)

                var leftTime = calculateLeftTime(ordered)
                Log.d("ORIGINLEFT", origin.left.toString())
                Log.d("CAL_LEFT", leftTime.toString())
                leftTime = origin.left - leftTime
                origin.left = leftTime
                useDaily.set(result_work.get(i), origin)

                ordered.leftMinute = leftTime
                orderedDaily.add(ordered)
                Log.d("EACH_ORDERED", ordered.toString())
            }
        }

        Log.d("All_ORDERED", orderedDaily.toString())
    }

    private fun calculateLeftTime(ordered : OrderedSchedule) : Int {
        var hour : Int = 0
        var minute : Int = 0
        if (ordered.endM > ordered.startM) {
            hour = (ordered.endH.toInt() - ordered.startH.toInt()) * 60
            minute = ordered.endM.toInt() - ordered.startM.toInt()
        } else {
            hour = (ordered.endH.toInt() - 1 - ordered.startH.toInt()) * 60
            minute = ordered.endM.toInt() + 60 - ordered.startM.toInt()
        }
        return hour + minute
    }

    private fun dailyToOrdered(){
        for(i in daily) {
            var ordered = OrderedSchedule(i.dailyId, i.name, "00","00","00","00",100)
            orderedDaily.add(ordered)
        }
    }

    private fun time_to_minute(schedule : DailySchedule) : Int {
        Log.d("TTM", (schedule.timeH.toInt() * 60 + schedule.timeM.toInt()).toString())
        return schedule.timeH.toInt() * 60 + schedule.timeM.toInt()
    }
}