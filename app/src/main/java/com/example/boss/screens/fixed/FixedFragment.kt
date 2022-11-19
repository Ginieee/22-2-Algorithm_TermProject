package com.example.boss.screens.fixed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boss.MainActivity
import com.example.boss.R
import com.example.boss.data.FixedSchedule
import com.example.boss.data.ScheduleDatabase
import com.example.boss.databinding.FragmentFixedBinding
import com.example.boss.screens.fixed.Adapter.*

class FixedFragment : Fragment() {

    lateinit var mContext : Context
    lateinit var mActivity : MainActivity

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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity){
            mContext = context
            mActivity = activity as MainActivity
        }
    }

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
                val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("월요일 일정",1, true, id)
                findNavController().navigate(action)
            }
        })

        tueFixedRVAdapter.setMyItemClickListener(object : TueFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("화요일 일정",2, true, id)
                findNavController().navigate(action)
            }
        })
        wedFixedRVAdapter.setMyItemClickListener(object : WedFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("수요일 일정",3, true, id)
                findNavController().navigate(action)
            }
        })

        thuFixedRVAdapter.setMyItemClickListener(object : ThuFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("목요일 일정",4, true, id)
                findNavController().navigate(action)
            }
        })
        friFixedRVAdapter.setMyItemClickListener(object : FriFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("금요일 일정",5, true, id)
                findNavController().navigate(action)
            }
        })

        satFixedRVAdapter.setMyItemClickListener(object : SatFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("토요일 일정",6, true, id)
                findNavController().navigate(action)
            }
        })

        sunFixedRVAdapter.setMyItemClickListener(object : SunFixedRVAdapter.MyItemClickListener{
            override fun onSendId(id: Int) {
                val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("일요일 일정",7, true, id)
                findNavController().navigate(action)
            }
        })
    }

    private fun clickHandler(){

        binding.mainCalendarBtn.setOnClickListener {
            findNavController().navigate(R.id.action_fixedFragment_to_calendarFragment)
        }

        binding.fixedSleepEditBtn.setOnClickListener {
            editSleep(true)
        }

        binding.fixedSleepSaveBtn.setOnClickListener {
            editSleep(false)
        }

        binding.fixedMonAdd.setOnClickListener {
            val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("월요일 일정 추가",1, false, -1)
            findNavController().navigate(action)
        }

        binding.fixedTueAdd.setOnClickListener {
            val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("화요일 일정 추가",2, false, -1)
            findNavController().navigate(action)
        }

        binding.fixedWedAdd.setOnClickListener {
            val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("수요일 일정 추가",3, false, -1)
            findNavController().navigate(action)
        }

        binding.fixedThuAdd.setOnClickListener {
            val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("목요일 일정 추가",4, false, -1)
            findNavController().navigate(action)
        }

        binding.fixedFriAdd.setOnClickListener {
            val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("금요일 일정 추가",5, false, -1)
            findNavController().navigate(action)
        }

        binding.fixedSatAdd.setOnClickListener {
            val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("토요일 일정 추가",6, false, -1)
            findNavController().navigate(action)
        }

        binding.fixedSunAdd.setOnClickListener {
            val action = FixedFragmentDirections.actionFixedFragmentToAddFixedFragment("일요일 일정 추가",7, false, -1)
            findNavController().navigate(action)
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
            requireActivity().runOnUiThread {
                monFixedRVAdapter.notifyDataSetChanged()
                tueFixedRVAdapter.notifyDataSetChanged()
                wedFixedRVAdapter.notifyDataSetChanged()
                thuFixedRVAdapter.notifyDataSetChanged()
                friFixedRVAdapter.notifyDataSetChanged()
                satFixedRVAdapter.notifyDataSetChanged()
                sunFixedRVAdapter.notifyDataSetChanged()
            }
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