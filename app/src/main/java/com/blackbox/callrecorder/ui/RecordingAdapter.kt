package com.blackbox.callrecorder.ui

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blackbox.callrecorder.data.Recording
import com.blackbox.callrecorder.databinding.ItemRecordingBinding

class RecordingAdapter(private val onClick: (Recording) -> Unit) :
    ListAdapter<Recording, RecordingAdapter.RecordingViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val binding = ItemRecordingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecordingViewHolder(private val binding: ItemRecordingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recording) {
            binding.tvName.text = item.contactName ?: "Unknown contact"
            binding.tvNumber.text = item.phoneNumber ?: "Unknown number"
            binding.tvDate.text = DateFormat.format("yyyy-MM-dd HH:mm", item.createdAt)
            binding.tvDuration.text = "${item.durationMs / 1000}s"
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Recording>() {
        override fun areItemsTheSame(oldItem: Recording, newItem: Recording): Boolean =
            oldItem.file.absolutePath == newItem.file.absolutePath

        override fun areContentsTheSame(oldItem: Recording, newItem: Recording): Boolean =
            oldItem == newItem
    }
}
