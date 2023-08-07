package com.andresuryana.aptasari.util

import androidx.annotation.StringRes
import com.andresuryana.aptasari.R

enum class SplashProgress(
    @StringRes var text: Int,
    val processStep: Int,
    val isError: Boolean = false
) {

    // Step 1
    CHECKING_UPDATES(R.string.title_checking_updates, 1),

    // Step 2
    UPDATING_DATA(R.string.title_updating_data, 2),
    FAILED_CHECKING_UPDATES(R.string.title_failed_check_update, 2, true),

    // Step 3
    POPULATING_DATA(R.string.title_populating_data, 3),

    // Step 4
    POPULATE_SUCCESS(R.string.title_populate_success, 4),
    POPULATE_ERROR(R.string.title_populate_error, 4, true),

    // Step 5
    DOWNLOAD_FILES(R.string.title_download_files, 5),

    // Step 6
    DOWNLOAD_FILES_PROCESS(R.string.title_download_files, 6),

    // Step 7
    DOWNLOAD_FILES_COMPLETED(R.string.title_download_files_completed, 7),
    DOWNLOAD_FILES_ERROR(R.string.title_download_files_error, 7, true),

    // Step Last
    APP_LAUNCH(R.string.title_app_launch, 8)

}