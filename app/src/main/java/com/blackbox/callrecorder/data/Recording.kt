package com.blackbox.callrecorder.data

import java.io.File

data class Recording(
    val file: File,
    val contactName: String?,
    val phoneNumber: String?,
    val createdAt: Long,
    val durationMs: Long
)
