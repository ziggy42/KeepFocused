package com.andreapivetta.keepfocused.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
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

        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(getSharedPreferences("MyPref", 0).getInt("BGAct", R.color.turquoise))));
        invalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        private Preference prefKeyRateApp, prefKeyShareApp, prefKeyRecord,
                prefKeySpeed, prefKeyRestore, prefKeyTheme;
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
            prefKeyRestore = findPreference("pref_key_restore");
            prefKeyTheme = findPreference("pref_key_theme");
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

                            seekBarTextView.setText(getActivity().getString(R.string.default_current) + String.format("%.1f", ((currentInterval / 10.0) + 0.6)) + "s");

                            doneButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mSharedPreferences.
                                            edit().
                                            putInt(msInterval, (currentInterval * 100) + 600)
                                            .apply();
                                    dialog.cancel();
                                }
                            });

                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                    currentInterval = i;
                                    seekBarTextView.setText(getActivity().getString(R.string.default_current) + String.format("%.1f", ((currentInterval / 10.0) + 0.6)) + "s");
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

            prefKeyRestore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.are_you_sure);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mSharedPreferences
                                    .edit()
                                    .putInt("Record", 0)
                                    .apply();
                            prefKeyRecord.setSummary("0");
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return false;
                }
            });

            prefKeyTheme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Dialog dialog = new Dialog(getActivity());
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.color_dialog, (ViewGroup) getActivity().findViewById(R.id.dialogColorLinearLayout));
                    dialog.setContentView(layout);

                    dialog.setTitle(R.string.chose_theme);

                    View turquoise_button = layout.findViewById(R.id.turquoise_button);
                    View emerald_button = layout.findViewById(R.id.emerald_button);
                    View peterRiver_button = layout.findViewById(R.id.peterRiver_button);
                    View amethyst_button = layout.findViewById(R.id.amethyst_button);
                    View sunFlower_button = layout.findViewById(R.id.sunFlower_button);
                    View carrot_button = layout.findViewById(R.id.carrot_button);
                    View alizarin_button = layout.findViewById(R.id.alizarin_button);
                    View clouds_button = layout.findViewById(R.id.clouds_button);

                    turquoise_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSharedPreferences
                                    .edit()
                                    .putInt("BG", R.color.turquoise)
                                    .putInt("BGAct", R.color.green_sea)
                                    .apply();
                            dialog.cancel();
                            updateColor();
                        }
                    });

                    emerald_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSharedPreferences
                                    .edit()
                                    .putInt("BG", R.color.emerald)
                                    .putInt("BGAct", R.color.nephritis)
                                    .apply();
                            dialog.cancel();
                            updateColor();
                        }
                    });

                    peterRiver_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSharedPreferences
                                    .edit()
                                    .putInt("BG", R.color.peter_river)
                                    .putInt("BGAct", R.color.belize_hole)
                                    .apply();
                            dialog.cancel();
                            updateColor();
                        }
                    });

                    amethyst_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSharedPreferences
                                    .edit()
                                    .putInt("BG", R.color.amethyst)
                                    .putInt("BGAct", R.color.wisteria)
                                    .apply();
                            dialog.cancel();
                            updateColor();
                        }
                    });


                    sunFlower_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSharedPreferences
                                    .edit()
                                    .putInt("BG", R.color.sun_flower)
                                    .putInt("BGAct", R.color.orange)
                                    .apply();
                            dialog.cancel();
                            updateColor();
                        }
                    });

                    carrot_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSharedPreferences
                                    .edit()
                                    .putInt("BG", R.color.carrot)
                                    .putInt("BGAct", R.color.pumpkin)
                                    .apply();
                            dialog.cancel();
                            updateColor();
                        }
                    });

                    alizarin_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSharedPreferences
                                    .edit()
                                    .putInt("BG", R.color.alizarin)
                                    .putInt("BGAct", R.color.pomegranate)
                                    .apply();
                            dialog.cancel();
                            updateColor();
                        }
                    });

                    clouds_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSharedPreferences
                                    .edit()
                                    .putInt("BG", R.color.clouds)
                                    .putInt("BGAct", R.color.silver)
                                    .apply();
                            dialog.cancel();
                            updateColor();
                        }
                    });

                    dialog.show();

                    return false;
                }
            });
        }

        void updateColor() {
            getActivity().
                    getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(mSharedPreferences.getInt("BGAct", R.color.turquoise))));
            getActivity().invalidateOptionsMenu();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(getSharedPreferences("MyPref", 0).getInt("BGAct", R.color.turquoise))));
        invalidateOptionsMenu();
    }

}
