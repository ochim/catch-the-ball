package com.example.catchtheball

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class ResultActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var mAdView: AdView
    private var mInterstitialAd: InterstitialAd? = null
    private val TAG = "ResultActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        firebaseAnalytics = Firebase.analytics

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

        InterstitialAd.load(
            this,
            "ca-app-pub-4255460803337831/5659872823",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, adError.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
    }

    fun tryAgain(view: View?) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, "2")
            param(FirebaseAnalytics.Param.ITEM_NAME, "Play again")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "Button")
        }

        if (mInterstitialAd == null) {
            goToMain()
            return
        }

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                mInterstitialAd = null
                goToMain()
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                mInterstitialAd = null
                goToMain()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
                goToMain()
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
            }
        }
        mInterstitialAd?.show(this)
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {}
}