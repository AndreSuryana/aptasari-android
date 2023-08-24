package com.andresuryana.aptasari.util

import androidx.annotation.StringRes
import com.andresuryana.aptasari.R

enum class RecorderStatus(@StringRes val text: Int) {
    WAITING(R.string.record_waiting),
    RECORDING(R.string.record_recording),
    STOPPED(R.string.record_stopped)
}