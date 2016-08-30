package com.example.campusmap.algorithm;

import android.util.Log;

import java.util.ArrayList;

public class SortedList<T extends Comparable<T>> {
    private ArrayList<T> list = new ArrayList<>();

    public SortedList() {
    }

    public void insert(T t) {
        int size = list.size();
        int low = 0;
        int high = size - 1;
        int mid = 0;
        int count=0;

        while (low <= high) {
            mid = (high + low)/2;
            int compare = list.get(mid).compareTo(t);
//            Log.i("SortedList", "insert: low:" + low + ", high:" + high + ", compare:" + compare);
            if (compare == 0) {
                break;
            } else if (compare < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
            if (++count > size) {
                Log.e("SortedList", "insert: out of count.. " + count + ", low:" + low + ", mid:" + mid + ", high:" + high);
                break;
            }
        }

        if (size<=0 || list.get(size-1).compareTo(t) > 0) {
            list.add(t);
        } else {
            list.add(mid, t);
        }

    }

    public T pollFirst() {
        if (size() <= 0) {
            return null;
        }
        return list.remove(0);
    }

    public int closest(T t) {

        return 0;
    }

    public boolean contains(T t) {
        return list.contains(t);
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
