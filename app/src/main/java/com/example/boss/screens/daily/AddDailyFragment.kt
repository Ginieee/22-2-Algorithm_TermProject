package com.example.boss.screens.daily

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
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

    private var isDataAccept = false

    var deleteDaily : DailySchedule = DailySchedule()

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

        var title : String = if (!arg.isDelete) month.toString() + "월 " + date + "일 일정 추가" else month.toString() + "월 " + date + "일 일정 삭제"
        binding.dailyAddTitleTv.text = title

        if (arg.isDelete) {
            binding.dailyAddSaveBtn.visibility = View.GONE
            binding.dailyAddDeleteBtn.visibility = View.VISIBLE
            setDelete()
        } else {
            binding.dailyAddSaveBtn.visibility = View.VISIBLE
            binding.dailyAddDeleteBtn.visibility = View.GONE
        }

        binding.dailyAddNormalRadioBtn.isChecked = true

        clickHandler()

        return binding.root
    }

    private fun setDelete(){
        Thread{
            deleteDaily = db.dailyDao.getDaily(arg.scheduleId)
            Log.d("ADDDAILYCHECK",deleteDaily.toString())
            Log.d("ADDDAILYCHECK","UI실행")
            mActivity.runOnUiThread {
                binding.dailyAddNameEt.setText(deleteDaily.name)
                binding.dailyAddTimeHEdit.setText(deleteDaily.timeH)
                binding.dailyAddTimeMEdit.setText(deleteDaily.timeM)
                binding.dailyAddDeadlineMonthEdit.setText(deleteDaily.deadlineMonth)
                binding.dailyAddDeadlineDateEdit.setText(deleteDaily.deadlineDate)
                if (deleteDaily.important) {
                    binding.dailyAddImportantRadioBtn.isChecked = true
                    binding.dailyAddNormalRadioBtn.isChecked = false
                } else {
                    binding.dailyAddImportantRadioBtn.isChecked = false
                    binding.dailyAddNormalRadioBtn.isChecked = true
                }
            }
        }.start()
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
            if (isDataAccept) {
                var print : String = ""
                Thread {
                    db.dailyDao.insertDaily(daily)
                    print = db.dailyDao.getDateDaily(year, month, date).toString()
                }.start()
                Log.d("ADDDAILY", print)

                val action = AddDailyFragmentDirections.actionAddDailyFragmentToDailyScheduleFragment(year, month, date, day)
                findNavController().navigate(action)
            }
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

        binding.dailyAddDeleteBtn.setOnClickListener {
            Thread {
                db.dailyDao.deleteDaily(deleteDaily)
            }.start()
            val action = AddDailyFragmentDirections.actionAddDailyFragmentToDailyScheduleFragment(year, month, date, day)
            findNavController().navigate(action)
        }
    }

    private fun getData() {
        isDataAccept = true

        if (binding.dailyAddNameEt.text.isNullOrBlank()) {
            Toast.makeText(mContext, "일정 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            isDataAccept = false
            return
        }
        else if ((binding.dailyAddTimeHEdit.text.toString().length != 2) || (binding.dailyAddTimeMEdit.text.toString().length != 2)) {
            Toast.makeText(mContext, "시간은 HH:MM의 두 글자 형태로 입력해주세요.", Toast.LENGTH_SHORT).show()
            isDataAccept = false
            return
        }
        else if (binding.dailyAddTimeHEdit.text.toString().toInt() >= 24 || binding.dailyAddTimeHEdit.text.toString().toInt() < 0) {
            Toast.makeText(mContext, "시간은 00:00 ~ 23:59 사이로 입력해주세요.", Toast.LENGTH_SHORT).show()
            isDataAccept = false
            return
        }
        else if (binding.dailyAddTimeMEdit.text.toString().toInt() >= 60 || binding.dailyAddTimeMEdit.text.toString().toInt() < 0) {
            Toast.makeText(mContext, "시간은 00:00 ~ 23:59 사이로 입력해주세요.", Toast.LENGTH_SHORT).show()
            isDataAccept = false
            return
        }
        else if ((binding.dailyAddDeadlineMonthEdit.text.toString().length != 2) || (binding.dailyAddDeadlineDateEdit.text.toString().length != 2)) {
            Toast.makeText(mContext, "날짜는 MM월 DD일의 두 글자 형태로 입력해주세요.", Toast.LENGTH_SHORT).show()
            isDataAccept = false
            return
        }
        else if (binding.dailyAddDeadlineMonthEdit.text.toString().toInt() > 12 || binding.dailyAddDeadlineMonthEdit.text.toString().toInt() < 1) {
            Toast.makeText(mContext, "01~12월 사이의 달을 입력해주세요.", Toast.LENGTH_SHORT).show()
            isDataAccept = false
            return
        }
        else if (binding.dailyAddDeadlineDateEdit.text.toString().toInt() > 31 || binding.dailyAddDeadlineDateEdit.text.toString().toInt() < 1) {
            Toast.makeText(mContext, "해당 달에 존재하는 날짜를 입력해주세요.", Toast.LENGTH_SHORT).show()
            isDataAccept = false
            return
        }


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
        daily.left = time_to_minute(daily)
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

    private fun time_to_minute(schedule : DailySchedule) : Int {
        Log.d("TTM", (schedule.timeH.toInt() * 60 + schedule.timeM.toInt()).toString())
        return schedule.timeH.toInt() * 60 + schedule.timeM.toInt()
    }
}