package com.andresuryana.aptasari.data

import com.andresuryana.aptasari.data.model.Answer
import com.andresuryana.aptasari.data.model.Level
import com.andresuryana.aptasari.data.model.Question
import com.andresuryana.aptasari.data.source.local.entity.AnswerEntity
import com.andresuryana.aptasari.data.source.local.entity.LevelEntity
import com.andresuryana.aptasari.data.source.local.entity.QuestionEntity
import com.andresuryana.aptasari.util.QuizType

object DataConverter {

    fun LevelEntity.toLevel(): Level {
        return Level(
            this.id,
            this.title,
            this.iconPath
        )
    }

    fun AnswerEntity.toAnswer(): Answer {
        return Answer(id, QuizType.fromId(type), questionId, isCorrect, title, textAnswer, audioPath)
    }

    fun QuestionEntity.toQuestion(answers: List<AnswerEntity>): Question {
        return Question(id, QuizType.fromId(type), title, textQuestion, audioPath, levelId, answers.map { it.toAnswer() })
    }

    fun Answer.toAnswerEntity(): AnswerEntity {
        return AnswerEntity(id, type?.id!!, questionId!!, isCorrect == true, title, textAnswer, audioPath)
    }

    fun Question.toQuestionEntity(): QuestionEntity {
        return QuestionEntity(id, type?.id!!, title, textQuestion, audioPath, levelId)
    }
}