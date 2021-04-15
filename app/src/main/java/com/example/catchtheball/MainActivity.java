package com.example.catchtheball;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView box;
    private ImageView orange;
    private ImageView pink;
    private ImageView black;

    // サイズ
    private int frameHeight;
    private int boxSize;
    private int screenWidth;

    // ボックスやボールの位置
    private float boxY;
    private float orangeX;
    private float orangeY;
    private float pinkX;
    private float pinkY;
    private float blackX;
    private float blackY;

    // スピード
    private int boxSpeed;
    private int orangeSpeed;
    private int pinkSpeed;
    private int blackSpeed;

    // Score
    private int score = 0;

    // Handler & Timer
    private final Handler handler = new Handler();
    private Timer timer = new Timer();

    // Status
    private boolean actionFlg = false;
    private boolean startFlg = false;

    // Sound
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);
        black = findViewById(R.id.black);

        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        int screenHeight = size.y;

        boxSpeed = Math.round(screenHeight / 60f);
        orangeSpeed = Math.round(screenWidth / 60f);
        pinkSpeed = Math.round(screenWidth / 36f);
        blackSpeed = Math.round(screenWidth / 45f);

        // 始めは画面の外に置いておく
        orange.setX(-80.0f);
        orange.setY(-80.0f);
        pink.setX(-80.0f);
        pink.setY(-80.0f);
        black.setX(-80.0f);
        black.setY(-80.0f);

        scoreLabel.setText(getString(R.string.score, 0));
    }

    private void changePos() {

        hitCheck();

        scoreLabel.setText(getString(R.string.score, score));

        // Orange
        orangeX -= orangeSpeed;
        if (orangeX < 0) {
            orangeX = screenWidth + 20;
            orangeY = randomHeight(orange.getHeight());
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        // Black
        blackX -= blackSpeed;
        if (blackX < 0) {
            blackX = screenWidth + 10;
            blackY = randomHeight(black.getHeight());
        }
        black.setX(blackX);
        black.setY(blackY);

        // Pink
        pinkX -= pinkSpeed;
        if (pinkX < 0) {
            pinkX = screenWidth + 5000;
            pinkY = randomHeight(pink.getHeight());
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        if (actionFlg) {
            boxY -= boxSpeed;
        } else {
            boxY += boxSpeed;
        }

        if (boxY < 0) boxY = 0;

        if (boxY > frameHeight - boxSize) boxY = frameHeight - boxSize;

        box.setY(boxY);
    }

    private float randomHeight(int height) {
        return (float) Math.floor(Math.random() * (frameHeight - height));
    }

    private void hitCheck() {
        // Orange
        float orangeCenterX = orangeX + orange.getWidth() / 2.0f;
        float orangeCenterY = orangeY + orange.getHeight() / 2.0f;

        if (hitStatus(orangeCenterX, orangeCenterY)) {
            orangeX = -10.0f;
            score += 10;
            soundPlayer.playHitSound();
        }

        // Pink
        float pinkCenterX = pinkX + pink.getWidth() / 2.0f;
        float pinkCenterY = pinkY + pink.getHeight() / 2.0f;

        if (hitStatus(pinkCenterX, pinkCenterY)) {
            pinkX = -10.0f;
            score += 30;
            soundPlayer.playHitSound();
        }

        // Black
        float blackCenterX = blackX + black.getWidth() / 2.0f;
        float blackCenterY = blackY + black.getHeight() / 2.0f;

        if (hitStatus(blackCenterX, blackCenterY)) {
            // Game Over!
            if (timer != null) {
                timer.cancel();
                timer = null;
                soundPlayer.playOverSound();
            }

            // 結果画面へ
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
            finish();
        }
    }

    private boolean hitStatus(float centerX, float centerY) {
        return (0 <= centerX && centerX <= boxSize && boxY <= centerY && centerY <= boxY + boxSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!startFlg) {
            startFlg = true;

            FrameLayout frame = findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            boxY = box.getY();
            boxSize = box.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);

        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                actionFlg = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                actionFlg = false;

            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {}
}
