package com.example.boss.screens.splash

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.boss.R
import com.example.boss.databinding.FragmentOnboarding3Binding

class ThirdFragment : Fragment() {

    private var _binding : FragmentOnboarding3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboarding3Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.onboarding3Next.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingFragment_to_calendarFragment)
            onBoardingFinished()
        }
    }

    private fun onBoardingFinished() {
        val prefs = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("finished",true).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ThirdFragment()
    }
}