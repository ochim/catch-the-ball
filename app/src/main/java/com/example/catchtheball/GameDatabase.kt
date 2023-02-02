package com.example.catchtheball

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class GameDatabase(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("GAME_DATA", AppCompatActivity.MODE_PRIVATE)
    }

    fun saveHighScore(score: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("HIGH_SCORE", score)
        editor.apply()
    }

    fun highScore(): Int {
        return sharedPreferences.getInt("HIGH_SCORE", 0)
    }

}