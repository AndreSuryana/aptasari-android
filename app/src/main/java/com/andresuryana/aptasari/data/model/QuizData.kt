package com.andresuryana.aptasari.data.model

import com.andresuryana.aptasari.data.source.local.entity.AnswerEntity
import com.andresuryana.aptasari.data.source.local.entity.LevelEntity
import com.andresuryana.aptasari.data.source.local.entity.QuestionEntity

data class QuizData(
        val dataVersion: Long,
        val createdAt: Long,
        val updatedAt: Long,
        val level: List<LevelEntity>,
        val questions: List<QuestionEntity>,
        val answers: List<AnswerEntity>
    )