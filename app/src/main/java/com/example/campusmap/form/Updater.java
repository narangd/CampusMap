package com.example.campusmap.form;

public class Updater {
    private String mTitle;
    private String mContents;
    private int mVote;

    public Updater(String title, String contents, int vote) {
        this.mTitle = title;
        this.mContents = contents;
        this.mVote = vote;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContents() {
        return mContents;
    }

    public int getVote() {
        return mVote;
    }
}