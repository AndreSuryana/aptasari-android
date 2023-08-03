package com.andresuryana.aptasari.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andresuryana.aptasari.data.source.local.dao.AnswerDao
import com.andresuryana.aptasari.data.source.local.dao.LevelDao
import com.andresuryana.aptasari.data.source.local.dao.QuestionDao
import com.andresuryana.aptasari.data.source.local.dao.UserConfigDao
import com.andresuryana.aptasari.data.source.local.entity.AnswerEntity
import com.andresuryana.aptasari.data.source.local.entity.LevelEntity
import com.andresuryana.aptasari.data.source.local.entity.QuestionEntity
import com.andresuryana.aptasari.data.source.local.entity.UserConfigEntity

@Database(
    entities = [LevelEntity::class, QuestionEntity::class, AnswerEntity::class, UserConfigEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun levelDao(): LevelDao

    abstract fun questionDao(): QuestionDao

    abstract fun answerDao(): AnswerDao

    abstract fun userConfigDao(): UserConfigDao
}