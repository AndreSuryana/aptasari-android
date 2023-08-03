package com.andresuryana.aptasari.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.andresuryana.aptasari.data.source.local.DatabaseContract.UserConfigTable
import com.andresuryana.aptasari.data.source.local.entity.UserConfigEntity

@Dao
interface UserConfigDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userConfig: UserConfigEntity)

    @Query("SELECT * FROM ${UserConfigTable.TABLE_NAME} WHERE ${UserConfigTable.COLUMN_USER_ID} = :userId LIMIT 1")
    suspend fun getUserConfigByUserId(userId: String): UserConfigEntity?

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUserConfig(userConfig: UserConfigEntity)

    @Query("DELETE FROM ${UserConfigTable.TABLE_NAME}")
    suspend fun deleteAll()
}