package com.example.campusmap.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.activity.NMTestActivity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class PathFindingFragment extends Fragment { //  implements LoaderManager.LoaderCallbacks<MapManager>
    private static final String TAG = "PathFindingFragment";
    private static final boolean DEBUG = false;
    public static final int TAP_INDEX = 2;

    private Toast toast;
    private ImageView mImageView;
//    private MapManager mMapManager;
    private ProgressBar mProgressBar;

    private AsyncTask pathFindAsyncTask;

    private boolean triggerLongPress = false;

    public static PathFindingFragment newInstance() {
        return new PathFindingFragment();
    }

    public PathFindingFragment() {
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        getLoaderManager().initLoader(
//                0,    // Loader 를 구분하기 위한 ID 값
//                null, // 추가 인자
//                this  // Loader 로 부터 콜백을 받기 위해서 LoaderManager.LoaderCallbacks 를 구현한 객체를 넘겨준다.
//        );
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "+++ onCreateView() called! +++");

        toast = Toast.makeText(getContext(), "토스트", Toast.LENGTH_SHORT);

        View rootView = inflater.inflate(R.layout.fragment_path_finding, container, false);

        mImageView = (ImageView) rootView.findViewById(R.id.path_imageview);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        mProgressBar.setIndeterminate(true);

        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: detected");
                if (triggerLongPress) {
                    // open
                    Intent intent = new Intent(
                            getContext(), NMTestActivity.class
                    );
                    startActivity(intent);
                } else {
//                    if (mMapManager != null) {
//                        runPathFindingAsyncTask();
//                    } else {
//                        toast.setText("로딩중입니다\n잠시 후 다시시도해주세요");
//                        toast.show();
//                    }
                }
            }
        });
        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                Log.i(TAG, "onLongPress: detected");
                triggerLongPress = true;
                fab.setImageResource(R.drawable.ic_zoom_out_map_white);
            }
        });
        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i(TAG, "onTouch: ACTION_UP detected");
                    fab.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            triggerLongPress = false;
                        }
                    }, 100);
//                    triggerLongPress = false;
                    fab.setImageResource(R.drawable.ic_navigation_white);
                }
//                Log.i(TAG, "onTouch: detected");
                return gestureDetector.onTouchEvent(event);
            }
        });

        // for test
//        TreeSet<Tile>

        mProgressBar.setIndeterminate(false);
        mProgressBar.setVisibility(View.GONE);

        return rootView;
    }

    private void runPathFindingAsyncTask() {
        if (pathFindAsyncTask == null) {
            toast.setText("경로를 검색합니다.");
            toast.show();

            pathFindAsyncTask = new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... params) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

//                    mMapManager.resetPath();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
//                    mMapManager.drawOnImageView(mImageView);
                    pathFindAsyncTask = null;
                }
            }.execute();
            new AsyncTask<Void,Void,Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    if (pathFindAsyncTask != null)
                        try {
                            pathFindAsyncTask.get(2L, TimeUnit.SECONDS);
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            return null;
                        }
                    return null;
                }
            }.execute();
        } else {
            toast.setText("경로를 검색중입니다");
            toast.show();
        }
    }

//    // ## PolygonD Loader (( LoaderManager.LoaderCallbacks )) ##
//    @Override
//    public Loader<MapManager> onCreateLoader(int id, Bundle args) {
//        if (DEBUG) Log.i(TAG, "+++ onCreateLoader() called! +++");
//        toast.setText("로딩중입니다...");
//        toast.show();
//
//        return new DrawingLoader(getActivity(), mImageView);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<MapManager> loader, MapManager drawing) {
//        if (DEBUG) Log.i(TAG, "+++ onLoadFinished() called! +++");
//
//        mMapManager = drawing;
//        drawing.resetPath();
//        drawing.drawOnImageView(mImageView);
//
//        mProgressBar.setIndeterminate(false);
//        mProgressBar.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<MapManager> loader) {
//        if (DEBUG) Log.i(TAG, "+++ onLoaderReset() called! +++");
//        mMapManager.getMap().resetPolygon();
//
//        Log.i("PathFindingFragment", "onLoaderReset!!");
//    }

}