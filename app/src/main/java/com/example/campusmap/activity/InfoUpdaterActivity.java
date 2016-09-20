package com.example.campusmap.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.database.InfoLocation;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.tree.branch.Floor;
import com.example.campusmap.tree.branch.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class InfoUpdaterActivity extends AppCompatActivity {

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    public static final String KEY_INFO_LOCATION = "InfoLocation";
    private UpdaterTask mAuthTask = null;
    private InfoLocation mInfoLocation;

    // UI references.
    private EditText mTitle;
    private EditText mContents;
    private TextView mSubTitleTextView;
    private ListView mListView;
    private Button mButton;
    private int mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_updater);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.list_view);
        ViewGroup listViewHeader = (ViewGroup) getLayoutInflater().inflate(R.layout.content_info_updater, mListView, false);
        if (mListView != null) {
            mListView.addHeaderView(listViewHeader, null, false);
            mListView.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    new String[]{"가","가","가","가","가","가","가","가","가","가","가","가","가","가","가"}
            ));
        }
        mSubTitleTextView = (TextView) listViewHeader.findViewById(R.id.sub_title);
        mTitle = (EditText) listViewHeader.findViewById(R.id.title);
        mContents = (EditText) listViewHeader.findViewById(R.id.contents);
        mButton = (Button) listViewHeader.findViewById(R.id.email_sign_in_button);

        getInfoFromParameter();

//        // Set up the login form.
//        ArrayList<String> list = new ArrayList<>();
//        for (String email : DUMMY_CREDENTIALS) {
//            list.add(email.split(":")[0]);
//        }
//        addEmailsToAutoComplete(list);

//        mContents.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == EditorInfo.IME_NULL) {
//                    attemptSubmit();
//                    return true;
//                }
//                return false;
//            }
//        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmit();
            }
        });
    }

    private void resetInfo() {
        mTitle.setText("");
        mContents.setText("");


    }

    private void getInfoFromParameter() {
        mInfoLocation = (InfoLocation) getIntent().getSerializableExtra(KEY_INFO_LOCATION);
        Log.i("InfoUpdaterActivity", "getInfoFromParameter: info location : " + mInfoLocation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mInfoLocation.toString());
        }

        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(this);

        mSubTitleTextView = (TextView) findViewById(R.id.sub_title);
        String path = "";
        if (mInfoLocation.getBuildingID() != InfoLocation.NONE) {
            Building building = helper.getBuildingDetail(mInfoLocation.getBuildingID());
            path += building.getName();
            mID = mInfoLocation.getBuildingID();
        }
        if (mInfoLocation.getFloorID() != InfoLocation.NONE) {
            Floor floor = helper.getFloorDetail(mInfoLocation.getFloorID());
            path += " / " + floor.toString();
            mID = mInfoLocation.getFloorID();
        }
        if (mInfoLocation.getRoomID() != InfoLocation.NONE) {
            Room room = helper.getRoomDetail(mInfoLocation.getRoomID());
            path += " / " + room.getName();
            mID = mInfoLocation.getRoomID();
        }
        mSubTitleTextView.setText(path);
    }

    private void attemptSubmit() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mTitle.setError(null);
        mContents.setError(null);

        // Store values at the time of the login attempt.
        String title = mTitle.getText().toString();
        String contents = mContents.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid contents, if the user entered one.
        if (!TextUtils.isEmpty(contents) && !isContentsValid(contents)) {
            mContents.setError(getString(R.string.error_invalid_password));
            focusView = mContents;
            cancel = true;
        }

        // Check for a valid title address.
        if (!TextUtils.isEmpty(title) && !isTitleValid(title)) {
            mTitle.setError(getString(R.string.error_invalid_email));
            focusView = mTitle;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
            mAuthTask = new UpdaterTask(title, contents);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isTitleValid(String title) {
        return title.length() > 4;
    }

    private boolean isContentsValid(String password) {
        return password.length() > 4;
    }

    public class UpdaterTask extends AsyncTask<Void, Void, String> {
        private static final String URL_UPDATER_PAGE = "http://203.232.193.178/campusmap/updater.php";
        private static final int TIMEOUT = 1000 * 3;

        private final String mTitle;
        private final String mContents;
        private ProgressDialog mProgressDialog;

        UpdaterTask(String title, String contents) {
            mTitle = title;
            mContents = contents;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(InfoUpdaterActivity.this);
            mProgressDialog.setTitle("업데이터를 등록중입니다");
            mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(InfoUpdaterActivity.this);

            final String uniqueID = preferences.getString(getString(R.string.pref_key_app_id), "");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(URL_UPDATER_PAGE);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout( TIMEOUT );
                connection.setConnectTimeout( TIMEOUT );
                connection.setDoInput(true);
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(os);
                writer.write("title=" + mTitle);
                writer.write("&contents=" + mContents);
                writer.write("&userid=" + uniqueID);
                writer.write("&tag=" + mInfoLocation.getTag());
                writer.write("&id=" + mID);
                writer.close();

                StringBuilder builder = new StringBuilder();
                String line;
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader reader  = new BufferedReader(isr);

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                reader.close();

                return builder.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String message) {
            mAuthTask = null;
            mProgressDialog.dismiss();
//            showProgress(false);

//            if (success) {
//                finish();
//            } else {
//                InfoUpdaterActivity.this.mContents.setError(getString(R.string.error_incorrect_password));
//                InfoUpdaterActivity.this.mContents.requestFocus();
//            }

            AlertDialog.Builder builder = new AlertDialog.Builder(InfoUpdaterActivity.this);
            builder.setTitle("서버로 보낸 결과");
            builder.setMessage(message);
            builder.show();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            showProgress(false);
        }
    }
}

