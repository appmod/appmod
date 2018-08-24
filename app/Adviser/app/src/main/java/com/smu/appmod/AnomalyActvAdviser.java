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
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class AnomalyActvAdviser extends Activity implements View.OnClickListener {
    UtilityClass utility;
    Button donothing, uninstall, kill;
    static Context context;
    private DBManager dbManager;
    String anomaly, seeker_name, anomalyId;
    TextView tv1, getDetails;
    String result;
    ProgressDialog progressDialog;
    private static final String TAG = "AAA";

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
        setContentView(R.layout.helpasked);
        utility = new UtilityClass(getApplicationContext());
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Give Advice</font>"));
        Intent intent = getIntent();
        anomaly = intent.getStringExtra("anomaly");
        seeker_name = intent.getStringExtra("dependantName");
        anomalyId = intent.getStringExtra("anomalyId");
        tv1 = (TextView) findViewById(R.id.tv);
        String text = "";
        if (anomaly != null && seeker_name != null) {
            //String text = "<font color=#000000> What advice would you give to </font><font color=#CC0000>" + seeker_name + "</font><font color=#000000> for this anomaly: " + anomaly + " ?</font>";
            text = "<font color=#08457E><br>What advice would you give to </font><font color=#CC0000>" + seeker_name + "</font><font color=#08457E>?<br><br>Anomaly - </font><font color=#CC0000>" + anomaly.split("Click")[0].trim() + "</font><br>";
            tv1.setText(Html.fromHtml(text));
        }

        getDetails = (TextView) findViewById(R.id.getdetails);
        text = "<font color=#08457E>";
        String appname = anomaly.split(" ")[0].trim();
        if (appname.equalsIgnoreCase("Whatsapp")) {
            if (anomaly.contains("contacts"))
                text += "This is to allow user to search for their friends and send them messages from inside Whatsapp.";
            else if (anomaly.contains("phone"))
                text += "What Candy Crush is doing: <br>1. Start new game. <br>2. Make phone call to 61234567, which is not found in your contact list.";
            else
                text += "No further information.";
        } else if (appname.equalsIgnoreCase("Facebook")) {
            if (anomaly.contains("location"))
                text += "This is to allow user to send his/her location to their friends from inside Facebook.";
            else
                text += "No further information.";
        } else if (appname.equalsIgnoreCase("Gmail")) {
            if (anomaly.contains("calendar"))
                text += "This is to allow user to access and modify calendar events from inside Gmail.";
            else if (anomaly.contains("emails"))
                text += "What Gmail is doing: <br/>1. Create an email with subject - Lottery Winner! <br/>2. Read your address book. <br/>3. Send the email to everyone in your address book.";
            else
                text += "No further information.";
        } else if (appname.equalsIgnoreCase("Instagram")) {
            if (anomaly.contains("camera"))
                text += "This is to allow user to take pictures from inside Instagram.";
            else
                text += "No further information.";
        } else if (appname.equalsIgnoreCase("YouTube")) {
            if (anomaly.contains("microphone"))
                text += "This is to allow user to search videos using voice commands from inside YouTube.";
            else
                text += "No further information.";
        } else if (appname.equalsIgnoreCase("Clock")) {
            if (anomaly.contains("geolocation"))
                text += "What Clock is doing <br/>1. Read the time and your geolocation. <br/>2. Connect to website - www.worldtime.com. <br/>3. Send your geolocation to the website.";
            else
                text += "No further information.";
        } else if (appname.equalsIgnoreCase("Sudoku")) {
            if (anomaly.contains("sim"))
                text += "What Suduko is doing: " + System.getProperty("line.separator") + " 1. Start a new game. <br/>2. Read your sim card info. <br/>3. Connect to website - www.abnormal.com. <br/>4. Send your sim card info to the website.";
            else
                text += "No further information.";
        } else if (appname.equalsIgnoreCase("Candy")) {
            if (anomaly.contains("contacts"))
                text += "What Candy Crush is doing: <br/>1. Start new game. <br/>2. Read your contacts. <br/>3. Connect to website - www.hackme.com. <br/>4. Send your contacts to the website.";
            else
                text += "No further information.";
        } else {
            text += "Unknown app - No further information.";
        }
        text += "<br></font>";
        getDetails.setText(Html.fromHtml(text));
        makeTextViewResizable(getDetails, 0, "View More", true);
        uninstall = (Button) findViewById(R.id.uninstall);
        uninstall.setOnClickListener(this);
        kill = (Button) findViewById(R.id.kill);
        kill.setOnClickListener(this);
        donothing = (Button) findViewById(R.id.donothing);
        donothing.setOnClickListener(this);
        dbManager = new DBManager(this);
        dbManager.open();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.donothing) {
            result = "Do nothing with";
        } else if (view.getId() == R.id.uninstall) {
            result = "Uninstall";
        } else if (view.getId() == R.id.kill) {
            result = "Kill";
        }
        progressDialog = ProgressDialog.show(AnomalyActvAdviser.this, "", "Sending advice. Please wait...", true);
        new SendDetailsAsyncTask().execute();
    }

    class SendDetailsAsyncTask extends AsyncTask<String, Void, Boolean> {
        String flag = null;

        public SendDetailsAsyncTask() {}

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final UtilityClass utility = new UtilityClass(getApplicationContext());
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/sendReply.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    flag = utility.serverInteraction(urlString, anomalyId + ":" + result);
                } else {
                    AnomalyActvAdviser.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(AnomalyActvAdviser.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
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
                if (value != null && value.equals("1")) {
                    dbManager.updateAdvice(result + " the application.", anomalyId);
                    Toast toast = Toast.makeText(AnomalyActvAdviser.this, "Advice Sent!", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent = new Intent(AnomalyActvAdviser.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (value != null && value.equals("0")) {
                    Toast toast = Toast.makeText(AnomalyActvAdviser.this, "Advice could not be sent. Please try again!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    tv.setText(Html.fromHtml(expandText));
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(Html.fromHtml(text));
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    //text = text.replace("\\\n", System.getProperty("line.separator"));
                    //tv.setText(text);
                    text = text.replaceAll("\\n", "<br>");
                    text = text.replaceAll("\n", "<br>");
                    tv.setText(Html.fromHtml(text));
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(Html.fromHtml(tv.getTag().toString()), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 10, "View Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(Html.fromHtml(tv.getTag().toString()), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 0, "View More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
        }
        return ssb;
    }
}
