package com.andresuryana.aptasari.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.databinding.ItemLevelBinding

class LevelAdapter : ListAdapter<Level, LevelAdapter.ViewHolder>(DIFF_CALLBACK), Filterable {

    private var originalList = ArrayList<Level>()
    private var onItemClickListener: ((level: Level) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelAdapter.ViewHolder {
        return ViewHolder(
            ItemLevelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LevelAdapter.ViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Level>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(originalList)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    originalList.forEach { level ->
                        if (level.title.lowercase().contains(filterPattern)) {
                            filteredList.add(level)
                        }
                    }
                }
                val result = FilterResults()
                result.values = filteredList
                return result
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                val filteredList = results?.values as? List<Level> ?: return
                submitList(filteredList)
            }
        }
    }

    fun setList(list: List<Level>) {
        this.originalList.clear()
        this.originalList.addAll(list)
        submitList(list)
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

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Level>() {

            override fun areItemsTheSame(oldItem: Level, newItem: Level): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Level, newItem: Level): Boolean {
                return oldItem == newItem
            }
        }
    }
}