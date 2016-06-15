package com.example.campusmap.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
    public static final int TAP_INDEX = 2;
    private static PathFindingFragment fragment = null;
    private Snackbar snackbar;
    ImageView imageView;
    FloatingActionButton fab;
    Context context;
    Drawing drawing;

    Loader loader;

    public static PathFindingFragment newInstance() {
        if (fragment == null) {
            fragment = new PathFindingFragment();
        }
        return fragment;
    }

    public PathFindingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_path_finding, container, false);
        context = rootView.getContext();

        imageView = (ImageView) rootView.findViewById(R.id.path_imageview);
        drawing = new Drawing(context, imageView);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.setText("경로를 표시합니다.");
                snackbar.show();
                drawing.drawPath();
            }
        });
        snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT)
                .setAction("Action", null);

//        rootView..initLoader(1, null, this).forceLoad();
        if (loader == null) {
            loader = getActivity().getSupportLoaderManager().initLoader(1, null, this);
            loader.forceLoad();
        } else {
            loader.cancelLoad();
            loader.forceLoad();
        }

        Toast.makeText(context, "생성되었습니다.", Toast.LENGTH_SHORT).show();
        return rootView;
    }

    @Override
    public Loader<ArrayList<Polygon>> onCreateLoader(int id, Bundle args) {
        snackbar.setText("로딩중입니다...");
        snackbar.show();
        return new PolygonLoader(context, drawing.getMap());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Polygon>> loader, ArrayList<Polygon> data) {
        drawing.reDraw();
        snackbar.setText("로드를 완료했습니다.");
        snackbar.show();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Polygon>> loader) {
        Toast.makeText(context, "초기화합니다..", Toast.LENGTH_SHORT).show();
        drawing.getMap().resetPolygon();
    }
}