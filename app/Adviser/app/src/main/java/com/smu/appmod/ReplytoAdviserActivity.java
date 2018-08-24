package com.smu.appmod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ReplytoAdviserActivity extends Activity implements OnClickListener {

    Button ok_btn;
    String msg;
    TextView tv;
    UtilityClass utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setTitle("Anomaly");
        setContentView(R.layout.dialogactivity1);
        Intent intent = getIntent();
        msg = intent.getStringExtra("msg");
        ok_btn = (Button) findViewById(R.id.ok_btn_id);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText(msg);
        ok_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn_id:
                Intent intent = new Intent(ReplytoAdviserActivity.this, ManageDependants.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        }
    }
}

