package com.teamtreehouse.ribbit.models;

import java.util.ArrayList;

/**
 * Created by benjakuben on 10/12/16.
 */

public class Relation<T> {

    private static ArrayList relatedItems;

    public Relation() {
        relatedItems = new ArrayList<T>();
    }

    public void add(T item) {
        relatedItems.add(item);
    }

    public void remove(T item) {
        relatedItems.remove(item);
    }

    public static Query getQuery() {
        Query query = new Query(Relation.class.getSimpleName());
        query.setDataSet(relatedItems);
        return query;
    }
}
