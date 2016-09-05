package com.example.campusmap.algorithm;

import java.util.ArrayList;

/**
 * Created by 성용 on 2016-09-01.
 */
public class MyArrayList<T extends Comparable<T>> {
    private ArrayList<T> list = new ArrayList<>();

    public MyArrayList() {

    }

    public void add(T t) {
        list.add(t);
    }

    public boolean contains(T t) {
        return list.contains(t);
    }

    public int size() {
        return list.size();
    }

    public T pullLowest() {
        if (list.size() <= 0)
            return null;

        int low = 0;
        T low_t = list.get(low);
        for (int i=1; i<list.size(); i++) {
            if (low_t.compareTo(list.get(i)) > 0) {
                low = i;
                low_t = list.get(low);
            }
        }
        return list.remove(low);
    }

    public void clear() {
        list.clear();
    }
}
