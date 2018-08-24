package com.smu.appmod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DeleteActivity extends Activity implements OnClickListener {

    Button ok_btn;
    String name;
    TextView tv;
    UtilityClass utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setTitle("Anomaly");
        setContentView(R.layout.dialogactivity1);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        ok_btn = (Button) findViewById(R.id.ok_btn_id);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText(name + " has deleted you as an advisee.");
        ok_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn_id:
                Intent intent = new Intent(DeleteActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        }
    }
}

