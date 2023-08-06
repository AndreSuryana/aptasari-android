package com.andresuryana.aptasari.util

import android.content.Context
import com.andresuryana.aptasari.data.source.local.entity.AnswerEntity
import com.andresuryana.aptasari.data.source.local.entity.LevelEntity
import com.andresuryana.aptasari.data.source.local.entity.QuestionEntity
import com.google.gson.Gson

object JsonDataConverter {

    fun getJsonFromRaw(context: Context, jsonId: Int): String {
        val inputStream = context.resources.openRawResource(jsonId)
        return inputStream.bufferedReader().use { it.readText() }
    }

    fun jsonToQuizData(json: String): QuizData {
        return Gson().fromJson(json, QuizData::class.java)
    }

    data class QuizData(
        val dataVersion: Long,
        val createdAt: Long,
        val updatedAt: Long,
        val level: List<LevelEntity>,
        val questions: List<QuestionEntity>,
        val answers: List<AnswerEntity>
    )
}