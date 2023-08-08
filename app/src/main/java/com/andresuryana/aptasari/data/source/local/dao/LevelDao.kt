package com.andresuryana.aptasari.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andresuryana.aptasari.data.source.local.DatabaseContract.LevelTable
import com.andresuryana.aptasari.data.source.local.entity.LevelEntity

@Dao
interface LevelDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(level: LevelEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(levels: List<LevelEntity>)

    @Query("SELECT * FROM ${LevelTable.TABLE_NAME}")
    suspend fun getAllLevel(): List<LevelEntity>

    @Query("UPDATE ${LevelTable.TABLE_NAME} SET ${LevelTable.COLUMN_ICON_PATH} = :iconPath WHERE ${LevelTable.COLUMN_ID} = :levelId")
    suspend fun updateLevelIconPath(levelId: String, iconPath: String)

    @Query("DELETE FROM ${LevelTable.TABLE_NAME}")
    suspend fun deleteAll()
}