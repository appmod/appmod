package com.smu.appmod;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdviceReceived extends Activity {
    /**
     * BELOW two parameters are added for outsourcing data.
     */
    static String[] outsourcing = null;
    static Map anomaly2percentages = new HashMap<String, String[]>();
    Button followBtn, dontfollowBtn;
    String reply, anomalyid, anomaly, date;
    TextView anomalyTV, replytv;
    Dialog dialog, dialog1;
    DBManager dbManager;
    static String action;
    ProgressDialog progressDialog;
    ImageView image;
    private static final String TAG = "AR";
    private long mLastClickTime = 0;


    static String[] detail = null;
    static Map anomaly2detail = new HashMap<String, String[]>();


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (dialog1 != null) {
            dialog1.dismiss();
            dialog1 = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advice_received);
        Intent intent = getIntent();
        reply = intent.getStringExtra("reply");
        anomalyid = intent.getStringExtra("anomalyid");
        anomaly = intent.getStringExtra("anomaly");
        date = intent.getStringExtra("date");
        image = (ImageView) findViewById(R.id.app);
        final String appname = anomaly.split(" ")[0].trim();
        Helper.setAppImage(image, appname);
        dbManager = new DBManager(this);
        dbManager.open();
        dialog = new Dialog(this);
        anomalyTV = (TextView) findViewById(R.id.anomaly);
        replytv = (TextView) findViewById(R.id.reply);
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Advice Received</font>"));
        String str = "<font color=#CC0000>" + " Anomaly:" + "</font> " +
                "<font color=#08457E>" + anomaly.split("Click")[0].trim() + " </font> ";
        String str1 = "<font color=#CC0000>" + " Advice Received:" + "</font> " +
                "<font color=#08457E>" + reply.substring(0, 1).toUpperCase() + reply.substring(1) + " the application </font> ";
        anomalyTV.setText(Html.fromHtml(str));
        replytv.setText(Html.fromHtml(str1));
        followBtn = (Button) findViewById(R.id.follow);
        dontfollowBtn = (Button) findViewById(R.id.dont);
        followBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbManager.updateAdviceReceived(anomalyid);
                progressDialog = ProgressDialog.show(AdviceReceived.this, "", "Please wait...", true);
                new FollowedAsyncTask().execute();
            }
        });
        dontfollowBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (outsourcing == null) {
                    outsourcing = getResources().getStringArray(R.array.outsourcing);
                    for (String anomaly_percent : outsourcing) {
                        String[] anomaly_array = anomaly_percent.split(",");
                        String[] percentages = {anomaly_array[1],anomaly_array[2],anomaly_array[3]};
                        anomaly2percentages.put(anomaly_array[0],percentages);
                    }
                }
                dialog = new Dialog(AdviceReceived.this);
                dialog.setContentView(R.layout.takeactiondialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.setTitle(Html.fromHtml("<font color='#08457E'><b>Take Action</font>"));

                Button dialogDoNothing = (Button) dialog.findViewById(R.id.dialogdonothing);
                Button dialogUninstall = (Button) dialog.findViewById(R.id.dialogButtonuninstall);
                Button dialogButtonKill = (Button) dialog.findViewById(R.id.dialogButtonkill);
                Button dialogDetail = (Button) dialog.findViewById(R.id.detail_adviee_btn);
                dialogDetail.setVisibility(View.GONE);

                String[] threePercentages = (String[]) anomaly2percentages.get(anomaly);
                if (threePercentages != null) {
                    int cs_u = Integer.parseInt(threePercentages[0].substring(0, threePercentages[0].length() - 1));
                    int cs_k = Integer.parseInt(threePercentages[1].substring(0, threePercentages[1].length() - 1));
                    int cs_d = Integer.parseInt(threePercentages[2].substring(0, threePercentages[2].length() - 1));

                    int flag = -1;
                    if (cs_u > cs_k && cs_u > cs_d) {
                        flag = 0;
                        dialogUninstall.setBackgroundColor(0xFFCC0000);
                    } else if (cs_k > cs_u && cs_k > cs_d) {
                        flag = 1;
                        dialogButtonKill.setBackgroundColor(0xFFCC0000);
                    } else if (cs_d > cs_u && cs_d > cs_k) {
                        flag = 2;
                        dialogDoNothing.setBackgroundColor(0xFFCC0000);
                    }

                    String desc = getString(R.string.os_desc);
                    dialogUninstall.setText(dialogUninstall.getText() + "\n(" + threePercentages[0] + " " + desc);
                    dialogDoNothing.setText(dialogDoNothing.getText() + "\n(" + threePercentages[2] + " " + desc);
                    dialogButtonKill.setText(dialogButtonKill.getText() + "\n(" + threePercentages[1] + " " + desc);
                }

                TextView tv = (TextView) dialog.findViewById(R.id.text);
                ImageView image = (ImageView) dialog.findViewById(R.id.app);
                Helper.setAppImage(image, appname);
                tv.setText(Html.fromHtml("<font color=#08457E>What action do you want to take?</font>"));
                dialogDoNothing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbManager.updateAdviceReceived(anomalyid);
                        progressDialog = ProgressDialog.show(AdviceReceived.this, "", "Please wait...", true);
                        new SendDetailsOwnAction("Do nothing").execute();
                    }
                });
                dialogUninstall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbManager.updateAdviceReceived(anomalyid);
                        progressDialog = ProgressDialog.show(AdviceReceived.this, "", "Please wait...", true);
                        new SendDetailsOwnAction("Uninstall").execute();
                    }
                });
                dialogButtonKill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbManager.updateAdviceReceived(anomalyid);
                        progressDialog = ProgressDialog.show(AdviceReceived.this, "", "Please wait...", true);
                        new SendDetailsOwnAction("Kill").execute();
                    }
                });
                if (dialog != null && !dialog.isShowing())
                    dialog.show();
            }
        });
    }

    class FollowedAsyncTask extends AsyncTask<String, Void, Boolean> {

        public FollowedAsyncTask() {}

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final UtilityClass utility = new UtilityClass(getApplicationContext());
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/advicefollow.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    String phone = utility.getLoginPhone();
                    utility.serverInteraction(urlString, phone + ":" + anomalyid + ":" + anomaly);
                } else {
                    AdviceReceived.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(AdviceReceived.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
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
            dialog = new Dialog(AdviceReceived.this);
            dbManager.updateReadDepndnt(date);
            dialog.setContentView(R.layout.actioncompleteddialog);
            dialog.setTitle(Html.fromHtml("<font color='#08457E'><b>Action Completed!</font>"));
            TextView replyTV = (TextView) dialog.findViewById(R.id.reply);
            Button okbtn = (Button) dialog.findViewById(R.id.ok);
            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdviceReceived.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            if (reply.equals("uninstall")) {
                action = "Please uninstall the application manually.";
            } else if (reply.equals("kill")) {
                action = "Please kill the application manually.";
            } else {
                action = "Nothing has been done to the application.";
            }
            replyTV.setText(action);
            if (dialog != null && !dialog.isShowing())
                dialog.show();
        }

    }

    class SendDetailsOwnAction extends AsyncTask<String, Void, Boolean> {
        private String mResponse;

        public SendDetailsOwnAction(String response) {
            mResponse = response;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final UtilityClass utility = new UtilityClass(getApplicationContext());
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/ownaction1.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    String phone = utility.getLoginPhone();
                    utility.serverInteraction(urlString, anomalyid + ":" + mResponse + ":" + anomaly + ":" + phone);
                } else {
                    AdviceReceived.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(AdviceReceived.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
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
            dialog1 = new Dialog(AdviceReceived.this);
            dialog1.setContentView(R.layout.actioncompleteddialog);
            dialog1.setTitle(Html.fromHtml("<font color='#08457E'><b>Action Completed!</font>"));
            dbManager.updateReadDepndnt(date);
            Button okbtn = (Button) dialog1.findViewById(R.id.ok);
            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdviceReceived.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            TextView replyTV = (TextView) dialog1.findViewById(R.id.reply);
            String text = null;
            if (mResponse.equals("Do nothing")) {
                text = "Nothing has been done to the application.";
            } else if (mResponse.equals("Uninstall")) {
                text = "Please uninstalled the application manually.";
            } else if (mResponse.equals("Kill")) {
                text = "Please kill the application manually.";
            }
            replyTV.setText(text);
            if (dialog1 != null && !dialog1.isShowing())
                dialog1.show();
        }
    }
}
