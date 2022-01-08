package com.example.catchtheball

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask

class MainActivity: AppCompatActivity() {

    private lateinit var scoreLabel: TextView
    private lateinit var startLabel: TextView
    private lateinit var box: ImageView
    private lateinit var orange: ImageView
    private lateinit var pink: ImageView
    private lateinit var black: ImageView

    // サイズ
    private var frameHeight: Int = 0
    private var boxSize: Int = 0
    private var screenWidth: Int = 0

    // ボックスやボールの位置
    private var boxY: Float = 0f
    private var orangeX: Float = 0f
    private var orangeY: Float = 0f
    private var pinkX: Float = 0f
    private var pinkY: Float = 0f
    private var blackX: Float = 0f
    private var blackY: Float = 0f

    // スピード
    private var boxSpeed: Int = 0
    private var orangeSpeed: Int = 0
    private var pinkSpeed: Int = 0
    private var blackSpeed: Int = 0

    // Score
    private var score = 0

    // Handler & Timer
    private val handler = Handler(Looper.getMainLooper())
    private var timer: Timer? = Timer()

    // Status
    private var actionFlg = false
    private var startFlg = false

    // Sound
    private lateinit var soundPlayer: SoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        soundPlayer = SoundPlayer(this)

        scoreLabel = findViewById(R.id.scoreLabel)
        startLabel = findViewById(R.id.startLabel)
        box = findViewById(R.id.box)
        orange = findViewById(R.id.orange)
        pink = findViewById(R.id.pink)
        black = findViewById(R.id.black)

        val wm: WindowManager = getWindowManager()
        val display: Display = wm.getDefaultDisplay()
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        val screenHeight = size.y

        boxSpeed = Math.round(screenHeight / 60f)
        orangeSpeed = Math.round(screenWidth / 60f)
        pinkSpeed = Math.round(screenWidth / 36f)
        blackSpeed = Math.round(screenWidth / 45f)

        // 始めは画面の外に置いておく
        orange.x = -80.0f
        orange.y = -80.0f
        pink.x = -80.0f
        pink.y = -80.0f
        black.x = -80.0f
        black.y = -80.0f

        scoreLabel.setText(getString(R.string.score, 0))
    }

    private fun changePos() {

        hitCheck()

        scoreLabel.setText(getString(R.string.score, score))

        // Orange
        orangeX -= orangeSpeed
        if (orangeX < 0) {
            orangeX = screenWidth + 20f
            orangeY = randomHeight(orange.getHeight())
        }
        orange.setX(orangeX)
        orange.setY(orangeY)

        // Black
        blackX -= blackSpeed
        if (blackX < 0) {
            blackX = screenWidth + 10f
            blackY = randomHeight(black.getHeight())
        }
        black.setX(blackX)
        black.setY(blackY)

        // Pink
        pinkX -= pinkSpeed
        if (pinkX < 0) {
            pinkX = screenWidth + 5000f
            pinkY = randomHeight(pink.getHeight())
        }
        pink.setX(pinkX)
        pink.setY(pinkY)

        if (actionFlg) {
            boxY -= boxSpeed
        } else {
            boxY += boxSpeed
        }

        if (boxY < 0) boxY = 0f

        if (boxY > frameHeight - boxSize) boxY = (frameHeight - boxSize).toFloat()

        box.setY(boxY)
    }

    private fun randomHeight(height: Int): Float {
        return Math.floor(Math.random() * (frameHeight - height)).toFloat()
    }

    private fun hitCheck() {
        // Orange
        val orangeCenterX: Float = orangeX + orange.getWidth() / 2.0f
        val orangeCenterY: Float = orangeY + orange.getHeight() / 2.0f

        if (hitStatus(orangeCenterX, orangeCenterY)) {
            orangeX = -10.0f
            score += 10
            soundPlayer.playHitSound()
        }

        // Pink
        val pinkCenterX: Float = pinkX + pink.getWidth() / 2.0f
        val pinkCenterY: Float = pinkY + pink.getHeight() / 2.0f

        if (hitStatus(pinkCenterX, pinkCenterY)) {
            pinkX = -10.0f
            score += 30
            soundPlayer.playHitSound()
        }

        // Black
        val blackCenterX: Float = blackX + black.getWidth() / 2.0f
        val blackCenterY: Float = blackY + black.getHeight() / 2.0f

        if (hitStatus(blackCenterX, blackCenterY)) {
            // Game Over!
            if (timer != null) {
                timer!!.cancel()
                timer = null
                soundPlayer.playOverSound()
            }

            // 結果画面へ
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("SCORE", score)
            startActivity(intent)
            finish()
        }
    }

    private fun hitStatus(centerX: Float, centerY: Float): Boolean {
        return (0 <= centerX && centerX <= boxSize && boxY <= centerY && centerY <= boxY + boxSize)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!startFlg) {
            startFlg = true

            val frame: FrameLayout = findViewById(R.id.frame)
            frameHeight = frame.getHeight()

            boxY = box.getY()
            boxSize = box.getHeight()

            startLabel.setVisibility(View.GONE)

            timer?.schedule(object: TimerTask() {
                override fun run() {
                    handler.post {
                        changePos()
                    }
                }
            }, 0, 20)


        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                actionFlg = true

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                actionFlg = false

            }
        }
        return true
    }

    override fun onBackPressed() {}
}
