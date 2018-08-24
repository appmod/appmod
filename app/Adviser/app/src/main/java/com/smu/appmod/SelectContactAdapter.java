package com.smu.appmod;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectContactAdapter extends BaseAdapter {

    public List<Contact> _data;
    private ArrayList<Contact> arraylist;
    Context _c;
    ViewHolder v;

    public SelectContactAdapter(List<Contact> contacts, Context context) {
        _data = contacts;
        _c = context;
        this.arraylist = new ArrayList<Contact>();
        this.arraylist.addAll(_data);
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.contact_row, null);
        } else {
            view = convertView;
        }
        v = new ViewHolder();
        v.name = (TextView) view.findViewById(R.id.name);
        v.phone = (TextView) view.findViewById(R.id.phone);
        v.check = (CheckBox) view.findViewById(R.id.check);
        final Contact data = _data.get(i);
        v.name.setText(data.getName());
        v.phone.setText(data.getPhone());
        v.check.setChecked(data.getCheckedBox());
        v.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int j = 0; j < _data.size(); j++) {
                    if (j == i) {
                        _data.get(j).setCheckedBox(true);
                    } else {
                        _data.get(j).setCheckedBox(false);
                    }
                }
                notifyDataSetChanged();
            }
        });
        view.setTag(data);
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _data.clear();
        if (charText.length() == 0) {
            _data.addAll(arraylist);
        } else {
            for (Contact wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    _data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView name, phone;
        CheckBox check;
    }
}