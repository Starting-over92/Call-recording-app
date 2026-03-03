package com.blackbox.callrecorder.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blackbox.callrecorder.BuildConfig
import com.blackbox.callrecorder.R
import com.blackbox.callrecorder.billing.BillingManager
import com.blackbox.callrecorder.databinding.FragmentSettingsBinding
import com.blackbox.callrecorder.utils.Constants

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var billingManager: BillingManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, 0)
        binding.switchAutoRecord.isChecked = prefs.getBoolean(Constants.KEY_AUTO_RECORD, false)
        binding.switchAutoRecord.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean(Constants.KEY_AUTO_RECORD, checked).apply()
        }

        billingManager = BillingManager(requireContext()).also { manager ->
            manager.connect()
            binding.btnRestorePurchase.setOnClickListener { manager.restorePurchases() }
        }

        binding.btnPrivacyPolicy.setOnClickListener {
            val url = Uri.parse("https://example.com/privacy")
            startActivity(Intent(Intent.ACTION_VIEW, url))
        }

        binding.tvVersion.text = getString(R.string.version_format, BuildConfig.VERSION_NAME)
    }

    override fun onDestroyView() {
        billingManager?.endConnection()
        _binding = null
        super.onDestroyView()
    }
}
