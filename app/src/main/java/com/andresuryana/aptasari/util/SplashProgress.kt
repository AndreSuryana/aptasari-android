package com.andresuryana.aptasari.util

import androidx.annotation.StringRes
import com.andresuryana.aptasari.R

enum class SplashProgress(@StringRes val text: Int) {

    CHECKING_UPDATES(R.string.title_checking_updates),
    UPDATING_DATA(R.string.title_updating_data),
    POPULATING_DATA(R.string.title_populating_data),
    POPULATE_SUCCESS(R.string.title_populate_success),
    POPULATE_ERROR(R.string.title_populate_error),
    FAILED_CHECKING_UPDATES(R.string.title_failed_check_update),
    RECHECK_UPDATES(R.string.title_recheck_updates),
    APP_LAUNCH(R.string.title_app_launch)

}