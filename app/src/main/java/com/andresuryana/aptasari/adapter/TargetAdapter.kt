package com.andresuryana.aptasari.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.databinding.ItemLearningTargetBinding
import com.andresuryana.aptasari.util.LearningTarget

class TargetAdapter : ListAdapter<LearningTarget, TargetAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: ((target: LearningTarget) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLearningTargetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    fun setOnItemClickListener(onItemClickListener: (target: LearningTarget) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setSelectedItem(recyclerView: RecyclerView, target: LearningTarget) {
        val position = currentList.indexOf(target)
        for (i in 0 until recyclerView.childCount) {
            val itemView = recyclerView.getChildAt(i)
            itemView.backgroundTintList = if (i == position)
                ContextCompat.getColorStateList(recyclerView.context, R.color.header_background)
            else ContextCompat.getColorStateList(recyclerView.context, R.color.white)
        }
    }

    inner class ViewHolder(private val binding: ItemLearningTargetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(target: LearningTarget) {
            // Set text
            binding.tvTitle.setText(target.title)
            binding.tvDescription.setText(target.description)

            // Set click listener
            itemView.setOnClickListener { onItemClickListener?.invoke(target) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LearningTarget>() {

            override fun areItemsTheSame(
                oldItem: LearningTarget,
                newItem: LearningTarget
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: LearningTarget,
                newItem: LearningTarget
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}