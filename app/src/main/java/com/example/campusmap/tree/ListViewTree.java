package com.example.campusmap.tree;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.campusmap.R;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.tree.branch.Parent;
import com.example.campusmap.xmlparser.BuildingInfoParser;

import java.util.ArrayList;
import java.util.LinkedList;

public class ListViewTree {
    private static final int SIMPLE_LIST_ITEM = android.R.layout.simple_list_item_1;
    private Context context;
    final private ArrayList<ListView> listViews;
    final BuildingInfoParser parser;
    Animation appear;
    Animation darkking;
    private final int showColor;
    private final int hideColor;
    int buildingClickedIndex;
    int index = 0;


    /**
     * ..생성자..
     * 데이터를 초기화한다.
     * @param context : 리스트뷰를 출력할 Context
     */
    public ListViewTree(Context context) {
        this.context = context;
        listViews = new ArrayList<>();
        parser = BuildingInfoParser.getInstance(context.getResources().getXml(R.xml.building_info));
        appear = null;
        darkking = null;

        showColor = ContextCompat.getColor(context, R.color.white);
        hideColor = ContextCompat.getColor(context, R.color.gray_white);
    }

    /**
     * 애니메이션을 지정한다.
     * @param appear : 리스트뷰가 나타나는 애니메이션
     * @param darkking : 리스트뷰를 강조하는 SideView 애니메이션
     */
    public void setAnimation(Animation appear, Animation darkking) {
        this.appear = appear;
        this.darkking = darkking;
    }

    /**
     * 처음 보여지는 리스트뷰를 지정한다.
     * @param listView :
     */
    public void setRoot(@NonNull ListView listView) {
        try {
            if (listViews.size() > 0)
                throw  new Exception("루트를 지정하기 전에 생성된 가지(Branch)가 있습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            listViews.clear();
            Log.w("Exception", "listPartners를 비웠습니다..");
        } finally {
            listView.setVisibility(View.INVISIBLE);
            listViews.add(listView);
        }
    }

    /**
     * 한 가지(ListView, TextView)을 추가한다.
     * @param listView : 보여지는 리스트뷰
     */
    public void addBranch(@NonNull ListView listView) {
        listView.setVisibility(View.INVISIBLE);
        listViews.add(listView);
    }

    /**
     * Branch 추가가 모두 완료되고 실행되어야 하는 함수이며,
     * 이벤트와 데이터를 로드한다.
     */
    public void complete() {
        // check animation
        if (appear==null || darkking==null)
            throw  new NullPointerException("appear Animation or darking Animation is null");
        //
        listViews.get(0).setVisibility(View.VISIBLE);

        // 리스트에 파서결과 넣기
        buildListView();

        // 이벤트 추가
        setEventListener();
    }

    /**
     * 이벤트리스너를 추가한다.
     */
    private void setEventListener() {
        final int lastIndex = listViews.size() - 1;
        for (int treeIndex=0; treeIndex<lastIndex; treeIndex++) {
            final int currentIndex = treeIndex;
            ListView currentListView = listViews.get(currentIndex);

            // 아이템클릭이벤트
            currentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    index = currentIndex+1;
                    if (currentIndex == 0) {
                        buildingClickedIndex = position;
                    }
                    // 현재 리스트뷰에 맞는 데이터 삽입.
                    pushBranchItem(currentIndex, position);
                    showChild(currentIndex);

                    // 해당 리스트뷰의 부모들을 어둡게 한다.
                    highlightListView(currentIndex);
                }
            });
            // 스크롤이벤트
            currentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    index = currentIndex;
                    // 스크롤된 다음 ListView들을 모두 숨긴다.
                    listViews.get(currentIndex).setBackgroundColor(showColor);
                    hideChildren(currentIndex + 1);

                    // 해당 리스트뷰의 부모들을 어둡게 한다.
                    highlightListView(currentIndex - 1);
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    // 스크롤이 되면 해야겠지만, 한화면에 다 차지않는 리스트는 스크롤이 되지않기에
                    // 상태가 변하게 될때로 변경이 되었다.
                }
            });
        }
    }

    private void buildListView() {
        ArrayList<Parent> list = new ArrayList<Parent>(parser.toBuildingList());
        ArrayAdapter<Parent> arrayAdapter = new ArrayAdapter<>(
                context,
                SIMPLE_LIST_ITEM,
                list
        );
        this.listViews.get(0).setAdapter(arrayAdapter);
    }

    /**
     * 클릭된 리스트뷰의 자식의 레이아웃이 나타난다.
     * @param index : 현재 listViews 의 위치
     */
    private void showChild(int index) {
        int lastIndex = listViews.size() - 1;

        // 모든 자식을 숨긴다.
        hideChildren(index + 1);

        if (index < lastIndex) {
            // 리스트뷰를 나타나게 한다.
            ListView currentListView = listViews.get(index+ 1);
            currentListView.setVisibility(View.VISIBLE);
            currentListView.startAnimation(appear);
            currentListView.setBackgroundColor(showColor);
        }
    }

    private void highlightListView(int index) {
        for (int i=0; index-i>=0; i++) {
            float rate = (float)Math.pow(0.7, (i)+1);
            listViews.get(index - i).setBackgroundColor(darker(hideColor, rate));
        }
    }

    /**
     * 리스트뷰의 자식들을 숨긴다.
     * @param index : 현재 listpartners 의 위치
     */
    private void hideChildren(int index) {
        for (int i=index; i< listViews.size(); i++) {
            ListView currentListView = listViews.get(i);
            currentListView.clearAnimation();
            currentListView.setVisibility(View.INVISIBLE);
        }
    }

    private void pushBranchItem(int deeps, int position) {
        switch (deeps) {
            case 0:
                listViews.get(1).setAdapter(new ArrayAdapter<Parent>(
                        context,
                        SIMPLE_LIST_ITEM,
                        new ArrayList<Parent>(parser.toFloorList(position))
                ));
                break;
            case 1:
                listViews.get(2).setAdapter(new ArrayAdapter<Parent>(
                        context,
                        SIMPLE_LIST_ITEM,
                        new ArrayList<Parent>(parser.toRoomList(buildingClickedIndex, position))
                ));
                break;
        }
    }


    public static int darker (int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor; // value component
        return Color.HSVToColor(hsv);
    }

    public int getIndex() {
        return index;
    }


    public void hideLastListView() {
        //Log.i("Index", "current index : " + index);
        listViews.get(index).setVisibility(View.INVISIBLE);
        listViews.get(index-1).setBackgroundColor(showColor);
        highlightListView(index - 2);
        index--;
    }
}
