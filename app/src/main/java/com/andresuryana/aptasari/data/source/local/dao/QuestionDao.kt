package com.andresuryana.aptasari.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andresuryana.aptasari.data.source.local.DatabaseContract.QuestionTable
import com.andresuryana.aptasari.data.source.local.entity.QuestionEntity

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT * FROM ${QuestionTable.TABLE_NAME} WHERE ${QuestionTable.COLUMN_LEVEL_ID} = :levelId")
    suspend fun getQuestionByLevelId(levelId: String): List<QuestionEntity>

    @Query("DELETE FROM ${QuestionTable.TABLE_NAME}")
    suspend fun deleteAll()
}