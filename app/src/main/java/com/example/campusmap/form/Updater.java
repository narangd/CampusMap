package com.example.campusmap.form;

public class Updater {
    private int mID;            // 고유 식별자
    private String mTitle;      // 제목
    private String mContents;   // 내용
    private int mVote;          // 나의 요청을 제외한 공감들
    private boolean mVoted;     // 내가 이 요청을 공감함

    public Updater(int id, String title, String contents, int vote, boolean voted) {
        mID = id;
        mTitle = title;
        mContents = contents;
        mVote = vote;
        mVoted = voted;
        if (mVoted) {
            mVote --;
        }
    }

    public int getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContents() {
        return mContents;
    }

    public int getVote() {
        return mVote + (mVoted ? 1 : 0);
    }

    public void setVoted(boolean voted) {
        mVoted = voted;
    }

    public boolean isVoted() {
        return mVoted;
    }
}