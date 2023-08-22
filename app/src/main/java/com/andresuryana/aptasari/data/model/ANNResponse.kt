package com.andresuryana.aptasari.data.model

import com.google.gson.annotations.SerializedName

data class ANNResponse<T>(
    val status: String,
    val data: T?,
    val message: String?
)

data class ANNPrediction(

    @SerializedName("actual_class")
    val actualClass: String,

    @SerializedName("predicted_class")
    val predictedClass: String

)
