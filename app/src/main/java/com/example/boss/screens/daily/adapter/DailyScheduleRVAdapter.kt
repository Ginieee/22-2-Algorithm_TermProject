package com.example.boss.screens.daily.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.boss.data.entity.DailySchedule
import com.example.boss.databinding.ItemTodoBinding

class DailyScheduleRVAdapter : RecyclerView.Adapter<DailyScheduleRVAdapter.ViewHolder>() {

    private val daily = ArrayList<DailySchedule>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemTodoBinding = ItemTodoBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(daily[position])
    }

    override fun getItemCount(): Int = daily.size

    @SuppressLint("NotifyDataSetChanged")
    fun addDaily(daily : ArrayList<DailySchedule>) {
        this.daily.clear()
        this.daily.addAll(daily)
    }

    inner class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(daily : DailySchedule) {
            binding.itemTodoOrder.text = daily.dailyId.toString()
            binding.itemTodoName.text = daily.name
            binding.itemTodoTime.text = "HH:MM ~ HH:MM"
            binding.itemTodoLessTime.text = "NN"
        }
    }
}