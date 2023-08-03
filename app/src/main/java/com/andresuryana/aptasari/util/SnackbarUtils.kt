package com.andresuryana.aptasari.util

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

object SnackbarUtils {

    fun Fragment.showSnackbarError(@StringRes messageRes: Int) {
        Snackbar.make(requireView(), messageRes, Snackbar.LENGTH_SHORT).show()
    }
}