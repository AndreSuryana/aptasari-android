package com.andresuryana.aptasari.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.data.model.Answer
import com.andresuryana.aptasari.databinding.ItemAnswerBinding
import com.andresuryana.aptasari.util.QuizType

private const val ASCII_CODE_START = 65

class AnswerAdapter : ListAdapter<Answer, AnswerAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: ((answer: Answer) -> Unit)? = null
    private var recyclerView: RecyclerView? = null

    var isItemClickable: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    fun setOnItemClickListener(onItemClickListener: (answer: Answer) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setSelectedItem(answer: Answer) {
        if (recyclerView != null) {
            val position = currentList.indexOf(answer)
            for (i in 0 until recyclerView!!.childCount) {
                val itemView = recyclerView!!.getChildAt(i)
                itemView.backgroundTintList = if (i == position)
                    ContextCompat.getColorStateList(recyclerView!!.context, R.color.header_background)
                else ContextCompat.getColorStateList(recyclerView!!.context, R.color.white)
            }
        }
    }

    fun setRecyclerViewTarget(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    inner class ViewHolder(private val binding: ItemAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(answer: Answer) {
            // Update ui according to answer type
            when (answer.type) {
                QuizType.TEXT -> setTextAnswer(answer)
                QuizType.AUDIO -> setAudioAnswer(answer)
                else -> Unit
            }

            // Set item click listener
            itemView.setOnClickListener {
                onItemClickListener?.invoke(answer)
            }
        }

        private fun setTextAnswer(answer: Answer) {
            // Update ui visibility
            binding.tvAnswer.visibility = View.VISIBLE
            binding.tvLabel.visibility = View.VISIBLE
            binding.btnPlayAudio.visibility = View.GONE

            val label = (ASCII_CODE_START + adapterPosition).toChar().toString()
            binding.tvLabel.text = label
            binding.tvAnswer.text = answer.textAnswer
        }

        private fun setAudioAnswer(answer: Answer) {
            // Update ui visibility
            binding.tvAnswer.visibility = View.GONE
            binding.tvLabel.visibility = View.GONE
            binding.btnPlayAudio.visibility = View.VISIBLE

            // Set audio question
            // TODO: Create callback to play the audio in the fragment! Don't play it in adapter
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Answer>() {

            override fun areItemsTheSame(
                oldItem: Answer,
                newItem: Answer
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Answer,
                newItem: Answer
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}