package com.andresuryana.aptasari.data.repository

import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.data.model.Question

interface QuizRepository {

    suspend fun fetchLevels(): List<Level>

    suspend fun fetchQuestionByLevel(level: Level): List<Question>
}