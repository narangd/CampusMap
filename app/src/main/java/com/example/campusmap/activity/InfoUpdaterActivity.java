package com.example.campusmap.activity;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.campusmap.Internet;
import com.example.campusmap.R;
import com.example.campusmap.adapter.UpdaterAdapter;
import com.example.campusmap.data.branch.Building;
import com.example.campusmap.data.branch.Floor;
import com.example.campusmap.data.branch.Room;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.form.InfoLocation;
import com.example.campusmap.form.Updater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfoUpdaterActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String KEY_INFO_LOCATION = "InfoLocation";

    private AsyncTask mAsyncTask = null;
    private ProgressDialog mProgressDialog;

    private InfoLocation mInfoLocation;
    private List<Updater> mUpdaterList;
    private int mID;

    private EditText mTitle;
    private EditText mContents;
    private TextView mSubTitleTextView;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Internet.isConnect(this)) {
            Toast.makeText(this, "인터넷에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_updater);

        mProgressDialog = new ProgressDialog(InfoUpdaterActivity.this);
        mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.list_view);
        ViewGroup listViewHeader = (ViewGroup) getLayoutInflater().inflate(R.layout.content_info_updater, mListView, false);
        mListView.addHeaderView(listViewHeader, null, false);
        mListView.setOnItemClickListener(this);

        mSubTitleTextView = (TextView) listViewHeader.findViewById(R.id.sub_title);
        mTitle = (EditText) listViewHeader.findViewById(R.id.title);
        mContents = (EditText) listViewHeader.findViewById(R.id.contents);

        Button button = (Button) listViewHeader.findViewById(R.id.email_sign_in_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmit();
            }
        });

        getInfoFromParameter();
        resetInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mProgressDialog.dismiss();

        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
    }

    private void resetInfo() {
        mTitle.setText("");
        mContents.setText("");

        loadingUpdaterList();
    }

    private void getInfoFromParameter() {
        mInfoLocation = (InfoLocation) getIntent().getSerializableExtra(KEY_INFO_LOCATION);
        Log.i("InfoUpdaterActivity", "getInfoFromParameter: info location : " + mInfoLocation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mInfoLocation.toString());
        }

        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(this);

        // Set SubTitle
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

    private void loadingUpdaterList() {
        mAsyncTask = new AsyncTask<Void,Void,List<Updater>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog.setTitle("목록을 불러오는 중입니다");
                mProgressDialog.show();
            }

            @Override
            protected List<Updater> doInBackground(Void... params) {
                ArrayList<Updater> updaters = new ArrayList<>();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(InfoUpdaterActivity.this);
                String user_id = preferences.getString(getString(R.string.pref_key_app_id), "");

                HashMap<String,String> dataMap = new HashMap<>();
                dataMap.put("tag", mInfoLocation.getTag());
                dataMap.put("userid", user_id);
                dataMap.put("id", String.valueOf(mID));

                String json;
                try {
                    json = Internet.connectHttpPage(
                            "http://203.232.193.178/campusmap/updater_list.php",
                            Internet.CONNECTION_METHOD_GET,
                            dataMap
                    );
                } catch (SocketTimeoutException e) {
                    json = "";
                }

                try {
                    JSONArray updaterArray = new JSONArray(json);
                    for (int i=0; i<updaterArray.length(); i++) {
                        JSONObject updaterObject = updaterArray.getJSONObject(i);

                        int id = updaterObject.getInt("id");
                        String title = updaterObject.getString("title");
                        String contents = updaterObject.getString("contents");
                        int votes = updaterObject.getInt("votes");
                        boolean voted = updaterObject.getBoolean("voted");

                        updaters.add( new Updater(id, title, contents, votes, voted) );
                    }
                } catch (JSONException e) {
                    Log.e("InfoUpdaterActivity", "doInBackground: JSONException(" + e.getMessage() + "), \"" + json + "\"");
                }

                return updaters;
            }

            @Override
            protected void onPostExecute(List<Updater> updaterList) {
                super.onPostExecute(updaterList);
                mAsyncTask = null;
                mProgressDialog.hide();
                mUpdaterList = updaterList;

                mListView.setAdapter(new UpdaterAdapter(
                        InfoUpdaterActivity.this,
                        updaterList
                ));
//                mListView.setAdapter(new ArrayAdapter<>(
//                        InfoUpdaterActivity.this,
//                        android.R.layout.simple_list_item_1,
//                        new String[]{"1","2","3","4"}
//                ));
            }
        }.execute();
    }

    private void attemptSubmit() {
        if (mAsyncTask != null) {
            return;
        }

        // Reset errors.
        mTitle.setError(null);
        mContents.setError(null);

        // Hide Keyboard
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Store values at the time of the login attempt.
        String title = mTitle.getText().toString();
        String contents = mContents.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid contents, if the user entered one.
        if (TextUtils.isEmpty(contents) && !isContentsValid(contents)) {
            mContents.setError(getString(R.string.error_invalid_password));
            focusView = mContents;
            cancel = true;
        }

        // Check for a valid title address.
        if (TextUtils.isEmpty(title) && !isTitleValid(title)) {
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
            sendUpdater(title, contents);
        }
    }

    private void sendUpdater(final String title, final String contents) {
        mAsyncTask = new AsyncTask<Void,Void,String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog.setTitle("업데이터를 등록중입니다");
                mProgressDialog.show();
            }

            @Override
            protected String doInBackground(Void[] params) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(InfoUpdaterActivity.this);

                String uniqueID = preferences.getString(getString(R.string.pref_key_app_id), "");

                HashMap<String,String> dataMap = new HashMap<>();
                dataMap.put("title", title);
                dataMap.put("contents", contents);
                dataMap.put("userid", uniqueID);
                dataMap.put("tag", mInfoLocation.getTag());
                dataMap.put("id", String.valueOf(mID));

                try {
                    return Internet.connectHttpPage(
                            "http://203.232.193.178/campusmap/updater.php",
                            Internet.CONNECTION_METHOD_POST,
                            dataMap
                    );
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                mAsyncTask = null;
                mProgressDialog.hide();

                String message;
                if (result.equals("true")) {
                    message = "정상적으로 처리되었습니다";
                } else {
                    message = "실패하였습니다";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(InfoUpdaterActivity.this);
                builder.setTitle("서버로 보낸 결과");
                builder.setMessage(message);
                builder.show();

                resetInfo();

                // 빠른 반복 전송을 막기위함.
                Internet.delay500ms();
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                mAsyncTask = null;
                mProgressDialog.hide();
            }
        }.execute();
    }

    private boolean isTitleValid(String title) {
        return title.length() > 4;
    }

    private boolean isContentsValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Updater updater = mUpdaterList.get(position-1); // Header reason

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(updater.getTitle());
        builder.setMessage(updater.getContents());
        builder.setPositiveButton("닫기", null);
        builder.show();
    }
}

