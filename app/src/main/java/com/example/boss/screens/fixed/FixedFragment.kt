package com.example.boss.screens.fixed

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boss.MainActivity
import com.example.boss.R
import com.example.boss.data.FixedSchedule
import com.example.boss.data.ScheduleDatabase
import com.example.boss.databinding.FragmentFixedBinding
import com.example.boss.screens.fixed.Adapter.*

class FixedFragment : Fragment() {

    var startH = ""
    var startM = ""
    var endH = ""
    var endM = ""

    private val ADDFIXED = "ADDFIXED"
    private val DAYNUM = "DAYNUM"
    private val ISDELETE = "ISDELETE"
    private val SCHEDULEID = "SCHEDULEID"

    private lateinit var binding : FragmentFixedBinding
    lateinit var db : ScheduleDatabase

    private val monFixedRVAdapter = MonFixedRVAdapter()
    private val tueFixedRVAdapter = TueFixedRVAdapter()
    private val wedFixedRVAdapter = WedFixedRVAdapter()
    private val thuFixedRVAdapter = ThuFixedRVAdapter()
    private val friFixedRVAdapter = FriFixedRVAdapter()
    private val satFixedRVAdapter = SatFixedRVAdapter()
    private val sunFixedRVAdapter = SunFixedRVAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentFixedBinding>(inflater,
            R.layout.fragment_fixed, container, false)

        db = ScheduleDatabase.getInstance(requireContext())!!

        clickHandler()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setAdapter()
        setItemClickListener()
    }

    private fun setAdapter(){
        for (i in 1..7){
            setDayAdapter(i)
        }
        getDayFixedFromDB()
    }

    private fun setItemClickListener(){
        monFixedRVAdapter.setMyItemClickListener(object : MonFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                    putExtra(ADDFIXED, "월요일 일정")
                    putExtra(DAYNUM, 1)
                    putExtra(ISDELETE, true)
                    putExtra(SCHEDULEID, id)
                }
                startActivity(intent)
            }
        })

        tueFixedRVAdapter.setMyItemClickListener(object : TueFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                    putExtra(ADDFIXED, "화요일 일정")
                    putExtra(DAYNUM, 2)
                    putExtra(ISDELETE, true)
                    putExtra(SCHEDULEID, id)
                }
                startActivity(intent)
            }
        })
        wedFixedRVAdapter.setMyItemClickListener(object : WedFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                    putExtra(ADDFIXED, "수요일 일정")
                    putExtra(DAYNUM, 3)
                    putExtra(ISDELETE, true)
                    putExtra(SCHEDULEID, id)
                }
                startActivity(intent)
            }
        })

        thuFixedRVAdapter.setMyItemClickListener(object : ThuFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                    putExtra(ADDFIXED, "목요일 일정")
                    putExtra(DAYNUM, 4)
                    putExtra(ISDELETE, true)
                    putExtra(SCHEDULEID, id)
                }
                startActivity(intent)
            }
        })
        friFixedRVAdapter.setMyItemClickListener(object : FriFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                    putExtra(ADDFIXED, "금요일 일정")
                    putExtra(DAYNUM, 5)
                    putExtra(ISDELETE, true)
                    putExtra(SCHEDULEID, id)
                }
                startActivity(intent)
            }
        })

        satFixedRVAdapter.setMyItemClickListener(object : SatFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                    putExtra(ADDFIXED, "화요일 일정")
                    putExtra(DAYNUM, 6)
                    putExtra(ISDELETE, true)
                    putExtra(SCHEDULEID, id)
                }
                startActivity(intent)
            }
        })

        sunFixedRVAdapter.setMyItemClickListener(object : SunFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                    putExtra(ADDFIXED, "일요일 일정")
                    putExtra(DAYNUM, 7)
                    putExtra(ISDELETE, true)
                    putExtra(SCHEDULEID, id)
                }
                startActivity(intent)
            }
        })
    }

    private fun clickHandler(){
        binding.fixedSleepEditBtn.setOnClickListener {
            editSleep(true)
        }

        binding.fixedSleepSaveBtn.setOnClickListener {
            editSleep(false)
        }

        binding.fixedMonAdd.setOnClickListener {
            val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                putExtra(ADDFIXED, "월요일 일정 추가")
                putExtra(DAYNUM, 1)
            }
            startActivity(intent)
        }

        binding.fixedTueAdd.setOnClickListener {
            val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                putExtra(ADDFIXED, "화요일 일정 추가")
                putExtra(DAYNUM, 2)
            }
            startActivity(intent)
        }

        binding.fixedWedAdd.setOnClickListener {
            val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                putExtra(ADDFIXED, "수요일 일정 추가")
                putExtra(DAYNUM, 3)
            }
            startActivity(intent)
        }

        binding.fixedThuAdd.setOnClickListener {
            val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                putExtra(ADDFIXED, "목요일 일정 추가")
                putExtra(DAYNUM, 4)
            }
            startActivity(intent)
        }

        binding.fixedFriAdd.setOnClickListener {
            val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                putExtra(ADDFIXED, "금요일 일정 추가")
                putExtra(DAYNUM, 5)
            }
            startActivity(intent)
        }

        binding.fixedSatAdd.setOnClickListener {
            val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                putExtra(ADDFIXED, "토요일 일정 추가")
                putExtra(DAYNUM, 6)
            }
            startActivity(intent)
        }

        binding.fixedSunAdd.setOnClickListener {
            val intent = Intent(context as MainActivity, AddFixedActivity::class.java).apply {
                putExtra(ADDFIXED, "일요일 일정 추가")
                putExtra(DAYNUM, 7)
            }
            startActivity(intent)
        }
    }

    private fun getDayFixedFromDB(){
        Thread {
            monFixedRVAdapter.addFixed(db.fixedDao.getDayFixed(1) as ArrayList)
            tueFixedRVAdapter.addFixed(db.fixedDao.getDayFixed(2) as ArrayList)
            wedFixedRVAdapter.addFixed(db.fixedDao.getDayFixed(3) as ArrayList)
            thuFixedRVAdapter.addFixed(db.fixedDao.getDayFixed(4) as ArrayList)
            friFixedRVAdapter.addFixed(db.fixedDao.getDayFixed(5) as ArrayList)
            satFixedRVAdapter.addFixed(db.fixedDao.getDayFixed(6) as ArrayList)
            sunFixedRVAdapter.addFixed(db.fixedDao.getDayFixed(7) as ArrayList)
        }.start()
    }

    private fun setDayAdapter(day : Int){
        when(day){
            1 -> {
                binding.fixedMonRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.fixedMonRv.adapter = monFixedRVAdapter
            }
            2 -> {
                binding.fixedTueRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.fixedTueRv.adapter = tueFixedRVAdapter
            }
            3 -> {
                binding.fixedWedRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.fixedWedRv.adapter = wedFixedRVAdapter
            }
            4 -> {
                binding.fixedThuRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.fixedThuRv.adapter = thuFixedRVAdapter
            }
            5 -> {
                binding.fixedFriRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.fixedFriRv.adapter = friFixedRVAdapter
            }
            6 -> {
                binding.fixedSatRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.fixedSatRv.adapter = satFixedRVAdapter
            }
            7 -> {
                binding.fixedSunRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.fixedSunRv.adapter = sunFixedRVAdapter
            }
            else -> {
                //nothing
            }
        }
    }

    private fun editSleep(isEdit : Boolean){
        if(isEdit){
            startH = binding.fixedSleepStartH.text.toString()
            startM = binding.fixedSleepStartM.text.toString()
            endH = binding.fixedSleepEndH.text.toString()
            endM = binding.fixedSleepEndM.text.toString()

            binding.fixedSleepStartH.visibility = View.GONE
            binding.fixedSleepStartHEdit.setText(startH)
            binding.fixedSleepStartHEdit.visibility = View.VISIBLE

            binding.fixedSleepStartM.visibility = View.GONE
            binding.fixedSleepStartMEdit.setText(startM)
            binding.fixedSleepStartMEdit.visibility = View.VISIBLE

            binding.fixedSleepEndH.visibility = View.GONE
            binding.fixedSleepEndHEdit.setText(endH)
            binding.fixedSleepEndHEdit.visibility = View.VISIBLE

            binding.fixedSleepEndM.visibility = View.GONE
            binding.fixedSleepEndMEdit.setText(endM)
            binding.fixedSleepEndMEdit.visibility = View.VISIBLE

            binding.fixedSleepSaveBtn.visibility = View.VISIBLE
            binding.fixedSleepEditBtn.visibility = View.GONE
        }
        else {
            startH = binding.fixedSleepStartHEdit.text.toString()
            startM = binding.fixedSleepStartMEdit.text.toString()
            endH = binding.fixedSleepEndHEdit.text.toString()
            endM = binding.fixedSleepEndMEdit.text.toString()

            binding.fixedSleepStartHEdit.visibility = View.GONE
            binding.fixedSleepStartH.setText(startH)
            binding.fixedSleepStartH.visibility = View.VISIBLE

            binding.fixedSleepStartMEdit.visibility = View.GONE
            binding.fixedSleepStartM.setText(startM)
            binding.fixedSleepStartM.visibility = View.VISIBLE

            binding.fixedSleepEndHEdit.visibility = View.GONE
            binding.fixedSleepEndH.setText(endH)
            binding.fixedSleepEndH.visibility = View.VISIBLE

            binding.fixedSleepEndMEdit.visibility = View.GONE
            binding.fixedSleepEndM.setText(endM)
            binding.fixedSleepEndM.visibility = View.VISIBLE

            binding.fixedSleepSaveBtn.visibility = View.GONE
            binding.fixedSleepEditBtn.visibility = View.VISIBLE
        }
    }

}