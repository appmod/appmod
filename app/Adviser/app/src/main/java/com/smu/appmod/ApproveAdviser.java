package com.smu.appmod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class ApproveAdviser extends Activity {
    Button yes, no;
    TextView tv;
    String adviserPhone;
    ProgressDialog progressDialog;
    UtilityClass utility;
    String date;
    String adviserName;
    private static final String TAG = "AA";

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_advisor);
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Approve Advisor</font>"));
        tv = (TextView) findViewById(R.id.tv);
        Intent intent = getIntent();
        adviserName = intent.getStringExtra("adviserName");
        date = intent.getStringExtra("date");
        utility = new UtilityClass(getApplicationContext());
        adviserPhone = utility.getAdviserPhone();
        String text = "<font color=#CC0000>" + adviserName + "</font><font color=#08457E> has added you as an advisee. Do you approve?</font> ";
        tv.setText(Html.fromHtml(text));
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        //progressDialog = new ProgressDialog(this);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(ApproveAdviser.this, "", "Please wait...", true);
                new SendDetailsAsyncTask(getApplicationContext(), "Accepted").execute();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(ApproveAdviser.this, "", "Please wait...", true);
                new SendDetailsAsyncTask(getApplicationContext(), "Not Accepted").execute();
            }
        });
    }

    class SendDetailsAsyncTask extends AsyncTask<String, Void, Boolean> {
        private Context mContext;
        String flag = null;
        private String mreply;

        public SendDetailsAsyncTask(Context context, String reply) {
            mContext = context;
            mreply = reply;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final UtilityClass utility = new UtilityClass(getApplicationContext());
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/approveAdviser.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    String phoneNo = utility.getLoginPhone();
                    //Log.d(TAG, "***** phoneNo=" + phoneNo + ":" + mreply + ":" + adviserPhone);
                    flag = utility.serverInteraction(urlString, phoneNo + ":" + mreply + ":" + adviserPhone);
                } else {
                    ApproveAdviser.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(ApproveAdviser.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "***** Error=" + e.toString());
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            String value = null;
            if (flag != null) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(flag);
                    value = jsonObj.getString("success");
                } catch (JSONException e) {
                    Log.e(TAG, "***** Error=" + e.toString());
                }
                final DBManager dbManager;
                dbManager = new DBManager(ApproveAdviser.this);
                dbManager.open();
                final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                String query2 = null;
                if (value != null && value.equals("1")) {
                    dbManager.updateReadDepndnt(date);
                    if (mreply.equals("Accepted"))
                        query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                                + currentDateTimeString + "','" + "myself" + "','" + "You have approved " + adviserName.trim() + " as your advisor." + "')";
                } else if (mreply.equals("Not Accepted")) {
                    query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                            + currentDateTimeString + "','" + "myself" + "','" + "You have not approved " + adviserName.trim() + " as your advisor." + "')";
                }
                dbManager.executeQuery(query2);
                utility.setAdviser(true);
                Intent intent = new Intent(ApproveAdviser.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Toast toast = Toast.makeText(ApproveAdviser.this, "Notification could not be sent to advisor. Please try again!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}


