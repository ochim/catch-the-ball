package com.example.catchtheball

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val scoreLabel = findViewById<TextView>(R.id.scoreLabel)
        val highScoreLabel = findViewById<TextView>(R.id.highScoreLabel)

        val score = intent.getIntExtra("SCORE", 0)
        scoreLabel.text = getString(R.string.result_score, score)

        val sharedPreferences = getSharedPreferences("GAME_DATA", MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("HIGH_SCORE", 0)
        if (score > highScore) {
            highScoreLabel.text = getString(R.string.high_score, score)
            val editor = sharedPreferences.edit()
            editor.putInt("HIGH_SCORE", score)
            editor.apply()
        } else {
            highScoreLabel.text = getString(R.string.score, highScore)
        }
    }

    fun tryAgain(view: View?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {}
}