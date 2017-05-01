package com.teamtreehouse.ribbit.models;

import com.teamtreehouse.ribbit.models.callbacks.FindCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by benjakuben on 10/12/16.
 */

public class Query<T extends Comparable<T>> {

    private List<T> queryItems;

    public Query(String className) {
        queryItems = new ArrayList<>();
    }

    public void orderByAscending(String key) {
        Collections.sort(queryItems);
    }

    public void addAscendingOrder(String key) {
        Collections.sort(queryItems);
    }

    public void addDescendingOrder(String key) {
        Collections.sort(queryItems, Collections.<T>reverseOrder());
    }

    public void setLimit(int limit) {
    }

    public void whereEqualTo(String key, String id) {
    }

    public void findInBackground(FindCallback<T> callback) {
        if (queryItems != null) {
            callback.done(queryItems, null);
        } else {
            callback.done(null, new Exception());
        }
    }

    public void setDataSet(List<T> data) {
        queryItems = data;
    }
}
