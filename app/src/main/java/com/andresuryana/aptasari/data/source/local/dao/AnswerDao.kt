package com.andresuryana.aptasari.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andresuryana.aptasari.data.source.local.DatabaseContract.AnswerTable
import com.andresuryana.aptasari.data.source.local.entity.AnswerEntity

@Dao
interface AnswerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(answer: AnswerEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(answers: List<AnswerEntity>)

    @Query("SELECT * FROM ${AnswerTable.TABLE_NAME} WHERE ${AnswerTable.COLUMN_QUESTION_ID} = :questionId")
    suspend fun getAnswerByQuestionId(questionId: String): List<AnswerEntity>

    @Query("DELETE FROM ${AnswerTable.TABLE_NAME}")
    suspend fun deleteAll()
}