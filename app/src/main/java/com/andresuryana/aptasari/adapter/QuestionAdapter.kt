package com.andresuryana.aptasari.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.data.model.Question
import com.andresuryana.aptasari.databinding.ItemQuestionBinding
import com.andresuryana.aptasari.util.QuizType

class QuestionAdapter(
    private val answerAdapter: AnswerAdapter
) : ListAdapter<Question, QuestionAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionAdapter.ViewHolder {
        return ViewHolder(
            ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: QuestionAdapter.ViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class ViewHolder(private val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(question: Question) {
            // Set title
            binding.tvTitle.text = question.title ?:
                if (question.type == QuizType.AUDIO) itemView.context.getString(R.string.title_quiz_audio)
                else itemView.context.getString(R.string.title_quiz_text)

            // Current question type
            when (question.type) {
                QuizType.TEXT -> setTextQuestion(question)
                QuizType.AUDIO -> setAudioQuestion(question)
                else -> Unit
            }

            // Setup adapter
            answerAdapter.setRecyclerViewTarget(binding.rvAnswer)
            answerAdapter.submitList(question.answers)
            binding.rvAnswer.apply {
                adapter = answerAdapter
                layoutManager = LinearLayoutManager(itemView.context, VERTICAL, false)
            }
        }

        private fun setTextQuestion(question: Question) {
            // Update ui
            binding.tvQuestion.visibility = View.VISIBLE
            binding.audioPlayerContainer.visibility = View.GONE

            // Set text question
            binding.tvQuestion.text = question.textQuestion
        }

        private fun setAudioQuestion(question: Question) {
            // Update ui
            binding.tvQuestion.visibility = View.GONE
            binding.audioPlayerContainer.visibility = View.VISIBLE

            // Set audio question
            // TODO: Create callback to play the audio in the fragment! Don't play it in adapter
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem == newItem
            }
        }
    }
}