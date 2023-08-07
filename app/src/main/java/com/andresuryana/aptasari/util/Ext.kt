package com.andresuryana.aptasari.util

import android.content.BroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object Ext {

    fun String.isEmail(): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return matches(emailRegex)
    }

    fun BroadcastReceiver.goAsync(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob()).launch(context) {
            try {
                block()
            } finally {
                pendingResult.finish()
            }
        }
    }

    fun Long.toMinutes(): Long = this / 60_000

    fun Long.formatTimer(): String {
        val hours = this / (60 * 60 * 1000)
        val minutes = (this % (60 * 60 * 1000)) / (60 * 1000)
        val seconds = (this % (60 * 1000)) / 1000

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun String.removeFileExtension(): String {
        val lastDotIndex = lastIndexOf('.')
        return if (lastDotIndex >= 0) {
            substring(0, lastDotIndex)
        } else {
            this
        }
    }
}