package com.blackbox.callrecorder.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.blackbox.callrecorder.R
import com.blackbox.callrecorder.utils.Constants
import com.blackbox.callrecorder.utils.FileManager

class CallRecordingService : Service() {

    private var mediaRecorder: MediaRecorder? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(Constants.NOTIFICATION_ID, buildNotification())
        when (intent?.action) {
            Constants.ACTION_START_RECORDING -> startRecording()
            Constants.ACTION_STOP_RECORDING -> {
                stopRecording()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }

    private fun startRecording() {
        if (mediaRecorder != null) return
        val output = FileManager.createRecordingFile(this)
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(output.absolutePath)
            try {
                prepare()
                start()
            } catch (_: Exception) {
                release()
                mediaRecorder = null
                stopSelf()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.let { recorder ->
            try {
                recorder.stop()
            } catch (_: Exception) {
                // No-op: stop can fail on some device states.
            } finally {
                recorder.release()
            }
        }
        mediaRecorder = null
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
