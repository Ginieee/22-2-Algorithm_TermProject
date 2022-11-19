package com.example.boss.screens.fixed

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.boss.MainActivity
import com.example.boss.R
import com.example.boss.data.entity.FixedSchedule
import com.example.boss.data.ScheduleDatabase
import com.example.boss.databinding.FragmentAddFixedBinding

class AddFixedFragment : Fragment() {

    lateinit var mContext : Context
    lateinit var mActivity : MainActivity

    private var dayNum = 0
    private var isDelete = false
    private var scheduleId = 0

    var fixed : FixedSchedule = FixedSchedule()

    private var add : FixedSchedule = FixedSchedule()

    private lateinit var binding : FragmentAddFixedBinding
    lateinit var db : ScheduleDatabase

    private val arg : AddFixedFragmentArgs by navArgs()

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
        binding = DataBindingUtil.inflate<FragmentAddFixedBinding>(inflater, R.layout.fragment_add_fixed, container, false)
        db = ScheduleDatabase.getInstance(requireContext())!!

        val title = arg.title
        dayNum = arg.dayId
        isDelete = arg.isDelete
        scheduleId = arg.scheduleId
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

        return binding.root
    }

    private fun setDelete(){
        Thread{
            fixed = db.fixedDao.getFixed(scheduleId)
            Log.d("ADDFIXEDCHECK",fixed.toString())
            Log.d("ADDFIXEDCHECK","UI실행")
            mActivity.runOnUiThread {
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
            findNavController().navigate(R.id.action_addFixedFragment_to_fixedFragment)
        }

        binding.fixedAddBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_addFixedFragment_to_fixedFragment)
        }

        binding.fixedAddDeleteBtn.setOnClickListener {
            Thread{
                db.fixedDao.deleteFixed(fixed)
            }.start()
            findNavController().navigate(R.id.action_addFixedFragment_to_fixedFragment)
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