package com.blackbox.callrecorder.data

import android.content.Context
import android.media.MediaMetadataRetriever
import com.blackbox.callrecorder.utils.Constants
import com.blackbox.callrecorder.utils.FileManager

class RecordingRepository(private val context: Context) {

    fun getAllRecordings(): List<Recording> {
        val files = FileManager.getRecordingsDir(context).listFiles()
            ?.filter { it.extension.equals("m4a", ignoreCase = true) }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()

        return files.map { file ->
            Recording(
                file = file,
                contactName = null,
                phoneNumber = parsePhoneFromName(file.name),
                createdAt = file.lastModified(),
                durationMs = readDuration(file.absolutePath)
            )
        }
    }

    fun canRecord(isPremium: Boolean): Boolean {
        return isPremium || getAllRecordings().size < Constants.FREE_RECORDING_LIMIT
    }

    fun remainingFreeSlots(): Int {
        return (Constants.FREE_RECORDING_LIMIT - getAllRecordings().size).coerceAtLeast(0)
    }

    private fun readDuration(path: String): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(path)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
            retriever.release()
            duration
        } catch (_: Exception) {
            0L
        }
    }

    private fun parsePhoneFromName(name: String): String? {
        val raw = name.substringBeforeLast('.').substringAfter("CALL_")
        return raw.takeIf { it.isNotBlank() }
    }
}
