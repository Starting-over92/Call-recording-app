package com.blackbox.callrecorder.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blackbox.callrecorder.R
import com.blackbox.callrecorder.data.RecordingRepository
import com.blackbox.callrecorder.databinding.FragmentHomeBinding
import com.blackbox.callrecorder.utils.Constants

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, 0)
        val repository = RecordingRepository(requireContext())
        binding.switchAutoRecord.isChecked = prefs.getBoolean(Constants.KEY_AUTO_RECORD, false)

        binding.switchAutoRecord.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean(Constants.KEY_AUTO_RECORD, checked).apply()
        }

        binding.btnUpgrade.setOnClickListener {
            startActivity(Intent(requireContext(), SubscriptionActivity::class.java))
        }

        val isPremium = prefs.getBoolean(Constants.KEY_PREMIUM, false)
        binding.tvRemaining.text = if (isPremium) {
            getString(R.string.premium_active)
        } else {
            getString(R.string.remaining_format, repository.remainingFreeSlots())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
