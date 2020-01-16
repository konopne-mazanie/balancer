package com.uhreckysw.balancer.ui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class MyArrayAdapter<T> extends ArrayAdapter<T> {
    public MyArrayAdapter(Activity act, int itemLayoutId, ArrayList<T> elems) {
        super(act, itemLayoutId, elems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        return view;
    }
}
