package com.andresuryana.aptasari.data.source.local

object DatabaseContract {

    const val DATABASE_NAME = "aptasari.db"

    object LevelTable {

        const val TABLE_NAME = "level_table"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ICON = "icon"
    }

    object QuestionTable {

        const val TABLE_NAME = "question_table"
        const val COLUMN_TYPE = "type"
        const val COLUMN_TITLE = "title"
        const val COLUMN_TEXT_QUESTION = "text_question"
        const val COLUMN_AUDIO_PATH = "audio_path"
        const val COLUMN_LEVEL_ID = "level_id"
    }

    object AnswerTable {

        const val TABLE_NAME = "answer_table"
        const val COLUMN_TYPE = "type"
        const val COLUMN_QUESTION_ID = "question_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_TEXT_ANSWER = "text_answer"
        const val COLUMN_AUDIO_PATH = "audio_path"
    }

    object UserConfigTable {

        const val TABLE_NAME = "user_config_table"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_IS_NOTIFY_TARGET = "is_notify_target"
        const val COLUMN_NOTIFY_DURATION = "notify_duration"
        const val COLUMN_CURRENT_PLAYTIME_DURATION = "current_playtime_duration"
    }
}