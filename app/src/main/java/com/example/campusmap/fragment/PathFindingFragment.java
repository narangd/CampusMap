package com.example.campusmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.example.campusmap.pathfinding.Drawing;
import com.example.campusmap.asynctask.loader.DrawingLoader;

import java.util.Random;


public class PathFindingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Drawing> {
    private static final String TAG = "ADP_PathFindingFragment";
    private static final boolean DEBUG = true;
    private static PathFindingFragment fragment = null;
    public static final int TAP_INDEX = 2;

    private Snackbar mSnackbar;
    ImageView mImageView;
    FloatingActionButton fab;
    Drawing mDrawing;
    private ProgressBar mProgressBar;

    public static PathFindingFragment newInstance() {
        if (fragment == null) {
            fragment = new PathFindingFragment();
        }
        return fragment;
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

        final Random random = new Random();

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawing != null) {
                    mSnackbar.setText("경로를 표시합니다.");
                    mSnackbar.show();

                    mDrawing.resetPath();
                    mDrawing.drawImageView(mImageView);
                    mImageView.invalidate();
//                    mDrawing.reDraw();
//                    TreeSet<Tile> testingSet = new TreeSet<>();
//                    for (int i=1; i<=10; i++)  {
//                        int number = random.nextInt(100);
//                        Tile newTile = new Tile(0,0);
//                        newTile.F = number;
//                        testingSet.add(newTile);
//                        if (DEBUG) Log.i(TAG, "==testingSet== : insert number : " + number);
//                    }
//                    if (DEBUG) Log.i(TAG, "==testingSet== : " + testingSet.toString());
////                    while (!testingSet.isEmpty()) {
////                        int number = testingSet.pollFirst();
////                        if (DEBUG) Log.i(TAG, "==testingSet== : pollFirst result : " + number);
////                    }
                } else {
                    mSnackbar.setText("잠시 후 다시시도해주세요\n로딩중입니다.");
                    mSnackbar.show();
                }
            }
        });
        mSnackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT)
                .setAction("Action", null);

        return rootView;
    }

    // ## Polygon Loader (( LoaderManager.LoaderCallbacks )) ##
    @Override
    public Loader<Drawing> onCreateLoader(int id, Bundle args) {
        if (DEBUG) Log.i(TAG, "+++ onCreateLoader() called! +++");
        mSnackbar.setText("로딩중입니다...");
        mSnackbar.show();

        return new DrawingLoader(getActivity(), mImageView);
    }

    @Override
    public void onLoadFinished(Loader<Drawing> loader, Drawing drawing) {
        if (DEBUG) Log.i(TAG, "+++ onLoadFinished() called! +++");

        mDrawing = drawing;
        drawing.drawImageView(mImageView);
        mImageView.invalidate();

        mProgressBar.setIndeterminate(false);
        mProgressBar.setVisibility(View.GONE);


        mSnackbar.setText("로드를 완료했습니다.");
        mSnackbar.show();
    }

    @Override
    public void onLoaderReset(Loader<Drawing> loader) {
        Toast.makeText(getActivity(), "초기화합니다..", Toast.LENGTH_SHORT).show();
        mDrawing.getMap().resetPolygon();

        Log.i("PathFindingFragment", "onLoaderReset!!");
    }

}