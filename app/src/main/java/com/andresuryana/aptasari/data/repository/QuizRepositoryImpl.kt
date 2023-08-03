package com.andresuryana.aptasari.data.repository

import com.andresuryana.aptasari.data.DataConverter.toLevel
import com.andresuryana.aptasari.data.DataConverter.toQuestion
import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.data.model.Question
import com.andresuryana.aptasari.data.source.local.LocalDatabase
import com.andresuryana.aptasari.util.Resource

class QuizRepositoryImpl(private val local: LocalDatabase) : QuizRepository {

    override suspend fun fetchLevels(): Resource<List<Level>> {
        return try {
            Resource.Success(local.levelDao().getAllLevel().map { it.toLevel() })
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun fetchQuestionByLevel(level: Level): Resource<List<Question>> {
        return try {
            Resource.Success(
                local.questionDao().getQuestionByLevelId(level.id).map {
                    val answers = local.answerDao().getAnswerByQuestionId(it.id)
                    it.toQuestion(answers)
                }
            )
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}