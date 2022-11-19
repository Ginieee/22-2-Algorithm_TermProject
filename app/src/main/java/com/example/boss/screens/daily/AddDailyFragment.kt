package com.example.boss.screens.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.boss.R
import com.example.boss.databinding.FragmentAddDailyBinding

class AddDailyFragment : Fragment() {

    private lateinit var binding : FragmentAddDailyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_daily, container, false)

        binding.dailyAddImportantRadioBtn.setOnClickListener {
            onRadioButtonClicked(binding.dailyAddImportantRadioBtn)
        }
        binding.dailyAddNormalRadioBtn.setOnClickListener {
            onRadioButtonClicked(binding.dailyAddNormalRadioBtn)
        }

        return binding.root
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when(view.id) {
                R.id.daily_add_important_radio_btn -> {
                    if (checked) {

                    }
                }

                R.id.daily_add_normal_radio_btn -> {
                    if (checked) {

                    }
                }
            }
        }
    }


}