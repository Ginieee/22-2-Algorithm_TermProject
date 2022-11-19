package com.example.boss.screens.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.boss.databinding.FragmentOnboardingBinding
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnboardingFragment : Fragment() {


    private var _binding : FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var dotsIndicator : DotsIndicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        dotsIndicator = binding.onboardingDotsIndicatorDi

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewPater()
    }

    private fun setUpViewPater(){
        val fragmentList = arrayListOf(
            FirstFragment.newInstance(),
            SecondFragment.newInstance(),
            ThirdFragment.newInstance()
        )

        val adapter = OnboardingVPAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.onboardingViewPagerVp.adapter = adapter
        dotsIndicator.attachTo(binding.onboardingViewPagerVp)

        //binding.onboardingViewPagerVp.isUserInputEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}