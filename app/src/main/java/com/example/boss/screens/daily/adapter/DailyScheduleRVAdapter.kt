package com.example.boss.screens.daily.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.boss.data.entity.DailySchedule
import com.example.boss.data.entity.OrderedSchedule
import com.example.boss.databinding.ItemTodoBinding

class DailyScheduleRVAdapter : RecyclerView.Adapter<DailyScheduleRVAdapter.ViewHolder>() {

    private val daily = ArrayList<OrderedSchedule>()

    interface MyItemClickListener{
        fun onSendId(id:Int)
    }

    private lateinit var mItemClickListener : MyItemClickListener

    fun setMyItemClickListener(itemClickListener : MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemTodoBinding = ItemTodoBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(daily[position], position)

        holder.itemView.setOnClickListener {
            mItemClickListener.onSendId(daily[position].dailyId)
        }
    }

    override fun getItemCount(): Int = daily.size

    @SuppressLint("NotifyDataSetChanged")
    fun addDaily(daily : ArrayList<OrderedSchedule>) {
        this.daily.clear()
        this.daily.addAll(daily)
    }

    inner class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(daily : OrderedSchedule, position: Int) {
            binding.itemTodoOrder.text = (position + 1).toString()
            binding.itemTodoName.text = daily.name
            binding.itemTodoTime.text = daily.startH + ":" + daily.startM + " ~ " + daily.endH + ":" + daily.endM
            binding.itemTodoLessTime.text = daily.leftMinute.toString()
        }
    }
}