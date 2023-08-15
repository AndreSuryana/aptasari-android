package com.andresuryana.aptasari.data.model

import com.andresuryana.aptasari.data.source.local.entity.AnswerEntity
import com.andresuryana.aptasari.data.source.local.entity.LevelEntity
import com.andresuryana.aptasari.data.source.local.entity.QuestionEntity

data class QuizData(
    val dataVersion: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val level: HashMap<String, LevelEntity>,
    val questions: HashMap<String, QuestionEntity>,
    val answers: HashMap<String, AnswerEntity>
) {
    fun getLevel(): List<LevelEntity> {
        return this.level.map { it.value }.sortedBy { it.order }
    }

    fun getQuestions(): List<QuestionEntity> {
        return this.questions.map { it.value }.sortedBy { it.levelId }
    }

    fun getAnswers(): List<AnswerEntity> {
        return this.answers.map { it.value }.sortedBy { it.questionId }
    }
}