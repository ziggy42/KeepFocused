package com.andreapivetta.keepfocused.settings;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.andreapivetta.keepfocused.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(getSharedPreferences("MyPref", 0).getInt("BGAct", R.color.turquoise))));
        invalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(getSharedPreferences("MyPref", 0).getInt("BGAct", R.color.turquoise))));
        invalidateOptionsMenu();
    }
}
