package com.andresuryana.aptasari.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andresuryana.aptasari.data.source.local.DatabaseContract.LevelTable

@Entity(tableName = LevelTable.TABLE_NAME)
data class LevelEntity(

    @PrimaryKey
    val id: String,

    @ColumnInfo(LevelTable.COLUMN_TITLE)
    val title: String,

    @ColumnInfo(LevelTable.COLUMN_ICON_PATH)
    val iconPath: String
)
