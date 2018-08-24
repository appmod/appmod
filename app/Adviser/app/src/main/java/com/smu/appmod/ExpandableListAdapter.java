package com.smu.appmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<Advice> _listDataHeader;
    private HashMap<Advice, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<Advice> listDataHeader) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        List<String> mList = null;
        _listDataChild = new HashMap<Advice, List<String>>();
        for (Advice adv : _listDataHeader) {
            mList = new ArrayList<String>();
            if (adv.getAdviceAskedDate() != null)
                mList.add(adv.getAdviceAskedDate());
            if (adv.getAdviceGiven() != null)
                mList.add(adv.getAdviceGiven());
            if (adv.getAdviceFollowed() != null && !adv.getAdviceFollowed().equals("null")) {
                mList.add(adv.getAdviceFollowed());
            }
            if (mList != null)
                this._listDataChild.put(adv, mList);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        View v = (View) convertView.findViewById(R.id.divider_description_child);
        if (isLastChild) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this._listDataChild.get(this._listDataHeader.get(groupPosition)) != null) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition).getAnomaly();
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).toString();
        String date = _listDataHeader.get(groupPosition).getAnomalyDate();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        TextView anomalydate = (TextView) convertView.findViewById(R.id.anomalydate);
        ImageView indicator = (ImageView) convertView.findViewById(R.id.ivGroupIndicator);
        if (getChildrenCount(groupPosition) == 0) {
            indicator.setVisibility(View.INVISIBLE);
            anomalydate.setPadding(75, 0, 0, 0);
        } else {
            indicator.setVisibility(View.VISIBLE);
            indicator.setImageResource(isExpanded ? R.drawable.group_expanded : R.drawable.group_closed);
            anomalydate.setPadding(20, 0, 0, 0);
        }
        lblListHeader.setText(headerTitle);
        anomalydate.setText(date);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
