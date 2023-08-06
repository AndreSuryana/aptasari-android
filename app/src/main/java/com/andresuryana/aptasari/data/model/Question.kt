package com.andresuryana.aptasari.data.model

import com.andresuryana.aptasari.util.QuizType

data class Question(
    val id: String,
    val type: QuizType? = null,
    val title: String? = null,
    val textQuestion: String? = null,
    val audioPath: String? = null, // NULL if type AUDIO
    val levelId: String,
    val answers: List<Answer>,
)
