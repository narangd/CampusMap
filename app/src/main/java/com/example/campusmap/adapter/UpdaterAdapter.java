package com.example.campusmap.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.campusmap.Internet;
import com.example.campusmap.R;
import com.example.campusmap.form.Updater;

import java.util.HashMap;
import java.util.List;

public class UpdaterAdapter extends ArrayAdapter<Updater> {

    private static final String TAG = "UpdaterAdapter";
    private List<Updater> mUpdaterList;

    public UpdaterAdapter(Context context, List<Updater> updaterList) {
        super(context, R.layout.list_item_with_number, updaterList);

        mUpdaterList = updaterList;
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(
                    R.layout.list_item_with_number, null);
        }
        final View rootView = view;

        final Updater currentUpdater = mUpdaterList.get(position);

        final ImageView favorite = (ImageView) rootView.findViewById(R.id.favorite);
        afterVote(favorite, currentUpdater.isVoted());

        TextView title = (TextView) rootView.findViewById(R.id.title);
        title.setText( currentUpdater.getTitle() );

        final TextView number = (TextView) rootView.findViewById(R.id.number);
        number.setText( String.valueOf(currentUpdater.getVote()) );

        rootView.findViewById(R.id.vote).setOnClickListener(new View.OnClickListener() {
            AsyncTask asyncTask;
            @Override
            public void onClick(View v) {
                currentUpdater.getID();
                if (asyncTask != null) {
                    return;
                }
                asyncTask = new AsyncTask<Void,Void,Boolean>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        rootView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fadeout));
                        rootView.setAlpha(0.5f);
                    }

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        String userid = preferences.getString(getContext().getString(R.string.pref_key_app_id), "");

                        HashMap<String,String> data = new HashMap<>();
                        data.put("updater_id", String.valueOf(currentUpdater.getID()) );
                        data.put("userid", userid);
                        data.put("vote", currentUpdater.isVoted() ? "1" : "0"); // true : 사용자가 이미 공감한 상태

                        String result = Internet.connectHttpPage(
                                "http://203.232.193.178/campusmap/updater_vote.php",
                                Internet.CONNECTION_METHOD_POST,
                                data
                        );

//                        Log.i(TAG, "doInBackground: result : " + result);
                        if (result != null && result.equals("true")) {
                            currentUpdater.setVoted(!currentUpdater.isVoted());
                        }

                        // 빠른 반복 전송을 막기위함.
                        Internet.delay500ms();

                        return currentUpdater.isVoted();
                    }

                    @Override
                    protected void onPostExecute(Boolean voted) {
                        super.onPostExecute(voted);

                        afterVote(favorite, voted);
                        number.setText( String.valueOf(currentUpdater.getVote()) );

//                        Log.i(TAG, "onPostExecute: result(boolean) : " + voted);
                        rootView.clearAnimation();
                        rootView.post(new Runnable() {
                            @Override
                            public void run() {
                                rootView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fadein));
                                rootView.setAlpha(1f);
                            }
                        });

                        asyncTask = null;
                    }
                }.execute();
            }
        });

//        Button vote = (Button) rootView.findViewById(R.id.vote);
//
//        Drawable drawable = ActivityCompat.getDrawable(getContext(), R.drawable.ic_favorite_white);
//        drawable.setBounds(0, 0, 30, 30);
//        drawable.setColorFilter(ActivityCompat.getColor(getContext(), R.color.Red500), PorterDuff.Mode.MULTIPLY);
//        vote.setCompoundDrawables(drawable, null, null, null);


        return rootView;
    }

    private void afterVote(ImageView favorite, boolean vote) {
        if (vote) {
            favorite.setColorFilter(ActivityCompat.getColor(getContext(), R.color.Red500));
        } else {
            favorite.setColorFilter(ActivityCompat.getColor(getContext(), R.color.Gray400));
        }
    }
}
