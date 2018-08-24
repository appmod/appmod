package com.smu.appmod;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class WithdrawActivity extends Activity implements OnClickListener {
    Button ok_btn, cancel_btn;
    TextView tv;
    UtilityClass utility;
    String phone;
    ProgressDialog progressDialog;
    Dialog dialog;
    private static final String TAG = "WA";

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
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setTitle("Withdraw");
        setContentView(R.layout.dialogactivity);
        ok_btn = (Button) findViewById(R.id.ok_btn_id);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText("Would you like to withdraw from the user study?");
        cancel_btn = (Button) findViewById(R.id.cancel_btn_id);
        ok_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }
    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.ok_btn_id:
                progressDialog = ProgressDialog.show(WithdrawActivity.this, "", "Please wait...", true);
                new SendDetailsOwnAction().execute();
                //this.finish();
                break;

            case R.id.cancel_btn_id:
                this.finish();
                break;
        }
    }


    class SendDetailsOwnAction extends AsyncTask<String, Void, Boolean> {
        String flag = null;

        public SendDetailsOwnAction() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final UtilityClass utility = new UtilityClass(getApplicationContext());
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/withdraw.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    String phone = utility.getLoginPhone();
                    flag = utility.serverInteraction(urlString, phone + ":" + "withdraw");
                } else {
                    WithdrawActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(WithdrawActivity.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
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
                dialog = new Dialog(WithdrawActivity.this);
                dialog.setContentView(R.layout.actioncompleteddialog);
                dialog.setTitle(Html.fromHtml("<font color='#08457E'><b>Withdrawal Completed!</font>"));
                Button okbtn = (Button) dialog.findViewById(R.id.ok);
                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                TextView replyTV = (TextView) dialog.findViewById(R.id.reply);
                String text = "You have successfully withdrawn from the user study.";
                replyTV.setText(text);
                DBManager dbManager = new DBManager(WithdrawActivity.this);
                dbManager.open();
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date()).trim();
                String query1 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                        + currentDateTimeString + "','" + "myself" + "','" + text + "')";
                dbManager.executeQuery(query1);
                final UtilityClass u = new UtilityClass(getApplicationContext());
                u.setWithdraw();
                if (dialog != null && !dialog.isShowing())
                    dialog.show();
            }
        }
    }
}


