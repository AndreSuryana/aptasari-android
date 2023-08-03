package com.andresuryana.aptasari.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.andresuryana.aptasari.data.source.local.DatabaseContract.UserConfigTable

@Entity(
    tableName = UserConfigTable.TABLE_NAME,
    indices = [Index(value = [UserConfigTable.COLUMN_USER_ID], unique = true)]
)
data class UserConfigEntity(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = UserConfigTable.COLUMN_USER_ID)
    val userId: String,

    @ColumnInfo(name = UserConfigTable.COLUMN_IS_NOTIFY_TARGET)
    val isNotifyTarget: Boolean = false,

    @ColumnInfo(name = UserConfigTable.COLUMN_NOTIFY_DURATION)
    val notifyDuration: Long = 0

)
