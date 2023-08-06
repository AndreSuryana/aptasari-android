package com.andresuryana.aptasari.data.source.remote

import com.andresuryana.aptasari.BuildConfig
import com.andresuryana.aptasari.data.model.QuizData
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(".json")
    suspend fun getQuizData(
        @Query("auth") auth: String = BuildConfig.API_KEY
    ): QuizData
}