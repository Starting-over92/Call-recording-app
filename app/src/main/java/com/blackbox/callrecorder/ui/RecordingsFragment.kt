package com.blackbox.callrecorder.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blackbox.callrecorder.R
import com.blackbox.callrecorder.data.RecordingRepository
import com.blackbox.callrecorder.databinding.FragmentRecordingsBinding
import com.blackbox.callrecorder.utils.Constants

class RecordingsFragment : Fragment(R.layout.fragment_recordings) {

    private var _binding: FragmentRecordingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRecordingsBinding.bind(view)

        val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, 0)
        val isPremium = prefs.getBoolean(Constants.KEY_PREMIUM, false)
        val repository = RecordingRepository(requireContext())

        if (!repository.canRecord(isPremium) && !isPremium) {
            AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.free_limit_reached))
                .setPositiveButton(R.string.ok, null)
                .show()
        }

        val adapter = RecordingAdapter { recording ->
            val intent = Intent(requireContext(), PlayerActivity::class.java)
                .putExtra(PlayerActivity.EXTRA_FILE_PATH, recording.file.absolutePath)
            startActivity(intent)
        }

        binding.recyclerRecordings.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRecordings.adapter = adapter
        adapter.submitList(repository.getAllRecordings())
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
