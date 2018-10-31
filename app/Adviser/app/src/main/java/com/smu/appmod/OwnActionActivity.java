package com.smu.appmod;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

import java.util.*;

public class OwnActionActivity extends Activity {
    /**
     * BELOW two parameters are added for outsourcing data.
     */
    static String[] outsourcing = null;
    static Map anomaly2percentages = new HashMap<String, String[]>();

    Button doNothing, uninstall, kill;
    TextView text;
    UtilityClass utility;
    static String phone, date;
    ProgressDialog progressDialog;
    static String message, anomalyid;
    private static final String TAG = "OAA";
    ImageView image;
    
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OwnActionActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (outsourcing == null) {
            outsourcing = getResources().getStringArray(R.array.outsourcing);
            for (String anomaly_percent : outsourcing) {
                String[] anomaly_array = anomaly_percent.split(",");
                String[] percentages = {anomaly_array[1],anomaly_array[2],anomaly_array[3]};
                anomaly2percentages.put(anomaly_array[0],percentages);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.takeactiondialog);
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Take Action</font>"));
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        message = intent.getStringExtra("message");
        anomalyid = intent.getStringExtra("anomalyid");
        text = (TextView) findViewById(R.id.text);
        String str = "<font color=#08457E>Anomaly - </font><font color=#CC0000>" + message.split("Click")[0].trim() + "</font><font color=#08457E><br><br>You have chosen to take your own action.</font>";
        text.setText(Html.fromHtml(str));

        image = (ImageView) findViewById(R.id.app);
        String appname = message.split(" ")[0].trim();
        if (appname.equalsIgnoreCase("YouTube")) {
            image.setImageResource(R.drawable.app_yt);
        } else if (appname.equalsIgnoreCase("Facebook")) {
            image.setImageResource(R.drawable.app_fb);
        } else if (appname.equalsIgnoreCase("Instagram")) {
            image.setImageResource(R.drawable.app_is);
        } else if (appname.equalsIgnoreCase("Gmail")) {
            image.setImageResource(R.drawable.app_gm);
        } else if (appname.equalsIgnoreCase("Whatsapp")) {
            image.setImageResource(R.drawable.app_wh);
        } else if (appname.equalsIgnoreCase("Clock")) {
            image.setImageResource(R.drawable.app_cl);
        } else if (appname.equalsIgnoreCase("Candy")) {
            image.setImageResource(R.drawable.app_cr);
        } else if (appname.equalsIgnoreCase("Sudoku")) {
            image.setImageResource(R.drawable.app_sk);
        } else {
            image.setImageResource(R.drawable.app);
        }

        final DBManager dbManager;
        dbManager = new DBManager(OwnActionActivity.this);
        dbManager.open();
        doNothing = (Button) findViewById(R.id.dialogdonothing);
        uninstall = (Button) findViewById(R.id.dialogButtonuninstall);
        kill = (Button) findViewById(R.id.dialogButtonkill);

        String[] threePercentages = (String[]) anomaly2percentages.get(message);
        uninstall.setText(uninstall.getText() + " " + threePercentages[0]);
        doNothing.setText(doNothing.getText() + " " + threePercentages[2]);
        kill.setText(kill.getText() + " " + threePercentages[1]);
        doNothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(OwnActionActivity.this, "", "Please wait...", true);
                dbManager.updateReadDepndnt(date);
                new SendDetailsOwnAction("Do nothing", anomalyid.trim()).execute();
            }
        });
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(OwnActionActivity.this, "", "Please wait...", true);
                dbManager.updateReadDepndnt(date);
                new SendDetailsOwnAction("Uninstall", anomalyid.trim()).execute();
            }
        });
        kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(OwnActionActivity.this, "", "Please wait...", true);
                dbManager.updateReadDepndnt(date);
                new SendDetailsOwnAction("Kill", anomalyid.trim()).execute();
            }
        });
    }

    class SendDetailsOwnAction extends AsyncTask<String, Void, Boolean> {
        private String mResponse;
        private String mAnomalyid;

        public SendDetailsOwnAction( String response, String anomalyid) {
            mResponse = response;
            mAnomalyid = anomalyid;
        }


        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final UtilityClass utility = new UtilityClass(getApplicationContext());
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/ownaction.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    String phone = utility.getLoginPhone();
                    utility.serverInteraction(urlString, phone + ":" + mAnomalyid + ":" + message + ":" + mResponse + ":" + "WithoutTakingAdvice");
                } else {
                    OwnActionActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(OwnActionActivity.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
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
            final Dialog dialog1;
            dialog1 = new Dialog(OwnActionActivity.this);
            dialog1.setContentView(R.layout.actioncompleteddialog);
            dialog1.setTitle(Html.fromHtml("<font color='#08457E'><b>Action Completed!</font>"));
            Button okbtn = (Button) dialog1.findViewById(R.id.ok);
            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OwnActionActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            TextView replyTV = (TextView) dialog1.findViewById(R.id.reply);
            String text = null;
            if (mResponse.equals("Do nothing")) {
                text = "Nothing has been done to the application.";
            } else if (mResponse.equals("Uninstall")) {
                text = "Please uninstall the application manually.";
            } else if (mResponse.equals("Kill")) {
                text = "Please kill the application manually.";
            }
            replyTV.setText(text);
            if (dialog1 != null && !dialog1.isShowing())
                dialog1.show();

            DBManager dbManager = new DBManager(OwnActionActivity.this);
            dbManager.open();
            //String query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly,advice,followed,pending) values ('" + "depen" + "','"
            //        + date + "','" + "myself" + "','" + message + "','" + "You did not take advice for this anomaly." + "','" + "actiontaken" + "','" + "no" + "')";
            String query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly,advice,followed,pending) values ('" + "depen" + "','"
                    + date + "','" + "myself" + "','" + message + "','" + "You took your own action for this anomaly." + "','" + text + "','" + "no" + "')";
            dbManager.executeQuery(query2);
            dbManager.updateAnomaly(date.trim(), mAnomalyid);
        }
    }
}


