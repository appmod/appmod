package com.smu.appmod;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.util.ArrayList;

public class LogsListAct extends Activity {
    UtilityClass utility;
    private DBManager dbManager;
    private ExpandableListView listView;
    private TextView emptyTV, headingTV;
    private Button backbtn;
    String dependantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_list);
        Intent intent = getIntent();
        dependantName = intent.getStringExtra("dependantName");
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Logs</font>"));
        utility = new UtilityClass(getApplicationContext());
        dbManager = new DBManager(this);
        dbManager.open();
        listView = (ExpandableListView) findViewById(R.id.advices_list);
        emptyTV = (TextView) findViewById(R.id.empty);
        headingTV = (TextView) findViewById(R.id.text);
        headingTV.setText("Logs w.r.t " + dependantName + " are as follows-");
        backbtn = (Button) findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LogsListAct.this, ManageLogsForAdviser.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }}
        );
        showList();
    }

    @Override
    public void onResume() {
        super.onResume();
        showList();
    }

    private void showList() {
        ArrayList<Advice> adviceList = new ArrayList<Advice>();
        adviceList.clear();
        String query = "SELECT * FROM ADVICES WHERE seeker_name=? ORDER BY date ASC";
        Cursor c1 = dbManager.selectQueryWithArgs(query, dependantName);
        if (c1 != null && c1.getCount() != 0) {
            emptyTV.setVisibility(View.GONE);
            if (c1.moveToFirst()) {
                do {
                    Advice adviceItem = new Advice();
                    if (!c1.getString(c1
                            .getColumnIndex("_id")).equals("depen")) {
                        adviceItem.setAnomaly(dependantName + " is seeking help for the anomaly - " + c1.getString(c1
                                .getColumnIndex("anomaly")));
                    } else {
                        adviceItem.setAnomaly(c1.getString(c1
                                .getColumnIndex("anomaly")));
                    }
                    adviceItem.setAnomalyDate(c1.getString(c1
                            .getColumnIndex("date")));

                    String fetchedAdv = c1.getString(c1
                            .getColumnIndex("advice"));
                    if (fetchedAdv != null) {
                        if (fetchedAdv.substring(fetchedAdv.length() - 1, fetchedAdv.length()).equals(".")) {
                            String advice = fetchedAdv.substring(0, fetchedAdv.length() - 1);
                            adviceItem.setAdviceGiven("Advice: " + advice);
                        } else {
                            adviceItem.setAdviceGiven("Advice: " + fetchedAdv);
                        }
                    }
                    if (c1.getString(c1
                            .getColumnIndex("followed")) != null) {
                        adviceItem.setAdviceFollowed(c1.getString(c1
                                .getColumnIndex("followed")));
                    }
                    adviceList.add(adviceItem);
                } while (c1.moveToNext());
            }
        } else {
            headingTV.setVisibility(View.GONE);
            emptyTV.setText(getResources().getString(R.string.noAdvice) + " " + dependantName + ".");
        }
        if (c1 != null)
            c1.close();

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(
                LogsListAct.this, adviceList);
        listView.setAdapter(listAdapter);
    }
}
