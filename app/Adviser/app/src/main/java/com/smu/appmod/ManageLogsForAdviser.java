package com.smu.appmod;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class ManageLogsForAdviser extends Activity {
    UtilityClass utility;
    private DBManager dbManager;
    private ListView listView;
    private TextView emptyTV, headingTV;
    private Button backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_logs);
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Manage Logs</font>"));
        utility = new UtilityClass(getApplicationContext());
        dbManager = new DBManager(this);
        dbManager.open();
        listView = (ListView) findViewById(R.id.dependants_list);
        emptyTV = (TextView) findViewById(R.id.empty);
        headingTV = (TextView) findViewById(R.id.text);
        backbtn = (Button) findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ManageLogsForAdviser.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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
        ArrayList<Dependant> depenList = new ArrayList<Dependant>();
        depenList.clear();
        Dependant depenItem = new Dependant();
        depenItem.setDependantName("myself");
        depenList.add(depenItem);
        String query = "SELECT * FROM DEPENDANTS";
        Cursor c1 = dbManager.selectQuery(query);
        if (c1 != null && c1.getCount() != 0) {
            emptyTV.setVisibility(View.GONE);
            if (c1.moveToFirst()) {
                do {
                    depenItem = new Dependant();
                    depenItem.setDependantName(c1.getString(c1.getColumnIndex("name")));
                    depenList.add(depenItem);
                } while (c1.moveToNext());
            }
        } else {
            //emptyTV.setText(R.string.noDepenAndAdvice);
            //headingTV.setVisibility(View.GONE);
        }
        if (c1 != null)
            c1.close();

        DepenAdviceAdapter depenListAdapter = new DepenAdviceAdapter(ManageLogsForAdviser.this, depenList);
        listView.setAdapter(depenListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TextView nametextview = (TextView) view.findViewById(R.id.dep_name);
                String name = nametextview.getText().toString();
                Intent intent = new Intent(ManageLogsForAdviser.this, LogsListAct.class);
                intent.putExtra("dependantName", name);
                startActivity(intent);
            }
        });
    }
}
