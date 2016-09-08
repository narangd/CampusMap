package com.example.campusmap.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.asynctask.loader.DrawingLoader;
import com.example.campusmap.pathfinding.Drawing;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class PathFindingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Drawing> {
    private static final String TAG = "PathFindingFragment";
    private static final boolean DEBUG = false;
    public static final int TAP_INDEX = 2;

    private Toast toast;
    private ImageView mImageView;
    private Drawing mDrawing;
    private ProgressBar mProgressBar;

    private AsyncTask pathFindAsyncTask;

    public static PathFindingFragment newInstance() {
        return new PathFindingFragment();
    }

    public PathFindingFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(
                0,    // Loader 를 구분하기 위한 ID 값
                null, // 추가 인자
                this  // Loader 로 부터 콜백을 받기 위해서 LoaderManager.LoaderCallbacks 를 구현한 객체를 넘겨준다.
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "+++ onCreateView() called! +++");

        View rootView = inflater.inflate(R.layout.fragment_path_finding, container, false);

        mImageView = (ImageView) rootView.findViewById(R.id.path_imageview);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setIndeterminate(true);
        toast = Toast.makeText(getContext(), "토스트", Toast.LENGTH_SHORT);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawing != null) {

                    runPathFindingAsyncTask();

                } else {
                    toast.setText("로딩중입니다\n잠시 후 다시시도해주세요");
                    toast.show();
                }
            }
        });

        // for test
//        TreeSet<Tile>

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

                    mDrawing.resetPath();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mDrawing.drawOnImageView(mImageView);
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

    // ## Polygon Loader (( LoaderManager.LoaderCallbacks )) ##
    @Override
    public Loader<Drawing> onCreateLoader(int id, Bundle args) {
        if (DEBUG) Log.i(TAG, "+++ onCreateLoader() called! +++");
        toast.setText("로딩중입니다...");
        toast.show();

        return new DrawingLoader(getActivity(), mImageView);
    }

    @Override
    public void onLoadFinished(Loader<Drawing> loader, Drawing drawing) {
        if (DEBUG) Log.i(TAG, "+++ onLoadFinished() called! +++");

        mDrawing = drawing;
        drawing.resetPath();
        drawing.drawOnImageView(mImageView);

        mProgressBar.setIndeterminate(false);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Drawing> loader) {
        if (DEBUG) Log.i(TAG, "+++ onLoaderReset() called! +++");
        mDrawing.getMap().resetPolygon();

        Log.i("PathFindingFragment", "onLoaderReset!!");
    }

}