package com.smu.appmod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DialogActivity extends Activity implements OnClickListener {

    Button ok_btn, cancel_btn;
    String date, message;
    TextView tv;
    UtilityClass utility;
    static String adviserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    Intent intt = new Intent(getApplicationContext(), GetAdvice.class);
                    intt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intt.putExtra("date", date);
                    intt.putExtra("message", message);
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