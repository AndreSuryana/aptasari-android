package com.andresuryana.aptasari.data.model

import com.andresuryana.aptasari.util.QuizType

data class Answer(
    val id: String,
    val type: QuizType? = null,
    val questionId: String? = null,
    val isCorrect: Boolean? = null,
    val title: String? = null,
    val textAnswer: String? = null,
    val audioPath: String? = null // NULL if type AUDIO
)
