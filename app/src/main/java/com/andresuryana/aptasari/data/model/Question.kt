package com.andresuryana.aptasari.data.model

import com.andresuryana.aptasari.util.QuizType

data class Question(
    val id: String,
    val type: QuizType?,
    val title: String,
    val textQuestion: String?,
    val audioPath: String?, // NULL if type AUDIO
    val levelId: String,
    val answers: List<Answer>,
)
