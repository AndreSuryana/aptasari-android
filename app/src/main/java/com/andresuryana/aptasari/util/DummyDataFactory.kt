package com.andresuryana.aptasari.util

import com.andresuryana.aptasari.data.DataConverter.toAnswerEntity
import com.andresuryana.aptasari.data.DataConverter.toQuestionEntity
import com.andresuryana.aptasari.data.model.Answer
import com.andresuryana.aptasari.data.model.Question
import com.andresuryana.aptasari.data.source.local.entity.AnswerEntity
import com.andresuryana.aptasari.data.source.local.entity.LevelEntity
import com.andresuryana.aptasari.data.source.local.entity.QuestionEntity
import java.util.UUID

object DummyDataFactory {

    fun generateLevelData(): List<LevelEntity> {
        return listOf(
            LevelEntity(UUID.randomUUID().toString(), "Dasar 1", "ic_dasar_1"),
            LevelEntity(UUID.randomUUID().toString(), "Dasar 2", "ic_dasar_2")
        )
    }

    fun generateQuestionData(levelId: String, callback: (questions: List<QuestionEntity>, answers: List<AnswerEntity>) -> Unit) {
        val questions = mutableListOf<Question>()

        // Text Question
        val textQuestionId = UUID.randomUUID().toString()
        val answersTextQuestion = listOf(
            Answer(
                id = UUID.randomUUID().toString(),
                type = QuizType.TEXT,
                questionId = textQuestionId,
                isCorrect = true,
                title = "Madrid",
                textAnswer = "Madrid is the capital of Spain.",
                audioPath = null,
            ),
            Answer(
                id = UUID.randomUUID().toString(),
                type = QuizType.TEXT,
                questionId = textQuestionId,
                isCorrect = false,
                title = "Barcelona",
                textAnswer = "Barcelona is a major city in Spain, but not the capital.",
                audioPath = null
            ),
            Answer(
                id = UUID.randomUUID().toString(),
                type = QuizType.TEXT,
                questionId = textQuestionId,
                isCorrect = false,
                title = "Lisbon",
                textAnswer = "Lisbon is the capital of Portugal, not Spain.",
                audioPath = null
            ),
            Answer(
                id = UUID.randomUUID().toString(),
                type = QuizType.TEXT,
                questionId = textQuestionId,
                isCorrect = false,
                title = "Paris",
                textAnswer = "Paris is the capital of France, not Spain.",
                audioPath = null
            )
        )
        val textQuestion = Question(
            id = textQuestionId,
            type = QuizType.TEXT,
            title = "What is the capital of Spain?",
            textQuestion = "Choose the correct option:",
            audioPath = null,
            levelId = levelId,
            answers = answersTextQuestion
        )
        questions.add(textQuestion)

        // Audio Question
        val audioQuestionId = UUID.randomUUID().toString()
        val answersAudioQuestion = listOf(
            Answer(
                id = UUID.randomUUID().toString(),
                type = QuizType.TEXT,
                questionId = audioQuestionId,
                isCorrect = true,
                title = "Cat",
                textAnswer = "The sound is a cat meowing.",
                audioPath = null
            ),
            Answer(
                id = UUID.randomUUID().toString(),
                type = QuizType.TEXT,
                questionId = audioQuestionId,
                isCorrect = false,
                title = "Dog",
                textAnswer = "The sound is a dog barking.",
                audioPath = null
            ),
            Answer(
                id = UUID.randomUUID().toString(),
                type = QuizType.TEXT,
                questionId = audioQuestionId,
                isCorrect = false,
                title = "Bird",
                textAnswer = "The sound is a bird singing.",
                audioPath = null
            ),
            Answer(
                id = UUID.randomUUID().toString(),
                type = QuizType.TEXT,
                questionId = audioQuestionId,
                isCorrect = false,
                title = "Car",
                textAnswer = "The sound is a car engine.",
                audioPath = null
            )
        )
        val audioQuestion = Question(
            id = audioQuestionId,
            type = QuizType.AUDIO,
            title = "Identify the sound:",
            textQuestion = null,
            audioPath = "path/to/audio/file.mp3",
            levelId = levelId,
            answers = answersAudioQuestion
        )
        questions.add(audioQuestion)

        callback.invoke(
            questions.map { it.toQuestionEntity() },
            (answersTextQuestion + answersAudioQuestion).map { it.toAnswerEntity() }
        )
    }
}