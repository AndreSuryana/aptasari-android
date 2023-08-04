package com.andresuryana.aptasari.data.model

import com.andresuryana.aptasari.util.QuizType

data class Answer(
    val id: String,
    val type: QuizType?,
    val questionId: String?,
    val isCorrect: Boolean?,
    val title: String,
    val textAnswer: String?,
    val audioPath: String? // NULL if type AUDIO
)
