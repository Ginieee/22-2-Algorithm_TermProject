package com.example.boss.screens.fixed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.boss.R
import com.example.boss.data.FixedSchedule
import com.example.boss.data.ScheduleDatabase
import com.example.boss.databinding.ActivityAddFixedBinding

class AddFixedActivity : AppCompatActivity() {

    private val ADDFIXED = "ADDFIXED"
    private val DAYNUM = "DAYNUM"
    private val ISDELETE = "ISDELETE"
    private val SCHEDULEID = "SCHEDULEID"

    private var dayNum = 0
    private var isDelete = false
    private var scheduleId = 0

    var fixed : FixedSchedule = FixedSchedule()

    private var add : FixedSchedule = FixedSchedule()

    private lateinit var binding : ActivityAddFixedBinding
    lateinit var db : ScheduleDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_fixed)
        db = ScheduleDatabase.getInstance(this)!!

        val title = intent.getStringExtra(ADDFIXED)
        dayNum = intent.getIntExtra(DAYNUM,1)
        isDelete = intent.getBooleanExtra(ISDELETE, false)
        scheduleId = intent.getIntExtra(SCHEDULEID,0)
        Log.d("ADDFIXEDCHECK", isDelete.toString() + "그리고" + scheduleId.toString())

        if (isDelete){
            binding.fixedAddSaveBtn.visibility = View.GONE
            binding.fixedAddDeleteBtn.visibility = View.VISIBLE
            setDelete()

        } else {
            binding.fixedAddSaveBtn.visibility = View.VISIBLE
            binding.fixedAddDeleteBtn.visibility = View.GONE
        }

        binding.addFixedTitleTv.text = title.toString()

        setClickHandler()

    }

    private fun setDelete(){
        Thread{
            fixed = db.fixedDao.getFixed(scheduleId)
            Log.d("ADDFIXEDCHECK",fixed.toString())
            Log.d("ADDFIXEDCHECK","UI실행")
            runOnUiThread {
                binding.fixedAddNameEt.setText(fixed.name)
                binding.fixedAddStartHEdit.setText(fixed.startH)
                binding.fixedAddStartMEdit.setText(fixed.startM)
                binding.fixedAddEndHEdit.setText(fixed.endH)
                binding.fixedAddEndMEdit.setText(fixed.endM)
            }
        }.start()
    }

    private fun setClickHandler(){

        binding.fixedAddSaveBtn.setOnClickListener {
            getData()
            var print : String = ""
            Thread {
                db.fixedDao.insertFixed(add)
                print = db.fixedDao.getDayFixed(dayNum).toString()
            }.start()
            Log.d("ADDFIXED", print)
            finish()
        }

        binding.fixedAddBackBtn.setOnClickListener {
            finish()
        }

        binding.fixedAddDeleteBtn.setOnClickListener {
            Thread{
                db.fixedDao.deleteFixed(fixed)
            }.start()
            finish()
        }
    }

    private fun getData(){
        add.name = binding.fixedAddNameEt.text.toString()
        add.startH = binding.fixedAddStartHEdit.text.toString()
        add.startM = binding.fixedAddStartMEdit.text.toString()
        add.endH = binding.fixedAddEndHEdit.text.toString()
        add.endM = binding.fixedAddEndMEdit.text.toString()
        add.day = dayNum
    }
}