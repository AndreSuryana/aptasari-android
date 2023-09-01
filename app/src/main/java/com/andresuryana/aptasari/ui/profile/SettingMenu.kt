package com.andresuryana.aptasari.ui.profile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.andresuryana.aptasari.R

enum class SettingMenu(val id: String, @StringRes val title: Int, @DrawableRes val icon: Int) {

    ACCOUNT("account", R.string.title_account_setting, R.drawable.ic_account),
    LEARNING_TARGET("learning_target", R.string.title_learning_target, R.drawable.ic_notification),
    LOGOUT("logout", R.string.title_logout, R.drawable.ic_logout),
}