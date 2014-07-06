package com.andreapivetta.keepfocused.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andreapivetta.keepfocused.R;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        private Preference prefKeyRateApp, prefKeyShareApp, prefKeyRecord, prefKeySpeed;
        private SharedPreferences mSharedPreferences;
        private int record, currentInterval;

        private static String msInterval = "INTERVAL";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            mSharedPreferences = getActivity().getSharedPreferences("MyPref", 0);
            prefKeyRateApp = findPreference("pref_key_rate_app");
            prefKeyShareApp = findPreference("pref_key_share_app");
            prefKeyRecord = findPreference("pref_key_record");
            prefKeySpeed = findPreference("pref_key_speed");
            record = mSharedPreferences.getInt("Record", 0);
            prefKeyRecord.setSummary(record + "");

            prefKeyRecord
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND)
                                    .putExtra(
                                            Intent.EXTRA_TEXT,
                                            getActivity().getString(R.string.my_record_is) + record + getActivity().getString(R.string.can_you_beat_me))
                                    .setType("text/plain");
                            startActivity(Intent.createChooser(sendIntent, getActivity().getString(R.string.record_share)));

                            return false;
                        }
                    });

            prefKeyRateApp
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent i = new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=com.andreapivetta.keepfocused"))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            return false;
                        }
                    });

            prefKeyShareApp
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND)
                                    .putExtra(
                                            Intent.EXTRA_TEXT,
                                            getActivity().getString(R.string.check_out_app))
                                    .setType("text/plain");
                            startActivity(Intent.createChooser(sendIntent,
                                    getActivity().getString(R.string.share_app)
                            ));

                            return false;
                        }
                    });

            prefKeySpeed
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            final Dialog dialog = new Dialog(getActivity());
                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                            View layout = inflater.inflate(R.layout.dialog, (ViewGroup) getActivity().findViewById(R.id.dialogRootLinearLayout));
                            dialog.setContentView(layout);
                            dialog.setTitle(R.string.set_speed_title);

                            Button doneButton = (Button) layout.findViewById(R.id.doneButton);
                            SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seekBar);
                            final TextView seekBarTextView = (TextView) layout.findViewById(R.id.seekBarTextView);

                            int interval = mSharedPreferences.getInt(msInterval, 1000);
                            currentInterval = (interval / 100) - 6;
                            seekBar.setProgress(currentInterval);

                            seekBarTextView.setText(getActivity().getString(R.string.default_current) + ((currentInterval * 100) + 600));

                            doneButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SharedPreferences.Editor e = mSharedPreferences.edit();
                                    e.putInt(msInterval, (currentInterval * 100) + 600);
                                    e.commit();
                                    dialog.cancel();
                                }
                            });

                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                    currentInterval = i;
                                    seekBarTextView.setText(getActivity().getString(R.string.default_current) + ((currentInterval * 100) + 600));
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });

                            dialog.show();

                            return false;
                        }
                    });
        }
    }

}
