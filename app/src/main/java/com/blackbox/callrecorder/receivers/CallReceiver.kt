package com.blackbox.callrecorder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.blackbox.callrecorder.data.RecordingRepository
import com.blackbox.callrecorder.utils.Constants
import com.blackbox.callrecorder.utils.PermissionManager

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return
        if (!PermissionManager.hasAllRequiredPermissions(context)) return

        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val autoRecord = prefs.getBoolean(Constants.KEY_AUTO_RECORD, false)
        val isPremium = prefs.getBoolean(Constants.KEY_PREMIUM, false)
        if (!autoRecord) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return
        val repository = RecordingRepository(context)

        when (state) {
            TelephonyManager.EXTRA_STATE_OFFHOOK,
            TelephonyManager.EXTRA_STATE_RINGING -> {
                if (repository.canRecord(isPremium)) {
                    val serviceIntent = Intent(context, com.blackbox.callrecorder.services.CallRecordingService::class.java).apply {
                        action = Constants.ACTION_START_RECORDING
                    }
                    ContextCompat.startForegroundService(context, serviceIntent)
                }
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {
                val serviceIntent = Intent(context, com.blackbox.callrecorder.services.CallRecordingService::class.java).apply {
                    action = Constants.ACTION_STOP_RECORDING
                }
                context.startService(serviceIntent)
            }
        }
    }
}
