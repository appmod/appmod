package com.smu.appmod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class AdviserNotificationAdapter extends BaseAdapter {

    Context context;
    ArrayList<Advice> notifList;

    public AdviserNotificationAdapter(Context context, ArrayList<Advice> list) {
        this.context = context;
        notifList = list;
    }

    @Override
    public int getCount() {
        return notifList.size();
    }

    @Override
    public Object getItem(int position) {
        return notifList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final Advice notifListItem = notifList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notification_row, null);
        }
        final TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView value = (TextView) convertView.findViewById(R.id.value);
        if (notifListItem.getSeeker().equals("feedback")) {
            date.setText(notifListItem.getAnomalyDate());
            value.setText(notifListItem.getAnomaly());
        } else {
            date.setText(notifListItem.getAnomalyDate());
            value.setText(notifListItem.getSeeker() + " is asking help for the anomaly - " + notifListItem.getAnomaly().split("Click")[0].trim());
        }
        return convertView;
    }
}
