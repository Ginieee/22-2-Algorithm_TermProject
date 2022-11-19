package com.example.boss.screens.fixed.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.boss.data.entity.FixedSchedule
import com.example.boss.databinding.ItemFixedScheduleBinding

class SatFixedRVAdapter : RecyclerView.Adapter<SatFixedRVAdapter.ViewHolder>() {

    private val fixed = ArrayList<FixedSchedule>()

    interface MyItemClickListener{
        fun onSendId(id:Int)
    }

    private lateinit var mItemClickListener : MyItemClickListener

    fun setMyItemClickListener(itemClickListener : MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemFixedScheduleBinding = ItemFixedScheduleBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fixed[position])

        holder.itemView.setOnClickListener {
            mItemClickListener.onSendId(fixed[position].fixedId)
        }
    }

    override fun getItemCount(): Int = fixed.size

    @SuppressLint("NotifyDataSetChanged")
    fun addFixed(fixed : ArrayList<FixedSchedule>){
        this.fixed.clear()
        this.fixed.addAll(fixed)
//        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding : ItemFixedScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fixed: FixedSchedule){
            binding.itemFixedTimeStart.text = "${fixed.startH}:${fixed.startM}"
            binding.itemFixedTimeEnd.text = "${fixed.endH}:${fixed.endM}"
            binding.itemFixedName.text = fixed.name
        }
    }
}