package com.example.campusmap.activity;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.database.SQLiteHelperCampusInfo;

public class SettingsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_CODE_READ_SMS = 100;
    private GeneralPreferenceFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment_v4 = null; // for this


        fragment = (GeneralPreferenceFragment) getFragmentManager().findFragmentById(R.id.fragment);

        Preference button = fragment.findPreference(getString(R.string.pref_key_db_reset));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                final Context context = SettingsActivity.this;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("데이터베이스 초기화");
                builder.setMessage("정말 데이터베이스를 초기화 하시겠습니까?");
                builder.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SettingsActivity.this, "데이터베이스를 초기화하기위해 다시시작합니다", Toast.LENGTH_SHORT).show();

                        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(context);
                        SQLiteDatabase db = helper.getWritableDatabase();

                        helper.delete(db);

                        db.close();

                        Log.i("SettingActivity", "onClick: Delete Data (building, floor, room)");


                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = preferences.edit();
                        String db_version_key = getString(R.string.pref_key_db_version);
                        editor.putInt(db_version_key, 0);
                        editor.apply();

                        Log.i("SettingActivity", "onClick: version : " + preferences.getInt(db_version_key, -1));
                        startActivity(new Intent(context, IntroActivity.class));
                        finish();
                    }
                });
                builder.setNegativeButton("최소", null);
                builder.show();
                return false;
            }
        });
        Preference download_button = fragment.findPreference(getString(R.string.pref_key_download_recent_app));
        download_button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://203.232.193.178/download/android/newest.php"));
                startActivity(browserIntent);
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_READ_SMS) {
            if (grantResults.length >= 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // success
                fragment.bringPhoneNumber();
            }
        }
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        private SharedPreferences sharedPreferences = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            bringFromSharedPreference();

        }

        private void bringFromSharedPreference() {
            String AppVersionKey = getString(R.string.pref_key_app_version);
            String AppIDKey = getString(R.string.pref_key_app_id);
            String DBVersionKey = getString(R.string.pref_key_db_version);
//            final String TodayKey = getString(R.string.pref_key_download_recent_app);
//            final String DateKey = getString(R.string.pref_key_last_skip_date);

            setPreferenceValue(AppVersionKey, "0.0.0");
            setPreferenceValue(AppIDKey, "abcd");
            setPreferenceValue(DBVersionKey, 0);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_SMS},
                        REQUEST_CODE_READ_SMS
                );
            } else {
                bringPhoneNumber();
            }

//            final Preference today = findPreference(TodayKey);
//            today.setSummary( sharedPreferences.getString(DateKey, "-") );
//            today.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    if (newValue instanceof Boolean) {
//                        boolean today_menu = (boolean) newValue;
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        if (today_menu) {
//                            editor.putString(DateKey, "-");
//                        } else {
//                            editor.putString(DateKey, MenuPlannerFragment.TODAY_DATE);
//                        }
//                        editor.apply();
//                        today.setSummary(sharedPreferences.getString(DateKey, "-"));
//                        return true;
//                    }
//                    return false;
//                }
//            });
        }

        private void setPreferenceValue(String key, String defaultValue) {
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getString(key, defaultValue));
        }

        private void setPreferenceValue(String key, int defaultValue) {
            Preference preference = findPreference(key);
            preference.setSummary( String.valueOf(sharedPreferences.getInt(key, defaultValue)) );
        }

        private void bringPhoneNumber() {
            TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            Preference phoneNumberPreference = findPreference(getString(R.string.pref_key_phone_number));
            phoneNumberPreference.setSummary(tMgr.getLine1Number());
        }
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
