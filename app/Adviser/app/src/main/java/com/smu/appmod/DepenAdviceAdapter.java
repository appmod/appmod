package com.smu.appmod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class DepenAdviceAdapter extends BaseAdapter {

    Context context;
    ArrayList<Dependant> depenList;

    public DepenAdviceAdapter(Context context, ArrayList<Dependant> list) {
        this.context = context;
        depenList = list;
    }

    @Override
    public int getCount() {
        return depenList.size();
    }

    @Override
    public Object getItem(int position) {
        return depenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final Dependant dependantListItem = depenList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dependant_advice_row, null);
        }
        final TextView dependantName = (TextView) convertView.findViewById(R.id.dep_name);
        dependantName.setText(dependantListItem.getDependantName());
        return convertView;
    }
}
