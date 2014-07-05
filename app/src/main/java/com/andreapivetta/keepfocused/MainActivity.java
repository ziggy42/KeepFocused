package com.andreapivetta.keepfocused;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

        timer = new CountDownTimer(3606000, 800) {

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
                break;
            case 1:
                secondImageView.setImageResource(color);
                firstImageView.setImageResource(R.drawable.basic);
                thirdImageView.setImageResource(R.drawable.basic);
                break;
            case 2:
                thirdImageView.setImageResource(color);
                secondImageView.setImageResource(R.drawable.basic);
                firstImageView.setImageResource(R.drawable.basic);
                break;
        }
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
                /*scoreTextView.setText("0");
                correctAnswer = true;
                timer.start();*/
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.game_lost);
        builder.setCancelable(false);

        int points = Integer.parseInt(scoreTextView.getText().toString());
        if (points > mSharedPreferences.getInt("Record", 0)) {
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putInt("Record", points);
            e.commit();
            builder.setMessage("Woah!! " + points + " is your new Record! Nice game ;)");
        }

        builder.setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*scoreTextView.setText("0");
                correctAnswer = true;
                disenable(true);
                timer.start();*/
                scoreTextView.setText("");
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
        dialog.show();
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
                timer.start();
                disenable(true);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        } else {
            if (id == R.id.action_restart) {
                timer.cancel();
                /*disenable(true);
                correctAnswer = true;
                scoreTextView.setText("0");
                timer.start();*/

                scoreTextView.setText("");
                countDownAnimation();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
