package com.andresuryana.aptasari.util

import androidx.annotation.StringRes
import com.andresuryana.aptasari.R
import kotlin.time.Duration.Companion.minutes

enum class LearningTarget(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val duration: Long
) {
    RELAX(R.string.target_title_relax, R.string.target_description_relax, 5.minutes.inWholeMilliseconds),
    NORMAL(R.string.target_title_normal, R.string.target_description_normal, 10.minutes.inWholeMilliseconds),
    SERIOUS(R.string.target_title_serious, R.string.target_description_serious, 15.minutes.inWholeMilliseconds),
    INTENSE(R.string.target_title_intense, R.string.target_description_intense, 20.minutes.inWholeMilliseconds),
}