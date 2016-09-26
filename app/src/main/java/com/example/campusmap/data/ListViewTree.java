package com.example.campusmap.data;

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
import com.example.campusmap.data.branch.Building;
import com.example.campusmap.data.branch.Floor;
import com.example.campusmap.data.branch.Room;
import com.example.campusmap.database.SQLiteHelperCampusInfo;

import java.util.ArrayList;

public class ListViewTree {
    private static final int SIMPLE_LIST_ITEM = android.R.layout.simple_list_item_1;
    private Context context;
    final private ArrayList<ListView> mListViews;
    private ArrayAdapter<Building> mBuildingAdapter;
    private ArrayAdapter<Floor> mFloorAdapter;
    private ArrayAdapter<Room> mRoomAdapter;
    private Animation appear;
    private Animation darkking;
    private final int showColor;
    private final int hideColor;
    private int buildingClickedIndex;
    private int mFocusedIndex = 0;


    /**
     * ..생성자..
     * 데이터를 초기화한다.
     * @param context : 리스트뷰를 출력할 Context
     */
    public ListViewTree(Context context) {
        this.context = context;
        mListViews = new ArrayList<>();
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
            if (mListViews.size() > 0)
                throw  new Exception("루트를 지정하기 전에 생성된 가지(Branch)가 있습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            mListViews.clear();
            Log.w("Exception", "listPartners를 비웠습니다..");
        } finally {
            listView.setVisibility(View.INVISIBLE);
            mListViews.add(listView);
        }
    }

    /**
     * 한 가지(ListView, TextView)을 추가한다.
     * @param listView : 보여지는 리스트뷰
     */
    public void addBranch(@NonNull ListView listView) {
        listView.setVisibility(View.INVISIBLE);
        mListViews.add(listView);
    }

    /**
     * Branch 추가가 모두 완료되고 실행되어야 하는 함수이며,
     * 이벤트와 데이터를 로드한다.
     */
    public void complete() {
        // requestPermission animation
        if (appear==null || darkking==null)
            throw  new NullPointerException("appear Animation or darking Animation is null");
        //
        mListViews.get(0).setVisibility(View.VISIBLE);

        // ## 리스트에 파서결과 넣기 ##
        buildListView();

        // ## 이벤트 추가 ##
        setEventListener();
    }

    private void buildListView() {

        if (mBuildingAdapter == null) {
            mBuildingAdapter = new ArrayAdapter<>(
                    context,
                    SIMPLE_LIST_ITEM
            );
        }
        mBuildingAdapter.clear();

        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(context);
        mBuildingAdapter.addAll(helper.getBuildingList());

        mListViews.get(0).setAdapter(mBuildingAdapter);
    }

    /**
     * 이벤트리스너를 추가한다.
     * 0:층리스트를 불러옴.
     * 1:룸리스트를 불러옴.
     * 2:층상세로 이동.
     */
    private void setEventListener() {
        final int lastIndex = mListViews.size() - 1;
        for (int treeIndex=0; treeIndex<lastIndex; treeIndex++) {
            final int currentIndex = treeIndex;
            ListView currentListView = mListViews.get(currentIndex);

            // ## 아이템클릭이벤트 ##
            currentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mFocusedIndex = currentIndex+1;
                    if (currentIndex == 0) {
                        buildingClickedIndex = position;
                    }
                    // # 현재 리스트뷰에 맞는 데이터 삽입. #
                    pushBranchItem(currentIndex+1, position);
                    showChild(currentIndex);

                    // # 해당 리스트뷰의 부모들을 어둡게 한다. #
                    highlightListView(currentIndex);
                }
            });
            // ## 스크롤이벤트 ##
            currentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    mFocusedIndex = currentIndex;
                    // # 스크롤된 다음 ListView들을 모두 숨긴다. #
                    mListViews.get(currentIndex).setBackgroundColor(showColor);
                    hideChildren(currentIndex + 1);

                    // # 해당 리스트뷰의 부모들을 어둡게 한다. #
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

    /**
     * 클릭된 리스트뷰의 자식의 레이아웃이 나타난다.
     * @param index : 현재 mListViews 의 위치
     */
    private void showChild(int index) {
        int lastIndex = mListViews.size() - 1;

        // 모든 자식을 숨긴다.
        hideChildren(index + 1);

        // 리스트뷰를 나타나게 한다.
        if (index < lastIndex) {
            ListView currentListView = mListViews.get(index+ 1);
            currentListView.setVisibility(View.VISIBLE);
            currentListView.startAnimation(appear);
            currentListView.setBackgroundColor(showColor);
        }
    }

    private void highlightListView(int index) {
        for (int i=0; i<=index; i++) {
            float rate = (float)Math.pow(0.7, (i)+1);
            mListViews.get(index - i).setBackgroundColor(darker(hideColor, rate));
        }
    }

    /**
     * 리스트뷰의 자식들을 숨긴다.
     * @param index : 현재 listpartners 의 위치
     */
    private void hideChildren(int index) {
        for (int i = index; i< mListViews.size(); i++) {
            ListView currentListView = mListViews.get(i);
            currentListView.clearAnimation();
            currentListView.setVisibility(View.INVISIBLE);
        }
    }

    private void pushBranchItem(int deeps, int position) {
        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(context);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                context,
                SIMPLE_LIST_ITEM
        );

        switch (deeps) {
            case 1: // get floor list
                if (mFloorAdapter == null) {
                    mFloorAdapter = new ArrayAdapter<>(
                            context,
                            SIMPLE_LIST_ITEM
                    );
                }
                mFloorAdapter.clear();
                Building currentBuilding = mBuildingAdapter.getItem(position);
                mFloorAdapter.addAll(helper.getFloorList(currentBuilding.getID()));
                for (int i=0; i<mFloorAdapter.getCount(); i++) {
                    Floor floor = mFloorAdapter.getItem(i);
                    dataAdapter.add(floor.toString());
                }
                break;
            case 2: // get room list
                if (mRoomAdapter == null) {
                    mRoomAdapter = new ArrayAdapter<>(
                            context,
                            SIMPLE_LIST_ITEM
                    );
                }
                mRoomAdapter.clear();
                Floor currentFloor = mFloorAdapter.getItem(position);
                mRoomAdapter.addAll( helper.getRoomList(currentFloor.getBuildingID(), currentFloor.getID()) );
                for (int i=0; i<mRoomAdapter.getCount(); i++) {
                    Room room = mRoomAdapter.getItem(i);
                    dataAdapter.add(room.toString());
                }
                break;
        }
        mListViews.get(deeps).setAdapter(dataAdapter);
    }


    public static int darker (int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor; // value component
        return Color.HSVToColor(hsv);
    }

    public int getFocusedIndex() {
        return mFocusedIndex;
    }


    public void hideLastListView() {
        //Log.i("Index", "current index : " + index);
        mListViews.get(mFocusedIndex).setVisibility(View.INVISIBLE);
        mListViews.get(mFocusedIndex -1).setBackgroundColor(showColor);
        highlightListView(mFocusedIndex - 2);
        mFocusedIndex--;
    }
}
