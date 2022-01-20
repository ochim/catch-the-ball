package com.example.catchtheball

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class ResultActivity : AppCompatActivity() {
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val scoreLabel = findViewById<TextView>(R.id.scoreLabel)
        val highScoreLabel = findViewById<TextView>(R.id.highScoreLabel)

        val score = intent.getIntExtra("SCORE", 0)
        scoreLabel.text = getString(R.string.result_score, score)

        val gameDatabase = GameDatabase(applicationContext);
        val highScore = gameDatabase.highScore()
        if (score > highScore) {
            highScoreLabel.text = getString(R.string.high_score, score)
            gameDatabase.saveHighScore(score)
        } else {
            highScoreLabel.text = getString(R.string.high_score, highScore)
        }

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    fun tryAgain(view: View?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {}
}