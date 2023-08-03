package com.andresuryana.aptasari.data.repository

import android.util.Log
import com.andresuryana.aptasari.data.DataConverter.toLevel
import com.andresuryana.aptasari.data.DataConverter.toQuestion
import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.data.model.Question
import com.andresuryana.aptasari.data.source.local.LocalDatabase

class QuizRepositoryImpl(private val local: LocalDatabase) : QuizRepository {

    override suspend fun fetchLevels(): List<Level> {
        return try {
            local.levelDao().getAllLevel().map {
                it.toLevel()
            }
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, "fetchLevels: $e", e)
            throw e
        }
    }

    override suspend fun fetchQuestionByLevel(level: Level): List<Question> {
        return try {
            local.questionDao().getQuestionByLevelId(level.id).map {
                val answers = local.answerDao().getAnswerByQuestionId(it.id)
                it.toQuestion(answers)
            }
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, "fetchQuestionByLevel: $e", e)
            throw e
        }
    }
}