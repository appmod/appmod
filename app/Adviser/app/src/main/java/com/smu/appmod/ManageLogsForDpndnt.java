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

public class ManageLogsForDpndnt extends Activity {
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
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Logs</font>"));
        utility = new UtilityClass(getApplicationContext());
        dbManager = new DBManager(this);
        dbManager.open();
        listView = (ExpandableListView) findViewById(R.id.advices_list);
        emptyTV = (TextView) findViewById(R.id.empty);
        headingTV = (TextView) findViewById(R.id.text);
        headingTV.setText("Here are the logs-");
        backbtn = (Button) findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
              public void onClick(View view) {
                 Intent intent = new Intent(ManageLogsForDpndnt.this, MainActivity.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 startActivity(intent);
              }
        }
        );
        showList();
    }

    @Override
    public void onResume() {
        super.onResume();
        showList();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManageLogsForDpndnt.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showList() {
        ArrayList<Advice> adviceList = new ArrayList<Advice>();
        adviceList.clear();
        String query = "SELECT * FROM ADVICES WHERE seeker_name=? ORDER BY date ASC";
        Cursor c1 = dbManager.selectQueryWithArgs(query, "myself");
        if (c1 != null && c1.getCount() != 0) {
            emptyTV.setVisibility(View.GONE);
            if (c1.moveToFirst()) {
                do {
                    Advice adviceItem = new Advice();
                    adviceItem.setAnomaly(c1.getString(c1
                            .getColumnIndex("anomaly")));
                    adviceItem.setAnomalyDate(c1.getString(c1
                            .getColumnIndex("date")));
                    String advice = c1.getString(c1.getColumnIndex("advice"));
                    if (advice != null) {
                        if (advice.equalsIgnoreCase("pending")) {
                            adviceItem.setAdviceGiven("Advice is pending from the advisor.");
                        } else if (advice.equalsIgnoreCase("You took your own action for this anomaly.")) {
                            adviceItem.setAdviceGiven(advice);
                        } else {
                            adviceItem.setAdviceGiven("You are advised to " + advice + " the application.");
                        }
                    }
                    if (!(c1.getString(c1.getColumnIndex("_id")).equals("depen")))
                        adviceItem.setAdviceAskedDate("You asked advice for this anomaly on " + c1.getString(c1
                                .getColumnIndex("advisordate")) + ".");
                    adviceItem.setAdviceFollowed(c1.getString(c1.getColumnIndex("followed")));
                    adviceList.add(adviceItem);
                } while (c1.moveToNext());
            }
        } else {
            headingTV.setVisibility(View.GONE);
            emptyTV.setText(getResources().getString(R.string.noAdvice) + " you.");
        }
        if (c1 != null)
            c1.close();

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(
                ManageLogsForDpndnt.this, adviceList);
        listView.setAdapter(listAdapter);
    }
}
