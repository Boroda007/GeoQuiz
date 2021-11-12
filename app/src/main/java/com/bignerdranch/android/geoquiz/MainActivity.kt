package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView

    val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        prevButton = findViewById(R.id.prev_button)
        nextButton = findViewById(R.id.next_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)

        trueButton.setOnClickListener {
            checkAnswer(true)
            updateButtons()
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
            updateButtons()
        }

        val nextQuestion = { _: View ->
            quizViewModel.moveToNext()
            updateQuestion()
            updateButtons()
        }

        nextButton.setOnClickListener(nextQuestion)
        questionTextView.setOnClickListener(nextQuestion)

        cheatButton.setOnClickListener {
            // Начало CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatActivityResultLauncher.launch(intent)
        }

        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
            updateButtons()
        }

        updateQuestion()
        updateButtons()
    }

    private val cheatActivityResultLauncher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater = it.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun updateButtons() {
        val result = (quizViewModel.userAnswer == Answer.UNKNOWN)
        trueButton.isEnabled = result
        falseButton.isEnabled = result
        cheatButton.isEnabled = result
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        quizViewModel.userAnswer = if (userAnswer == correctAnswer) {
            Answer.CORRECT
        } else {
            Answer.INCORRECT
        }

        val message = when {
            quizViewModel.questionsIsOver() ->
                getString(R.string.percent_correct_answer).format(quizViewModel.percentCorrectAnswer())
            quizViewModel.isCheater ->  getString(R.string.judgment_toast)
            quizViewModel.userAnswer == Answer.CORRECT -> getString(R.string.correct_toast)
            else -> getString(R.string.incorrect_toast)
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}