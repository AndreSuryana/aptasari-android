package com.andresuryana.aptasari.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String?,
    val username: String?,
    val email: String?
) : Parcelable
