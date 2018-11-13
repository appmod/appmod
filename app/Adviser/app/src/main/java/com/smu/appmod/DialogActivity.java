package com.smu.appmod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DialogActivity extends Activity implements OnClickListener {

    Button ok_btn, cancel_btn;
    String date, message;
    TextView tv;
    UtilityClass utility;
    static String adviserName;
    static Set<String> ownActionSet = new HashSet<String>();
    static String[] ownactionStrs = null;

    private static final String TAG = "DA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ownactionStrs == null) {
            ownactionStrs = getResources().getStringArray(R.array.ownaction);
            for (String str : ownactionStrs) {
                ownActionSet.add(str);
            }
        }

        super.onCreate(savedInstanceState);
        getWindow().setTitle("Anomaly");
        setContentView(R.layout.dialogactivity);
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        message = intent.getStringExtra("message");
        ok_btn = (Button) findViewById(R.id.ok_btn_id);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText(message + " Please take action.");
        cancel_btn = (Button) findViewById(R.id.cancel_btn_id);
        ok_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ok_btn_id:
                utility = new UtilityClass(getApplicationContext());
                adviserName = utility.getAdviserName();
                if (!adviserName.equals("notset")) {
                    Intent intt = null;
                    if (ownActionSet.contains(message)) {
                        intt = new Intent(getApplicationContext(), OwnActionActivity.class);
                    } else {
                        intt = new Intent(getApplicationContext(), GetAdvice.class);
                    }
                    intt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intt.putExtra("date", date);
                    intt.putExtra("message", message);

                    try {
                        final UtilityClass utility = new UtilityClass(getApplicationContext());
                        final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/getAnomalyID.php";
                        if (utility.hasActiveInternetConnection("http://www.google.com")) {
                            String notification = message.split("Click")[0].trim();
                            String anomalyid = utility.serverInteraction(urlString, notification);
                            intt.putExtra("anomalyid", anomalyid);
                        } else {
                            DialogActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast toast = Toast.makeText(DialogActivity.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "***** Error=" + e.toString());
                        return;
                    }

                    startActivity(intt);
                    this.finish();
                    break;
                } else {
                    Toast toast = Toast.makeText(DialogActivity.this, "Wait till your advisor sends you a request to be paired up.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            case R.id.cancel_btn_id:
                this.finish();
                break;
        }
    }
}