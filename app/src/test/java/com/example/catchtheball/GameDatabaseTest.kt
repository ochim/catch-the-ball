package com.example.catchtheball

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameDatabaseTest {
    private var gameDatabase: GameDatabase? = null

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        gameDatabase = GameDatabase(context = context)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testSaveHighScore() {
        gameDatabase?.saveHighScore(100)
        val score = gameDatabase?.highScore()
        assertThat(score).isEqualTo(100)
    }

    @Test
    fun testHighScore() {
        val score = gameDatabase?.highScore()
        assertThat(score).isEqualTo(0)
    }
}