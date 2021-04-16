package com.example.catchtheball

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundPlayer(context: Context) {
    private var soundPool: SoundPool
    private var hitSound: Int
    private var overSound: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        soundPool = SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(2)
                .build()
        hitSound = soundPool.load(context, R.raw.hit, 1)
        overSound = soundPool.load(context, R.raw.over, 1)
    }

    fun playHitSound() {
        soundPool.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun playOverSound() {
        soundPool.play(overSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }

}