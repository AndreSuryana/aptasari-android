package com.andresuryana.aptasari.ui.quiz

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.andresuryana.aptasari.R

enum class QuizButtonState(@StringRes val buttonText: Int, @ColorRes val color: Int, val isClickable: Boolean) {

    CHECK(R.string.btn_check, R.color.primary, true),
    CHECKING(R.string.btn_checking, R.color.button_checking, false),
    WRONG(R.string.btn_wrong_answer, R.color.danger, false),
    CORRECT(R.string.btn_correct_answer, R.color.success, false),
    CONTINUE(R.string.btn_continue_quiz, R.color.primary, true),
    END(R.string.btn_end_quiz, R.color.primary, true)

}