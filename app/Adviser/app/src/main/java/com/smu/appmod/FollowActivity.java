package com.smu.appmod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FollowActivity extends Activity implements OnClickListener {

    Button ok_btn;
    String text;
    TextView tv;
    UtilityClass utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setTitle("Feedback");
        setContentView(R.layout.dialogactivity1);
        Intent intent = getIntent();
        text = intent.getStringExtra("text");
        text = text.split("Click")[0].trim();
        ok_btn = (Button) findViewById(R.id.ok_btn_id);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText(Html.fromHtml(text + " You can click on <b>Logs</b> button for more details."));
        ok_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn_id:
                Intent intent = new Intent(FollowActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        }
    }
}

