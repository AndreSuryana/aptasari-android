package com.andresuryana.aptasari.data.source.remote

import com.andresuryana.aptasari.data.model.ANNPrediction
import com.andresuryana.aptasari.data.model.ANNResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ANNService {

    @Multipart
    @POST("predict")
    suspend fun predictAudio(
        @Part audio: MultipartBody.Part,
        @Part("actual_class") actualClass: RequestBody
    ): ANNResponse<ANNPrediction>
}