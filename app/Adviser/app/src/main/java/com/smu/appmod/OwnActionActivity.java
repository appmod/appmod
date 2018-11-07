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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OwnActionActivity extends Activity {
    /**
     * BELOW two parameters are added for outsourcing data.
     */
    static String[] outsourcing = null;
    static Map anomaly2percentages = new HashMap<String, String[]>();
    static String[] detail = null;
    static Map anomaly2detail = new HashMap<String, String[]>();
    Button doNothing, uninstall, kill, detailBtn;
    TextView text;
    UtilityClass utility;
    static String phone, date;
    ProgressDialog progressDialog;
    static String message, anomalyid;
    private static final String TAG = "OAA";
    ImageView image;

    Dialog dialog;
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


                dialog = new Dialog(OwnActionActivity.this);
                dialog.setContentView(R.layout.detaildialog_advisee);
                dialog.setTitle(Html.fromHtml("<font color='#08457E'><b>Details of the Anomaly</font>"));


                WebView webviewAbout = (WebView) dialog.findViewById(R.id.google_play);
                webviewAbout.setWebViewClient(new WebViewClient());
                if (id_context != null) {
                    webviewAbout.loadUrl("https://play.google.com/store/apps/details?id=" + id_context[0] + "&hl=en");
                }
                if (dialog != null && !dialog.isShowing())
                    dialog.show();
            }
        });

        final DBManager dbManager;
        dbManager = new DBManager(OwnActionActivity.this);
        dbManager.open();
        doNothing = (Button) findViewById(R.id.dialogdonothing);
        uninstall = (Button) findViewById(R.id.dialogButtonuninstall);
        kill = (Button) findViewById(R.id.dialogButtonkill);

        String[] threePercentages = (String[]) anomaly2percentages.get(message);
        if (threePercentages != null) {
            int cs_u = Integer.parseInt(threePercentages[0].substring(0, threePercentages[0].length() - 1));
            int cs_k = Integer.parseInt(threePercentages[1].substring(0, threePercentages[1].length() - 1));
            int cs_d = Integer.parseInt(threePercentages[2].substring(0, threePercentages[2].length() - 1));

            int flag = -1;
            if (cs_u > cs_k && cs_u > cs_d) {
                flag = 0;
                uninstall.setBackgroundColor(0xFFCC0000);
            } else if (cs_k > cs_u && cs_k > cs_d) {
                flag = 1;
                kill.setBackgroundColor(0xFFCC0000);
            } else if (cs_d > cs_u && cs_d > cs_k) {
                flag = 2;
                doNothing.setBackgroundColor(0xFFCC0000);
            }
            String desc = getString(R.string.os_desc);
            uninstall.setText(uninstall.getText() + "\n(" + threePercentages[0] + " " + desc);
            doNothing.setText(doNothing.getText() + "\n(" + threePercentages[2] + " " + desc);
            kill.setText(kill.getText() + "\n(" + threePercentages[1] + " " + desc);
        }


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


