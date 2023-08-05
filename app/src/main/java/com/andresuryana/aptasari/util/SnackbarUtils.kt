package com.andresuryana.aptasari.util

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.andresuryana.aptasari.R
import com.google.android.material.snackbar.Snackbar

object SnackbarUtils {

    fun Fragment.showSnackbar(message: Int) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    fun Fragment.showSnackbar(messagePair: Pair<Int?, String?>) {
        val message = if (messagePair.first != null) getString(messagePair.first!!)
        else if (messagePair.second != null) messagePair.second!!
        else return
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    fun Fragment.showSnackbarError(messagePair: Pair<Int?, String?>) {
        val message = if (messagePair.first != null) getString(messagePair.first!!)
        else if (messagePair.second != null) messagePair.second!!
        else getString(R.string.error_default)
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
            .setBackgroundTintList(
                ContextCompat.getColorStateList(requireContext(), R.color.danger)
            )
            .setTextColor(
                ContextCompat.getColorStateList(requireContext(), R.color.white)
            )
            .show()
    }
}