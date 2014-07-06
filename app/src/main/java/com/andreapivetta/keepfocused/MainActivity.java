package com.andreapivetta.keepfocused;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreapivetta.keepfocused.settings.SettingsActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Random;


public class MainActivity extends Activity {

    private ImageButton firstButton, secondButton, thirdButton;
    private ImageView firstImageView, secondImageView, thirdImageView;
    private TextView scoreTextView;
    private Random random;
    private CountDownTimer timer;
    private int index, color, currentColor;
    private boolean correctAnswer = true;
    private SharedPreferences mSharedPreferences;
    private static String msInterval = "INTERVAL";

    private boolean restartEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences("MyPref", 0);
        firstButton = (ImageButton) findViewById(R.id.firstButton);
        secondButton = (ImageButton) findViewById(R.id.secondButton);
        thirdButton = (ImageButton) findViewById(R.id.thirdButton);
        firstImageView = (ImageView) findViewById(R.id.firstImageView);
        secondImageView = (ImageView) findViewById(R.id.secondImageView);
        thirdImageView = (ImageView) findViewById(R.id.thirdImageView);
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        random = new Random();
        setUpOnClickListener();
        disenable(false);

        timer = new CountDownTimer(3606000, mSharedPreferences.getInt(msInterval, 1000)) { // 800

            public void onTick(long millisUntilFinished) {
                if (!correctAnswer) {
                    gameOver();
                } else {
                    setUpColors();
                    correctAnswer = false;
                    disenable(true);
                }
            }

            public void onFinish() {
                timer.start();
            }
        };

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
                addTestDevice("F561EA0FF158FF7FC0B4E64B8FB39410").
                addTestDevice("A272A918ED2BBA9EC2138C622D7212D0").
                addTestDevice("C9F505E68A8DADEB86EF831BD769444D").
                build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        startGame();
    }

    private void setUpColors() {
        index = random.nextInt(3);
        color = setColorId(random.nextInt(3));

        switch (index) {
            case 0:
                firstImageView.setImageResource(color);
                secondImageView.setImageResource(R.drawable.basic);
                thirdImageView.setImageResource(R.drawable.basic);

                animateColor(firstImageView);
                break;
            case 1:
                secondImageView.setImageResource(color);
                firstImageView.setImageResource(R.drawable.basic);
                thirdImageView.setImageResource(R.drawable.basic);

                animateColor(secondImageView);
                break;
            case 2:
                thirdImageView.setImageResource(color);
                secondImageView.setImageResource(R.drawable.basic);
                firstImageView.setImageResource(R.drawable.basic);

                animateColor(thirdImageView);
                break;
        }
    }


    private void animateColor(ImageView image) {
        int duration = mSharedPreferences.getInt(msInterval, 1000)/2;

        Animation scale = new ScaleAnimation(1, 1.1f, 1, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation scale2 = new ScaleAnimation(1.1f, 1, 1.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(duration);
        scale2.setDuration(duration);
        scale2.setStartOffset(scale.getDuration());
        AnimationSet animSet = new AnimationSet(true);
        animSet.setFillEnabled(true);
        animSet.addAnimation(scale);
        animSet.addAnimation(scale2);
        image.startAnimation(animSet);
    }

    private void restoreColors() {
        firstImageView.setImageResource(R.drawable.basic);
        secondImageView.setImageResource(R.drawable.basic);
        thirdImageView.setImageResource(R.drawable.basic);
    }

    private int setColorId(int i) {
        currentColor = i;

        switch (i) {
            case 0:
                return R.drawable.blue;
            case 1:
                return R.drawable.red;
            case 2:
                return R.drawable.green;
            default:
                return 0;
        }
    }

    private void startGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.welcome_title);
        builder.setMessage(R.string.welcome_text);
        builder.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                countDownAnimation();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void gameOver() {
        timer.cancel();
        correctAnswer = false;
        disenable(false);
        restoreColors();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.game_lost);
        builder.setCancelable(false);

        int points = Integer.parseInt(scoreTextView.getText().toString());
        if (points > mSharedPreferences.getInt("Record", 0)) {
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putInt("Record", points);
            e.commit();
            builder.setMessage("Woah!! " + points + getString(R.string.record_congrats));
        }

        builder.setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                scoreTextView.setText("");
                restartEnabled = false;
                countDownAnimation();
            }
        });
        builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(0);
            }
        });
        builder.setNeutralButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();

        try {
            dialog.show();
        } catch (Exception e) {
            Log.i("Exception", "Game Over while the app is in background");
        }
    }

    public void countDownAnimation() {
        TextView countDownTextView = (TextView) findViewById(R.id.countDownTextView);
        countDownTextView.setVisibility(View.VISIBLE);
        CountDownAnimation countDownAnimation = new CountDownAnimation(countDownTextView, 3);

        countDownAnimation.setCountDownListener(new CountDownAnimation.CountDownListener() {
            @Override
            public void onCountDownEnd(CountDownAnimation animation) {
                scoreTextView.setText("0");
                correctAnswer = true;
                restoreTimer();
                timer.start();
                disenable(true);
                restartEnabled = true;
            }
        });

        countDownAnimation.start();
    }

    private void setUpOnClickListener() {
        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(0);
            }

        });

        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(1);
            }

        });

        thirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(2);
            }

        });
    }

    public void click(int i) {
        if (currentColor == i) {
            scoreTextView.setText((Integer.parseInt(scoreTextView.getText().toString()) + 1) + "");
            correctAnswer = true;
            disenable(false);
        } else {
            gameOver();
        }
    }

    private void disenable(boolean enableAll) {
        firstButton.setEnabled(enableAll);
        secondButton.setEnabled(enableAll);
        thirdButton.setEnabled(enableAll);
    }

    public void restoreTimer() {
        timer = new CountDownTimer(3606000, mSharedPreferences.getInt(msInterval, 800)) { // 800

            public void onTick(long millisUntilFinished) {
                if (!correctAnswer) {
                    gameOver();
                } else {
                    setUpColors();
                    correctAnswer = false;
                    disenable(true);
                }
            }

            public void onFinish() {
                timer.start();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        } else {
            if (id == R.id.action_restart && restartEnabled) {
                restartEnabled = false;
                restoreColors();
                timer.cancel();
                restoreTimer();
                scoreTextView.setText("");
                countDownAnimation();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
