package com.andresuryana.aptasari.data.model

data class ANNResponse<T>(
    val status: String,
    val data: T?,
    val message: String?
)

data class ANNPrediction(
    val actualClass: String,
    val predictedClass: String
)
