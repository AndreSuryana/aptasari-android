package com.andresuryana.aptasari.util

object Ext {

    fun String.isEmail(): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return matches(emailRegex)
    }
}