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
import android.widget.ImageView;

public class GetAdvice extends Activity {
    Button getadvice_btn, ownaction_btn;
    TextView text;
    UtilityClass utility;
    static String phone, date;
    ProgressDialog progressDialog;
    static String adviserName, message;
    ImageView image;
    private static final String TAG = "GA";

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getadvice);
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Anomaly</font>"));
        utility = new UtilityClass(getApplicationContext());
        adviserName = utility.getAdviserName();
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        message = intent.getStringExtra("message");
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
        text = (TextView) findViewById(R.id.tv);
        String str = "<font color=#08457E>Anomaly - </font><font color=#CC0000>" + message.split("Click")[0].trim() + "</font><font color=#08457E><br><br>This may be a suspicious activity. You can take advice from your Advisor: </font><font color=#CC0000>" + adviserName.trim() + "</font>" +
                "<font color=#08457E> by clicking on the button below.</font> ";
        text.setText(Html.fromHtml(str));
        getadvice_btn = (Button) findViewById(R.id.getadvice);
        ownaction_btn = (Button) findViewById(R.id.ownaction);
        getadvice_btn.setOnClickListener(new View.OnClickListener() {
                                             public void onClick(View view) {
                                                 progressDialog = ProgressDialog.show(GetAdvice.this, "", "Please wait...", true);
                                                 new GetIDAsyncTask("yes").execute();
                                             }
                                         }
        );
        ownaction_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(GetAdvice.this, "", "Please wait...", true);
                new GetIDAsyncTask("no").execute();
            }
        });
    }

    class SendDetailsAsyncTask extends AsyncTask<String, Void, Boolean> {
        private String mAnomalyid;
        String flag;

        public SendDetailsAsyncTask(String anomalyid) {
            mAnomalyid = anomalyid;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final UtilityClass utility = new UtilityClass(getApplicationContext());
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/seekAdvise.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    String phone = utility.getLoginPhone();
                    Log.i("AppMod", "SeekAdvise: " + phone + ":" + mAnomalyid + ":" + message);
                    flag = utility.serverInteraction(urlString, phone + ":" + mAnomalyid + ":" + message);
                } else {
                    GetAdvice.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(GetAdvice.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
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
            DBManager dbManager = new DBManager(GetAdvice.this);
            dbManager.open();
            final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            if (flag != null) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(flag);
                    value = jsonObj.getString("success");
                } catch (JSONException e) {
                    Log.e(TAG, "***** Error=" + e.toString());
                }
                if (value != null && value.equals("1")) {
                    dbManager.updateReadDepndnt(date);
                    String query2 = "INSERT INTO ADVICES(_id,date,advisordate,seeker_name,anomaly,advice,followed,pending) values ('" + mAnomalyid + "','"
                            + date + "','"
                            + currentDateTimeString + "','" + "myself" + "','" + message + "','" + "Pending" + "','" + "null" + "','" + "yes" + "')";
                    dbManager.executeQuery(query2);
                    Toast toast = Toast.makeText(GetAdvice.this, "Request sent. Please wait!", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(GetAdvice.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(GetAdvice.this, "Request could not be sent. Please try again later.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(GetAdvice.this, "Request could not be sent. Please try again later.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    class GetIDAsyncTask extends AsyncTask<String, Void, Boolean> {
        String flag, isSeekingAdvice;

        public GetIDAsyncTask(String seekAdvice) {
            isSeekingAdvice = seekAdvice;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final UtilityClass utility = new UtilityClass(getApplicationContext());
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/getAnomalyID.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    String notification = message.split("Click")[0].trim();
                    flag = utility.serverInteraction(urlString, notification);
                } else {
                    GetAdvice.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(GetAdvice.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
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
            if (flag != null) {
                if (isSeekingAdvice.equals("yes")) {
                    new SendDetailsAsyncTask(flag.trim()).execute();
                } else {
                    Intent intt = new Intent(getApplicationContext(), OwnActionActivity.class);
                    intt.putExtra("date", date);
                    intt.putExtra("message", message);
                    intt.putExtra("anomalyid", flag);
                    startActivity(intt);
                }
            }
        }
    }

}


