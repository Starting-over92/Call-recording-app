package com.blackbox.callrecorder.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileManager {
    fun getRecordingsDir(context: Context): File {
        return File(context.getExternalFilesDir("recordings"), "").apply { mkdirs() }
    }

    fun createRecordingFile(context: Context): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(getRecordingsDir(context), "CALL_$timestamp.m4a")
    }

    fun renameRecording(file: File, newBaseName: String): File? {
        val safeName = newBaseName.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val newFile = File(file.parentFile, "$safeName.m4a")
        return if (file.exists() && file.renameTo(newFile)) newFile else null
    }
}
