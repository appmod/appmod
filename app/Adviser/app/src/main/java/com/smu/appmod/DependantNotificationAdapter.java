package com.smu.appmod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DependantNotificationAdapter extends BaseAdapter {

    Context context;
    ArrayList<DependantNotifications> notifList;

    public DependantNotificationAdapter(Context context, ArrayList<DependantNotifications> list) {
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
        final DependantNotifications notifListItem = notifList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notification_row, null);
        }
        final TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView value = (TextView) convertView.findViewById(R.id.value);
        if (notifListItem.getRead().equals("unread")) {
            date.setText(notifListItem.getDate());
            value.setText(notifListItem.getValue());
        }
        return convertView;
    }
}
