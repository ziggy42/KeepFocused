package com.andreapivetta.keepfocused;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreapivetta.keepfocused.settings.SettingsActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.Random;


public class MainActivity extends Activity {

    private ImageButton firstButton, secondButton, thirdButton;
    private ImageView firstImageView, secondImageView, thirdImageView;
    private TextView scoreTextView;
    private Random random;
    private CountDownTimer timer;
    private int currentColor;
    private boolean correctAnswer = true, running = false;
    private SharedPreferences mSharedPreferences, prefs;
    private MediaPlayer mp1, mpGO;
    private InterstitialAd interstitial;
    private int count = 0, level = 1, nLevels = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences("MyPref", 0);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        firstButton = (ImageButton) findViewById(R.id.firstButton);
        secondButton = (ImageButton) findViewById(R.id.secondButton);
        thirdButton = (ImageButton) findViewById(R.id.thirdButton);
        firstImageView = (ImageView) findViewById(R.id.firstImageView);
        secondImageView = (ImageView) findViewById(R.id.secondImageView);
        thirdImageView = (ImageView) findViewById(R.id.thirdImageView);
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        random = new Random();
        mp1 = MediaPlayer.create(this, R.raw.sound);
        mpGO = MediaPlayer.create(this, R.raw.gameover);

        setUpOnClickListener();
        disenable(false);
        restoreTimer();

        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(mSharedPreferences.getInt("BGAct", R.color.turquoise))));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(getResources().getColor(mSharedPreferences.getInt("BGAct", R.color.turquoise)));
        }
        findViewById(R.id.rootRelLayout).setBackgroundColor(getResources().getColor(mSharedPreferences.getInt("BG", R.color.green_sea)));

        if (!running) {
            scoreTextView.setTextSize(40);
            scoreTextView.setText(getResources().getString(R.string.start_message));
            disenable(true);
        }

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-8642726692616831/5265085502");
        AdRequest adRequest = new AdRequest.Builder().
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
                addTestDevice("EB8CB71D9FE394E0DCCBF26188BED5D7").
                addTestDevice("A272A918ED2BBA9EC2138C622D7212D0").
                addTestDevice("C9F505E68A8DADEB86EF831BD769444D").
                build();
        interstitial.loadAd(adRequest);

        if(mSharedPreferences.getInt("OPENED", 0) > 20) {
            askPromo();
            mSharedPreferences.edit().putInt("OPENED", 0).apply();
        } else {
            mSharedPreferences.edit().putInt("OPENED", mSharedPreferences.getInt("OPENED", 0) + 1).apply();
        }
    }

    private void setUpColors() {
        int index = random.nextInt(3);
        int color = setColorId(random.nextInt(3));

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
        int duration = (Levels.getTicksFromLevel(Levels.newLevel(level, nLevels)))*50 + 50;

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

    private void gameOver() {
        int points;
        try {
            points = Integer.parseInt(scoreTextView.getText().toString());
        } catch (NumberFormatException e) {
            points = 0;
        }

        if (points >= 5)
            displayInterstitial();

        running = false;
        scoreTextView.setTextSize(40);
        scoreTextView.setText(getResources().getString(R.string.start_message));
        disenable(false);

        if (prefs.getBoolean("SOUND", true))
            mpGO.start();

        timer.cancel();
        correctAnswer = false;
        restoreColors();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.game_lost);
        builder.setCancelable(false);

        if (points > mSharedPreferences.getInt("Record", 0)) {
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putInt("Record", points);
            e.apply();
            builder.setMessage("Woah!! " + points + " " + getString(R.string.record_congrats));
        }

        builder.setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                scoreTextView.setText("");
                disenable(false);
                countDownAnimation();
            }
        });
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                disenable(true);
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
                scoreTextView.setTextSize(120);
                correctAnswer = true;
                restoreTimer();
                timer.start();
                disenable(true);
                running = true;
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
        if (!running) {
            countDownAnimation();
            running = !running;
            scoreTextView.setText("");
            disenable(false);
        } else {
            if (currentColor == i) {
                if (prefs.getBoolean("SOUND", false))
                    playSound();

                scoreTextView.setText((Integer.parseInt(scoreTextView.getText().toString()) + 1) + "");
                correctAnswer = true;
                disenable(false);
            } else {
                gameOver();
            }
        }
    }

    private void disenable(boolean enableAll) {
        firstButton.setEnabled(enableAll);
        secondButton.setEnabled(enableAll);
        thirdButton.setEnabled(enableAll);
    }

    public void restoreTimer() {
        count = 0;
        level = 1;
        nLevels = 0;

        timer = new CountDownTimer(3606000, 100) {

            public void onTick(long millisUntilFinished) {
                if(count == 0 || count == Levels.getTicksFromLevel(level)) {
                    if (!correctAnswer) {
                        gameOver();
                    } else {
                        setUpColors();
                        correctAnswer = false;
                        disenable(true);
                    }

                    level = Levels.newLevel(level, nLevels);
                    nLevels ++;
                    count = 0;
                }
                count++;
            }

            public void onFinish() {
                timer.start();
            }
        };
    }

    private void playSound() {
        mp1.start();
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(mSharedPreferences.getInt("BGAct", R.color.turquoise))));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(getResources().getColor(mSharedPreferences.getInt("BGAct", R.color.turquoise)));
        }
        findViewById(R.id.rootRelLayout).setBackgroundColor(getResources().getColor(mSharedPreferences.getInt("BG", R.color.green_sea)));
        invalidateOptionsMenu();
    }

    public void displayInterstitial() {
        if (interstitial.isLoaded())
            interstitial.show();

        AdRequest adRequest = new AdRequest.Builder().
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
                addTestDevice("EB8CB71D9FE394E0DCCBF26188BED5D7").
                addTestDevice("A272A918ED2BBA9EC2138C622D7212D0").
                addTestDevice("C9F505E68A8DADEB86EF831BD769444D").
                build();
        interstitial.loadAd(adRequest);
    }

    public void askPromo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.rate_me_title));
        builder.setMessage(getResources().getString(R.string.rate_me_message));

        builder.setCancelable(false);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.andreapivetta.keepfocused"));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
