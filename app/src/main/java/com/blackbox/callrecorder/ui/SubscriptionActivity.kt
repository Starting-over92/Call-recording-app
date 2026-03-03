package com.blackbox.callrecorder.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blackbox.callrecorder.billing.BillingManager
import com.blackbox.callrecorder.databinding.ActivitySubscriptionBinding

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubscriptionBinding
    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        billingManager = BillingManager(this)
        billingManager.connect {
            binding.btnSubscribe.setOnClickListener { billingManager.launchSubscription(this) }
            binding.btnRestore.setOnClickListener { billingManager.restorePurchases() }
        }
    }

    override fun onDestroy() {
        billingManager.endConnection()
        super.onDestroy()
    }
}
