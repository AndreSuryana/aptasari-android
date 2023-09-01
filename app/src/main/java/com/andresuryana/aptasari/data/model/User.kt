package com.andresuryana.aptasari.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String? = null,
    var username: String? = null,
    val email: String? = null,
    var address: String? = null
) : Parcelable
