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
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.widget.ImageView;

public class GetAdvice extends Activity {
    Button getadvice_btn, ownaction_btn, detailBtn, closeBtn, doNothing, uninstall, kill;
    static String[] detail = null;
    static Map anomaly2detail = new HashMap<String, String[]>();
    TextView text;
    UtilityClass utility;
    static String phone, date;
    ProgressDialog progressDialog;
    static String adviserName, message, anomalyid;
    ImageView image;
    private static final String TAG = "GA";

    Dialog dialog, dialog1;

    static Set<String> ownActionSet = new HashSet<String>();
    static String[] ownactionStrs = null;
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (dialog1 != null) {
            dialog1.dismiss();
            dialog1  = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        message = intent.getStringExtra("message");
        //Log.e(TAG, "onCreate" + message);
        if (ownactionStrs == null) {
            ownactionStrs = getResources().getStringArray(R.array.ownaction);
            for (String str : ownactionStrs) {
                ownActionSet.add(str);
            }
        }


        if (ownActionSet.contains(message)) {
            setContentView(R.layout.takeactiondialog);
            getActionBar().setIcon(R.drawable.icon);
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
            getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Take Action</font>"));
            try {
                final UtilityClass utility = new UtilityClass(getApplicationContext());
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/getAnomalyID.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    String notification = message.split("Click")[0].trim();
                    anomalyid = utility.serverInteraction(urlString, notification);
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
                return;
            }
            text = (TextView) findViewById(R.id.text);
            String str = "<font color=#08457E>Anomaly - </font><font color=#CC0000>" + message.split("Click")[0].trim() + "</font><font color=#08457E><br><br>You have chosen to take your own action.</font>";
            text.setText(Html.fromHtml(str));

            image = (ImageView) findViewById(R.id.app);
            String appname = message.split(" ")[0].trim();
            Helper.setAppImage(image, appname);
            detailBtn = (Button) findViewById(R.id.detail_adviee_btn);

            detailBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (detail == null) {
                        detail = getResources().getStringArray(R.array.detail);
                        for (String detail_item : detail) {
                            String[] detail_array = detail_item.split(",");
                            String[] id_context = {detail_array[1],detail_array[2]};
                            anomaly2detail.put(detail_array[0],id_context);
                        }
                    }

                    String[] id_context = (String[]) anomaly2detail.get(message);


                    dialog = new Dialog(GetAdvice.this);
                    dialog.setContentView(R.layout.detaildialog);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    dialog.setTitle(Html.fromHtml("<font color='#08457E'><b>Details of the Anomaly</font>"));

                    if (id_context != null) {
                        WebView webviewAbout = (WebView) dialog.findViewById(R.id.google_play);
                        webviewAbout.setWebViewClient(new WebViewClient());
                        //webviewAbout.loadUrl("https://play.google.com/store/apps/details?id=" + id_context[0] + "&hl=en");
                        webviewAbout.loadUrl("file:///android_asset/" + id_context[0]);

                        WebView webviewContext = (WebView) dialog.findViewById(R.id.context);
                        webviewContext.setWebViewClient(new WebViewClient());
                        webviewContext.loadUrl("file:///android_asset/" + id_context[1]);
                    }


                    closeBtn = (Button) dialog.findViewById(R.id.close_btn);
                    if (closeBtn != null) {
                        closeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }

                    if (dialog != null && !dialog.isShowing())
                        dialog.show();
                }
            });

            final DBManager dbManager;
            dbManager = new DBManager(GetAdvice.this);
            dbManager.open();
            doNothing = (Button) findViewById(R.id.dialogdonothing);
            uninstall = (Button) findViewById(R.id.dialogButtonuninstall);
            kill = (Button) findViewById(R.id.dialogButtonkill);


            doNothing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog = ProgressDialog.show(GetAdvice.this, "", "Please wait...", true);
                    dbManager.updateReadDepndnt(date);
                    new GetAdvice.SendDetailsOwnAction("Do nothing", anomalyid.trim()).execute();
                }
            });
            uninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog = ProgressDialog.show(GetAdvice.this, "", "Please wait...", true);
                    dbManager.updateReadDepndnt(date);
                    new GetAdvice.SendDetailsOwnAction("Uninstall", anomalyid.trim()).execute();
                }
            });
            kill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog = ProgressDialog.show(GetAdvice.this, "", "Please wait...", true);
                    dbManager.updateReadDepndnt(date);
                    new GetAdvice.SendDetailsOwnAction("Kill", anomalyid.trim()).execute();
                }
            });
        } else {
            setContentView(R.layout.getadvice);
            getActionBar().setIcon(R.drawable.icon);
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
            getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Anomaly</font>"));
            utility = new UtilityClass(getApplicationContext());
            adviserName = utility.getAdviserName();
            image = (ImageView) findViewById(R.id.app);
            String appname = message.split(" ")[0].trim();
            Helper.setAppImage(image, appname);
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
        super.onCreate(savedInstanceState);
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
            dialog1 = new Dialog(GetAdvice.this);
            dialog1.setContentView(R.layout.actioncompleteddialog);
            dialog1.setTitle(Html.fromHtml("<font color='#08457E'><b>Action Completed!</font>"));
            Button okbtn = (Button) dialog1.findViewById(R.id.ok);
            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GetAdvice.this, MainActivity.class);
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

            DBManager dbManager = new DBManager(GetAdvice.this);
            dbManager.open();
            //String query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly,advice,followed,pending) values ('" + "depen" + "','"
            //        + date + "','" + "myself" + "','" + message + "','" + "You did not take advice for this anomaly." + "','" + "actiontaken" + "','" + "no" + "')";
            String query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly,advice,followed,pending) values ('" + "depen" + "','"
                    + date + "','" + "myself" + "','" + message + "','" + "You took your own action for this anomaly." + "','" + text + "','" + "no" + "')";
            dbManager.executeQuery(query2);
            dbManager.updateAnomaly(date.trim(), mAnomalyid);
        }
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


