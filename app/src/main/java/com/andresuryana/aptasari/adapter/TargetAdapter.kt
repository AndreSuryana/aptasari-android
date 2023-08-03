package com.andresuryana.aptasari.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.aptasari.databinding.ItemLearningTargetBinding
import com.andresuryana.aptasari.util.LearningTarget

class TargetAdapter : RecyclerView.Adapter<TargetAdapter.ViewHolder>() {

    private var list = ArrayList<LearningTarget>()
    private var onItemClickListener: ((target: LearningTarget) -> Unit)? = null

    private val topCornerRadius = floatArrayOf(24f, 24f, 0f, 0f)
    private val bottomCornerRadius = floatArrayOf(24f, 24f, 0f, 0f)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLearningTargetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<LearningTarget>) {
        this.list.clear()
        this.list.addAll(list)
        this.notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: (target: LearningTarget) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    inner class ViewHolder(private val binding: ItemLearningTargetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(target: LearningTarget, position: Int) {
            // Modify card corner radius
            if (position == 0 || position == list.size - 1) {
                val shape = ShapeDrawable(
                    RoundRectShape(
                        if (position == 0) topCornerRadius else bottomCornerRadius,
                        null,
                        null
                    )
                )
                binding.root.background = shape
            }

            // Set text
            binding.tvTitle.setText(target.title)
            binding.tvDescription.setText(target.description)

            // Set click listener
            itemView.setOnClickListener { onItemClickListener?.invoke(target) }
        }
    }
}