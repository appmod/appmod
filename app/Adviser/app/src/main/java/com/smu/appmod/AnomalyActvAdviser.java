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
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import java.util.Map;
import java.util.HashMap;

public class AnomalyActvAdviser extends Activity implements View.OnClickListener {
    /**
     * BELOW two parameters are added for outsourcing data.
     */
    static String[] outsourcing = null;
    static String[] detail = null;
    static Map anomaly2percentages = new HashMap<String, String[]>();
    static Map anomaly2detail = new HashMap<String, String[]>();

    UtilityClass utility;
    Button donothing, uninstall, kill, detailBtn;
    static Context context;
    private DBManager dbManager;
    String anomaly, seeker_name, anomalyId;
    TextView tv1, getDetails;
    ScrollView scroll;
    String result;
    ProgressDialog progressDialog;
    Dialog dialog;


    private static final String TAG = "AAA";
    private String viewMoreText = "";

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
        if (outsourcing == null) {
            outsourcing = getResources().getStringArray(R.array.outsourcing);
            for (String anomaly_percent : outsourcing) {
                String[] anomaly_array = anomaly_percent.split(",");
                String[] percentages = {anomaly_array[1],anomaly_array[2],anomaly_array[3]};
                anomaly2percentages.put(anomaly_array[0],percentages);
            }
        }

        if (detail == null) {
            detail = getResources().getStringArray(R.array.detail);
            for (String detail_item : detail) {
                String[] detail_array = detail_item.split(",");
                String[] id_context = {detail_array[1],detail_array[2]};
                anomaly2detail.put(detail_array[0],id_context);
            }
        }

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
            text = "<font color=#08457E>What advice would you give to </font><font color=#CC0000>" + seeker_name + "</font><font color=#08457E>?<br><br>Anomaly - </font><font color=#CC0000>" + anomaly.split("Click")[0].trim() + "</font><br>";
            tv1.setText(Html.fromHtml(text));
        }


        detailBtn = (Button) findViewById(R.id.detail_btn);

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

                String[] id_context = (String[]) anomaly2detail.get(anomaly);


                dialog = new Dialog(AnomalyActvAdviser.this);
                dialog.setContentView(R.layout.detaildialog);
                dialog.setTitle(Html.fromHtml("<font color='#08457E'><b>Details of the Anomaly</font>"));


                if (id_context != null) {
                    WebView webviewAbout = (WebView) dialog.findViewById(R.id.google_play);
                    webviewAbout.setWebViewClient(new WebViewClient());
                    webviewAbout.loadUrl("https://play.google.com/store/apps/details?id=" + id_context[0] + "&hl=en");

                    WebView webviewContext = (WebView) dialog.findViewById(R.id.context);
                    webviewContext.setWebViewClient(new WebViewClient());
                    webviewContext.loadUrl("file:///android_asset/" + id_context[1]);
                }
                if (dialog != null && !dialog.isShowing())
                    dialog.show();
            }
        });


        uninstall = (Button) findViewById(R.id.uninstall);
        uninstall.setOnClickListener(this);
        kill = (Button) findViewById(R.id.kill);
        kill.setOnClickListener(this);
        donothing = (Button) findViewById(R.id.donothing);
        donothing.setOnClickListener(this);
        dbManager = new DBManager(this);
        dbManager.open();

        String[] threePercentages = (String[]) anomaly2percentages.get(anomaly);

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
                donothing.setBackgroundColor(0xFFCC0000);
            }

            String desc = getString(R.string.os_desc);
            uninstall.setText(uninstall.getText() + "\n(" + threePercentages[0] + " " + desc);
            donothing.setText(donothing.getText() + "\n(" + threePercentages[2] + " " + desc);
            kill.setText(kill.getText() + "\n(" + threePercentages[1] + " " + desc);
        }
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
//                    tv.setMovementMethod(new ScrollingMovementMethod());
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
//                            addClickablePartTextViewResizable(tv.getText().toString(), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
//                    tv.setText(Html.fromHtml(text));
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
//                            addClickablePartTextViewResizable(tv.getText().toString(), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    //text = text.replace("\\\n", System.getProperty("line.separator"));
                    //tv.setText(text);
                    text = text.replaceAll("\\n", "<br>");
                    text = text.replaceAll("\n", "<br>");
//                    tv.setText(Html.fromHtml(text));
                    tv.setText(text);
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
        Log.i("AppMod", str);
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(Html.fromHtml(tv.getTag().toString()), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 30, "Close", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(Html.fromHtml(tv.getTag().toString()), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 0, "Details of the anomaly", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
        }
        return ssb;
    }
}
