package com.andresuryana.aptasari.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andresuryana.aptasari.data.source.local.DatabaseContract.QuestionTable

@Entity(tableName = QuestionTable.TABLE_NAME)
data class QuestionEntity(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = QuestionTable.COLUMN_TYPE)
    val type: Int,

    @ColumnInfo(name = QuestionTable.COLUMN_TITLE)
    val title: String?,

    @ColumnInfo(name = QuestionTable.COLUMN_TEXT_QUESTION)
    val textQuestion: String?,

    @ColumnInfo(name = QuestionTable.COLUMN_AUDIO_PATH)
    val audioPath: String?,

    @ColumnInfo(name = QuestionTable.COLUMN_LEVEL_ID)
    val levelId: String,


)
