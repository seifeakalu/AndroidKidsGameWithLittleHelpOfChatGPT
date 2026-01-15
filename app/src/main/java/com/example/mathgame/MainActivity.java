package com.example.mathgame;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Animation shakeAnimation;
    private MediaPlayer errorSound;
    private MediaPlayer clapSound;
    private TextView tvQuestion, tvTimer, tvScore, tvLevel;
    private EditText etAnswer;
    private Button btnSubmit;
    private int score = 0;
    private int level = 1;
    private int num1, num2, num3;
    private double correctAnswer;

    private CountDownTimer countDownTimer;
    //private DBHelper dbHelper;
    DBHelper dbHelper;
    TextView tvBestScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        errorSound = MediaPlayer.create(this, R.raw.error_sound);
        clapSound = MediaPlayer.create(this, R.raw.clap_sound);
        dbHelper = new DBHelper(this);
        tvBestScore = findViewById(R.id.tvBestScore);

// Display current best score
        tvBestScore.setText("Best Score: " + dbHelper.getBestScore());

        tvQuestion = findViewById(R.id.tvQuestion);
        tvTimer = findViewById(R.id.tvTimer);
        tvScore = findViewById(R.id.tvScore);
        tvLevel = findViewById(R.id.tvLevel);
        etAnswer = findViewById(R.id.etAnswer);
        btnSubmit = findViewById(R.id.btnSubmit);

        dbHelper = new DBHelper(this);

        startLevel();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
    }

    private void generateQuestion() {
        Random random = new Random();

        // Generate numbers
        num1 = random.nextInt(9) + 1;
        num2 = random.nextInt(9) + 1;
        num3 = random.nextInt(9) + 1;

        if (level == 1) {
            correctAnswer = num1 + num2;
            tvQuestion.setText(num1 + " + " + num2);

        } else if (level == 2) {
            correctAnswer = num1 - num2;
            tvQuestion.setText(num1 + " - " + num2);

        } else if (level == 3) {
            correctAnswer = num1 * num2;
            tvQuestion.setText(num1 + " × " + num2);

        } else if (level == 4) {
            // For floating-point division
            correctAnswer = (double) num1 / num2;  // use double
            tvQuestion.setText(num1 + " ÷ " + num2);
        }
        else if (level < 10) {
            // Level 5–9 → 3-number addition
            correctAnswer = num1 + num2 + num3;
            tvQuestion.setText(num1 + " + " + num2 + " + " + num3);

        } else {
            // ⭐ LEVEL 10+ → Mixed multiplication & division with 3 numbers
            int operation = random.nextInt(2); // 0 = multiply first, 1 = divide first

            if (operation == 0) {
                // num1 × num2 + num3
                correctAnswer = (num1 * num2) + num3;
                tvQuestion.setText(num1 + " × " + num2 + " + " + num3);

            } else {
                // Ensure divisible
                num1 = num2 * num3;
                correctAnswer = (num1 / num2) + num3;
                tvQuestion.setText(num1 + " ÷ " + num2 + " + " + num3);
            }
        }
    }




    private void startLevel() {
        etAnswer.setText("");
        tvLevel.setText("Level: " + level);

        // Generate a new question **only once per level**
        generateQuestion();

        startTimer();
    }


    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(10000, 1000) { // 10 seconds per question
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Time: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Toast.makeText(MainActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();

                // Generate new numbers for the same level
                generateQuestion(); // new random question at the same level

                etAnswer.setText(""); // clear input

                startTimer(); // restart timer for the new question
            }


        }.start();
    }

    private void checkAnswer() {

        String answerText = etAnswer.getText().toString().trim();
        if (answerText.isEmpty()) {
            Toast.makeText(this, "Enter your answer!", Toast.LENGTH_SHORT).show();
            return;
        }

        double userAnswer;
        try {
            userAnswer = Double.parseDouble(answerText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

// Compare with a small tolerance
        if (Math.abs(userAnswer - correctAnswer) < 0.01) {
            // Correct
            if (clapSound != null) clapSound.start();
            score+=10;
            tvScore.setText("Score: " + score);

            level++;
            tvLevel.setText("Level: " + level);
            countDownTimer.cancel();
            generateQuestion();
        } else {
            // Wrong
            etAnswer.startAnimation(shakeAnimation);
            if (errorSound != null) errorSound.start();
            etAnswer.setText("");
        }



        etAnswer.setText(""); // clear input
        startTimer(); // restart timer
    }

    private void nextLevel() {
        countDownTimer.cancel();
        level++;
        startLevel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (errorSound != null) {
            errorSound.release();
            errorSound = null;
        }
        if (clapSound != null) {
            clapSound.release();
            clapSound = null;
        }
    }

}
