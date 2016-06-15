package com.example.campusmap.fragment;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.pathfinding.Drawing;
import com.example.campusmap.pathfinding.asynctask.PolygonLoader;
import com.example.campusmap.pathfinding.graphic.Polygon;

import java.util.ArrayList;


public class PathFindingFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Polygon>> {
    private static final String TAG = "ADP_PathFindingFragment";
    private static final boolean DEBUG = true;
    private static PathFindingFragment fragment = null;
    public static final int TAP_INDEX = 2;

    private Snackbar snackbar;
    ImageView imageView;
    FloatingActionButton fab;
    Drawing drawing;

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

        imageView = (ImageView) rootView.findViewById(R.id.path_imageview);
        drawing = new Drawing(getActivity(), imageView);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                snackbar.setText("경로를 표시합니다.");
//                snackbar.show();
//                drawing.drawPath();
                drawing.reDraw();
            }
        });
        snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT)
                .setAction("Action", null);

        Toast.makeText(getActivity(), "생성되었습니다.", Toast.LENGTH_SHORT).show();
        return rootView;
    }

    // ## Polygon Loader (( LoaderManager.LoaderCallbacks )) ##
    @Override
    public Loader<ArrayList<Polygon>> onCreateLoader(int id, Bundle args) {
        if (DEBUG) Log.i(TAG, "+++ onCreateLoader() called! +++");
        snackbar.setText("로딩중입니다...");
        snackbar.show();

        return new PolygonLoader(getActivity(), drawing.getMap());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Polygon>> loader, ArrayList<Polygon> data) {
        if (DEBUG) Log.i(TAG, "+++ onLoadFinished() called! +++");

        drawing.reDraw();

        snackbar.setText("로드를 완료했습니다.");
        snackbar.show();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Polygon>> loader) {
        Toast.makeText(getActivity(), "초기화합니다..", Toast.LENGTH_SHORT).show();
        drawing.getMap().resetPolygon();

        Log.i("PathFindingFragment", "onLoaderReset!!");
    }

}