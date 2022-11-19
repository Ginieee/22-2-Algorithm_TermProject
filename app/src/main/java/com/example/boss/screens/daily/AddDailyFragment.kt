package com.example.boss.screens.daily

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.boss.MainActivity
import com.example.boss.R
import com.example.boss.data.ScheduleDatabase
import com.example.boss.data.entity.DailySchedule
import com.example.boss.databinding.FragmentAddDailyBinding

class AddDailyFragment : Fragment() {

    lateinit var mContext : Context
    lateinit var mActivity : MainActivity

    var daily : DailySchedule = DailySchedule()
    private var isImportant : Boolean = false


    private lateinit var binding : FragmentAddDailyBinding
    lateinit var db : ScheduleDatabase

    private val arg : AddDailyFragmentArgs by navArgs()
    private var year : Int = 0
    private var month : Int = 0
    private var date : Int = 0
    private var day : Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity){
            mContext = context
            mActivity = activity as MainActivity
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_daily, container, false)
        getArgs()
        db = ScheduleDatabase.getInstance(requireContext())!!

        var title : String = month.toString() + "월 " + date + "일 일정 추가"
        binding.dailyAddTitleTv.text = title

        clickHandler()

        return binding.root
    }

    private fun getArgs() {
        year = arg.year
        month = arg.month
        date = arg.date
        day = arg.day
    }

    private fun clickHandler() {
        binding.dailyAddSaveBtn.setOnClickListener {
            getData()
            var print : String = ""
            Thread {
                db.dailyDao.insertDaily(daily)
                print = db.dailyDao.getDateDaily(year, month, date).toString()
            }.start()
            Log.d("ADDDAILY", print)

            val action = AddDailyFragmentDirections.actionAddDailyFragmentToDailyScheduleFragment(year, month, date, day)
            findNavController().navigate(action)
        }

        binding.dailyAddBackBtn.setOnClickListener {
            val action = AddDailyFragmentDirections.actionAddDailyFragmentToDailyScheduleFragment(year, month, date, day)
            findNavController().navigate(action)
        }

        binding.dailyAddImportantRadioBtn.setOnClickListener {
            onRadioButtonClicked(binding.dailyAddImportantRadioBtn)
        }
        binding.dailyAddNormalRadioBtn.setOnClickListener {
            onRadioButtonClicked(binding.dailyAddNormalRadioBtn)
        }
    }

    private fun getData() {
        daily.day = day
        daily.year = year
        daily.month = month
        daily.date = date
        daily.name = binding.dailyAddNameEt.text.toString()
        daily.important = isImportant
        daily.timeH = binding.dailyAddTimeHEdit.text.toString()
        daily.timeM = binding.dailyAddTimeMEdit.text.toString()
        daily.deadlineMonth = binding.dailyAddDeadlineMonthEdit.text.toString()
        daily.deadlineDate = binding.dailyAddDeadlineDateEdit.text.toString()
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when(view.id) {
                R.id.daily_add_important_radio_btn -> {
                    if (checked) {
                        isImportant = true
                        Log.d("RADIO","중요")
                    }
                }

                R.id.daily_add_normal_radio_btn -> {
                    if (checked) {
                        isImportant = false
                        Log.d("RADIO","일반")
                    }
                }
            }
        }
    }
}