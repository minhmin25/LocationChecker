package com.dev.minhmin.locationchecker.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dev.minhmin.locationchecker.R;
import com.dev.minhmin.locationchecker.model.Checker;

import java.util.ArrayList;

/**
 * Created by Minh min on 4/28/2017.
 */

public class ListCheckerAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Checker> listCheckers = new ArrayList<>();

    public ListCheckerAdapter(Activity activity, ArrayList<Checker> listCheckers) {
        this.activity = activity;
        this.listCheckers = listCheckers;
    }

    @Override
    public int getCount() {
        return listCheckers.size();
    }

    @Override
    public Object getItem(int i) {
        return listCheckers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Viewholder viewholder;
        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.item_list_checker, viewGroup, false);
            viewholder = new Viewholder();
            viewholder.tvCheckerName = (TextView) view.findViewById(R.id.tv_checker_name);
            view.setTag(viewholder);
        } else {
            viewholder = (Viewholder) view.getTag();
        }
        viewholder.tvCheckerName.setText(listCheckers.get(i).getName());
        return view;
    }

    private class Viewholder {
        TextView tvCheckerName;
    }
}
