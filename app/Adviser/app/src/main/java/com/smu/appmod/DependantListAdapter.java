package com.smu.appmod;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class DependantListAdapter extends BaseAdapter {
    private DependantInterface callback;
    Context context;
    ArrayList<Dependant> depenList;
    private static final String TAG = "DLA";
    Dialog dialog;

    public DependantListAdapter(Context context, ArrayList<Dependant> list) {
        this.context = context;
        depenList = list;
    }

    public void setCallback(DependantInterface callback) {
        this.callback = callback;
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dependant_row, null);
        }
        final TextView dependantName = (TextView) convertView.findViewById(R.id.dependantName);
        TextView dependantStatus = (TextView) convertView.findViewById(R.id.dependantStatus);
        dependantName.setText(dependantListItem.getDependantName());
        dependantStatus.setText(dependantListItem.getDependantStatus());
        Button sendRequest = (Button) convertView.findViewById(R.id.button1);
        Button deletebtn = (Button) convertView.findViewById(R.id.button2);
        if (dependantListItem.getDependantStatus().equals(context.getResources().getString(R.string.paired))) {
            dependantName.setTextColor(Color.parseColor("#008000"));
            sendRequest.setVisibility(View.GONE);
            deletebtn.setVisibility(View.GONE);
        } else if (dependantListItem.getDependantStatus().equals(context.getResources().getString(R.string.sent_request))) {
            dependantName.setTextColor(Color.parseColor("#FFA812"));
            sendRequest.setVisibility(View.GONE);
            deletebtn.setVisibility(View.GONE);
        } else if (dependantListItem.getDependantStatus().equals(context.getResources().getString(R.string.not_registered))) {
            dependantName.setTextColor(Color.parseColor("#E32636"));
            deletebtn.setVisibility(View.GONE);
            sendRequest.setVisibility(View.GONE);
        } else {
            dependantName.setTextColor(Color.parseColor("#E32636"));
            deletebtn.setVisibility(View.GONE);
        }
        sendRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (callback != null) {
                    callback.callAddDependantAsyncTask(dependantListItem.getDependantName());
                }
            }
        });
        deletebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.delete_dialog);
                dialog.setTitle(Html.fromHtml("<font color='#08457E'><b>Delete</font>"));
                final String name = dependantListItem.getDependantName();
                TextView tv = (TextView) dialog.findViewById(R.id.tv);
                tv.setText("Are you sure you want to delete " + name + " ?");
                Button dialogButtonYes = (Button) dialog.findViewById(R.id.yes);
                Button dialogButtonNo = (Button) dialog.findViewById(R.id.no);
                dialogButtonYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.callDeleteDependantAsyncTask(name);
                        }
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }
                });
                dialogButtonNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }
                });
                if (dialog != null && !dialog.isShowing())
                    dialog.show();
            }
        });
        return convertView;
    }
}
