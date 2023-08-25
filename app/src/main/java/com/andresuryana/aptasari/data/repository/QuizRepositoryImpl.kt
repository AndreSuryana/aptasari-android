package com.andresuryana.aptasari.data.repository

import com.andresuryana.aptasari.data.DataConverter.toLevel
import com.andresuryana.aptasari.data.DataConverter.toQuestion
import com.andresuryana.aptasari.data.model.ANNPrediction
import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.data.model.Question
import com.andresuryana.aptasari.data.model.QuizData
import com.andresuryana.aptasari.data.source.local.LocalDatabase
import com.andresuryana.aptasari.data.source.remote.ANNService
import com.andresuryana.aptasari.data.source.remote.ApiService
import com.andresuryana.aptasari.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class QuizRepositoryImpl(
    private val local: LocalDatabase,
    private val remote: ApiService,
    private val annService: ANNService
) : QuizRepository {

    override suspend fun fetchLevels(): Resource<List<Level>> {
        return try {
            Resource.Success(
                local.levelDao().getAllLevel().map { it.toLevel() }.sortedBy { it.order }
            )
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun fetchQuestionByLevel(levelId: String): Resource<List<Question>> {
        return try {
            Resource.Success(
                local.questionDao().getQuestionByLevelId(levelId).map {
                    val answers = local.answerDao().getAnswerByQuestionId(it.id)
                    it.toQuestion(answers)
                }
            )
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun fetchQuizData(): QuizData {
        return remote.getQuizData()
    }

    override suspend fun predictAudio(
        actualClass: String,
        audio: File
    ): Resource<ANNPrediction> {
        return try {
            // Create 'audio' file part
            val audioPart = MultipartBody.Part.createFormData(
                "audio",
                audio.name,
                audio.asRequestBody("audio/*".toMediaTypeOrNull())
            )

            // Create 'actualClass' part
            val actualClassPart = actualClass.toRequestBody("text/plain".toMediaTypeOrNull())

            // Request to ANN Service
            val result = annService.predictAudio(audioPart, actualClassPart)
            if (result.status == "success" && result.data != null) {
                Resource.Success(result.data)
            } else {
                Resource.Error(result.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}