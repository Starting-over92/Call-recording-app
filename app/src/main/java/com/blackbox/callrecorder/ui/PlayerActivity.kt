package com.blackbox.callrecorder.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.blackbox.callrecorder.databinding.ActivityPlayerBinding
import com.blackbox.callrecorder.utils.FileManager
import java.io.File

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILE_PATH = "extra_file_path"
    }

    private lateinit var binding: ActivityPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val path = intent.getStringExtra(EXTRA_FILE_PATH) ?: run {
            finish(); return
        }
        file = File(path)
        setupPlayer()

        binding.btnPlayPause.setOnClickListener {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    binding.btnPlayPause.text = "Play"
                } else {
                    player.start()
                    binding.btnPlayPause.text = "Pause"
                    startSeekUpdates()
                }
            }
        }

        binding.btnRename.setOnClickListener {
            val input = com.google.android.material.textfield.TextInputEditText(this)
            MaterialAlertDialogBuilder(this)
                .setTitle("Rename recording")
                .setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val renamed = FileManager.renameRecording(file, input.text?.toString().orEmpty())
                    if (renamed != null) {
                        file = renamed
                        binding.tvFileName.text = file.name
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnShare.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/m4a"
                putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            }
            startActivity(Intent.createChooser(sendIntent, "Share recording"))
        }
    }

    private fun setupPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
        }
        binding.tvFileName.text = file.name
        val total = mediaPlayer?.duration ?: 0
        binding.seekBar.max = total
        binding.tvDuration.text = "${total / 1000}s"

        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) = Unit
        })
    }

    private fun startSeekUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                val current = mediaPlayer?.currentPosition ?: 0
                binding.seekBar.progress = current
                if (mediaPlayer?.isPlaying == true) handler.postDelayed(this, 300)
            }
        })
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}
