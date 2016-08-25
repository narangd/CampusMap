package com.example.campusmap.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.fragment.MenuPlannerFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Fragment fragment_v4 = null; // for this


        GeneralPreferenceFragment fragment = (GeneralPreferenceFragment) getFragmentManager().findFragmentById(R.id.fragment);

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

                        SQLiteHelperCampusInfo sqLiteHelperCampusInfo = SQLiteHelperCampusInfo.getInstance(context);
                        SQLiteDatabase db = sqLiteHelperCampusInfo.getWritableDatabase();

                        sqLiteHelperCampusInfo.deleteTable(db, SQLiteHelperCampusInfo.BuildingEntry.TABLE_NAME);
                        sqLiteHelperCampusInfo.deleteTable(db, SQLiteHelperCampusInfo.FloorEntry.TABLE_NAME);
                        sqLiteHelperCampusInfo.deleteTable(db, SQLiteHelperCampusInfo.RoomEntry.TABLE_NAME);

                        Log.i("SettingActivity", "onClick: Delete Data (building, floor, room)");


                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = preferences.edit();
                        String db_version_key = getString(R.string.pref_key_db_version);
                        editor.putInt(db_version_key, 0);
                        editor.apply();

                        Log.i("SettingActivity", "onClick: version : " + preferences.getInt(db_version_key, -1));

                        finish();
                        startActivity(new Intent(context, IntroActivity.class));
                    }
                });
                builder.setNegativeButton("최소", null);
                builder.show();
                return false;
            }
        });
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
            String DBVersionKey = getString(R.string.pref_key_db_version);
            String TodayKey = getString(R.string.pref_key_today_menu_planner);
            final String DateKey = getString(R.string.pref_key_last_skip_date);

            setPreferenceValue(AppVersionKey, "0.0.0", false);
            setPreferenceValue(DBVersionKey, 0, false);

            Log.i("SettingsActivity", "onCreate: today_menu_planer : " + sharedPreferences.getBoolean(TodayKey, false) );
            Log.i("SettingsActivity", "onCreate: today_menu_planer : " + sharedPreferences.getBoolean(TodayKey, true) );

            final Preference today = findPreference(TodayKey);
            today.setSummary( sharedPreferences.getString(DateKey, "-") );
            today.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {
                        boolean today_menu = (boolean) newValue;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (today_menu) {
                            editor.putString(DateKey, "-");
                        } else {
                            editor.putString(DateKey, MenuPlannerFragment.TODAY_DATE);
                        }
                        editor.apply();
                        today.setSummary(sharedPreferences.getString(DateKey, "-"));
                        return true;
                    }
                    return false;
                }
            });
        }

        private void setPreferenceValue(String key, String defaultValue, boolean isBind) {
            Preference preference = findPreference(key);
//            if (isBind) {
//                bindPreferenceSummaryToValue(preference);
//            }
            preference.setSummary(sharedPreferences.getString(key, defaultValue));
        }

        private void setPreferenceValue(String key, int defaultValue, boolean isBind) {
            Preference preference = findPreference(key);
//            if (isBind) {
//                bindPreferenceSummaryToValue(preference);
//            }
            preference.setSummary( String.valueOf(sharedPreferences.getInt(key, defaultValue)) );
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

//
//    private static boolean isXLargeTablet(Context context) {
//        return (context.getResources().getConfiguration().screenLayout
//                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
//    }
//
//    private static void bindPreferenceSummaryToValue(Preference preference) {
//        // Set the listener to watch for value changes.
//        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
//
//        // Trigger the listener immediately with the preference's
//        // current value.
//        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
//                PreferenceManager
//                        .getDefaultSharedPreferences(preference.getContext())
//                        .getString(preference.getKey(), ""));
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_settings);
////        getFragmentManager().beginTransaction().replace(android.R.layout.).commit();
////        setupActionBar();
//    }
//
//    private void setupActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            // Show the Up button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }

//    @Override
//    public boolean onIsMultiPane() {
//        return isXLargeTablet(this);
//    }

//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.pref_headers, target);
//    }

//    protected boolean isValidFragment(String fragmentName) {
//        return PreferenceFragment.class.getName().equals(fragmentName)
//                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
//                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
//                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
//    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class GeneralPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_general);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
////            bindPreferenceSummaryToValue(findPreference("example_text"));
////            bindPreferenceSummaryToValue(findPreference("example_list"));
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    /**
//     * This fragment shows notification preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class NotificationPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_notification);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    /**
//     * This fragment shows data and sync preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class DataSyncPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_data_sync);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }
}
