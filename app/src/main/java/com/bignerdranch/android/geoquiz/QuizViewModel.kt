package com.bignerdranch.android.geoquiz

//import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {

//    init {
//        Log.d(TAG, "ViewModel instance created")
//    }

//    override fun onCleared() {
//        super.onCleared()
//        Log.d(TAG, "ViewModel instance about to be destroyed")
//    }

    var currentIndex = 0
    private var answerCount = 0

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
    )

    private val answerBank = Array(questionBank.size) { Answer.UNKNOWN }
    private val cheaterBank = Array(questionBank.size) { false }

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var userAnswer: Answer
        get() = answerBank[currentIndex]
        set(value) {
            if (answerBank[currentIndex] == Answer.UNKNOWN) {
                answerBank[currentIndex] = value
                answerCount++
            }
        }

    var isCheater: Boolean
        get() = cheaterBank[currentIndex]
        set(value) {
            if (!cheaterBank[currentIndex]) {
                cheaterBank[currentIndex] = value
            }
        }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = if (currentIndex == 0) {
            questionBank.size - 1
        } else {
            currentIndex - 1
        }
    }

    fun questionsIsOver() = (answerCount == questionBank.size)

    fun percentCorrectAnswer(): Float {
        val countCorrectAnswer = answerBank.count { it == Answer.CORRECT }
        return countCorrectAnswer * 100.0F / answerBank.size
    }
}