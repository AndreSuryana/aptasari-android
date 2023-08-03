package com.andresuryana.aptasari.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.databinding.ItemLevelBinding

class LevelAdapter : RecyclerView.Adapter<LevelAdapter.ViewHolder>() {

    private var list = ArrayList<Level>()
    private var onItemClickListener: ((level: Level) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelAdapter.ViewHolder {
        return ViewHolder(
            ItemLevelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LevelAdapter.ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Level>) {
        this.list.clear()
        this.list.addAll(list)
        this.notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: (level: Level) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    inner class ViewHolder(private val binding: ItemLevelBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(level: Level) {
            // Get icon resource
            @SuppressLint("DiscouragedApi")
            val iconId = itemView.context.resources.getIdentifier(level.iconResName, "drawable", itemView.context.packageName)

            // Set text
            binding.ivIcon.setImageResource(iconId)
            binding.tvTitle.text = level.title

            // Set click listener
            itemView.setOnClickListener { onItemClickListener?.invoke(level) }
        }
    }
}