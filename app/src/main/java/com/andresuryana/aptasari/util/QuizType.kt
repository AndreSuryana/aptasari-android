package com.andresuryana.aptasari.util

enum class QuizType(val id: Int) {
    TEXT(0), AUDIO(1), AUDIO_INPUT(2);

    companion object {
        fun fromId(id: Int): QuizType? = values().find { it.id == id }
    }
}