package com.andresuryana.aptasari.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andresuryana.aptasari.data.source.local.DatabaseContract.AnswerTable

@Entity(tableName = AnswerTable.TABLE_NAME)
data class AnswerEntity(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = AnswerTable.COLUMN_TYPE)
    val type: Int,

    @ColumnInfo(name = AnswerTable.COLUMN_QUESTION_ID)
    val questionId: String,

    @ColumnInfo(name = AnswerTable.COLUMN_IS_CORRECT)
    val isCorrect: Boolean,

    @ColumnInfo(name = AnswerTable.COLUMN_TITLE)
    val title: String?,

    @ColumnInfo(name = AnswerTable.COLUMN_TEXT_ANSWER)
    val textAnswer: String?,

    @ColumnInfo(name = AnswerTable.COLUMN_AUDIO_PATH)
    val audioPath: String?
)
