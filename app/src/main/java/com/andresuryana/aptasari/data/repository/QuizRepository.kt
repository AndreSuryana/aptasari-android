package com.andresuryana.aptasari.data.repository

import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.data.model.Question
import com.andresuryana.aptasari.util.Resource

interface QuizRepository {

    suspend fun fetchLevels(): Resource<List<Level>>

    suspend fun fetchQuestionByLevel(level: Level): Resource<List<Question>>
}