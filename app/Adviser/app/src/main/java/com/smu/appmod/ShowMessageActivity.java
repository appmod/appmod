package com.smu.appmod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.net.Uri;

public class ShowMessageActivity extends Activity implements OnClickListener {

    Button ok_btn;
    String text;
    TextView tv;
    UtilityClass utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setTitle("Message");
        setContentView(R.layout.dialogactivity1);
        Intent intent = getIntent();
        text = intent.getStringExtra("message");
        ok_btn = (Button) findViewById(R.id.ok_btn_id);
        tv = (TextView) findViewById(R.id.textView1);
        if (text.contains("Thank you") && text.contains("user survey")) {
            ok_btn.setText("Go to survey");
        }
        tv.setText(Html.fromHtml(text));
        ok_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn_id:
                if (text.contains("Thank you") && text.contains("user survey")) {
                    String url = "https://docs.google.com/forms/d/1AhcKkGAdu1tvPG2iolmweOnusRNYAXMo94bLumcpzog";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                this.finish();
                break;
        }
    }
}

