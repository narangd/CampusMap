package com.example.campusmap.database;

import java.io.Serializable;

/**
 * Created by 성용 on 2016-06-28.
 */
public class SearchResultItem implements Serializable {
    public enum Tag {
        BUILDING, ROOM
    }
    public int mParentId;
    public String mName;
    public Tag mTag;

    public SearchResultItem (String name, Tag tag, int parentId) {
        mName = name;
        mTag = tag;
        mParentId = parentId;
    }

    @Override
    public String toString() {
        return mName;
    }
}
