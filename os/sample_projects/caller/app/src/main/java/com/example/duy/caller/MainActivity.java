package com.example.duy.caller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ActivityManager;

import java.io.File;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        final String currentPackageName = this.getApplicationContext().getPackageName();
        final Button open_app_button = findViewById(R.id.open_app);
        open_app_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText text = (EditText) findViewById(R.id.display_box);

                text.setText("Running " + currentPackageName);
//
//                Intent i = new Intent(Intent.ACTION_MAIN);
//                i.addCategory(Intent.CATEGORY_LAUNCHER);
//                i.setPackage("com.android.calculator2");
//                startActivity(i);

                Intent launchIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.smu.appmod");
                if (launchIntent != null) {
                    launchIntent.putExtra("target_app", currentPackageName);
                    //startActivity(launchIntent);//null pointer check in case package name was not found
                    getApplicationContext().getApplicationContext().startActivity(launchIntent);
                }
            }
        });
        final Button do_one_but = findViewById(R.id.doone);
        do_one_but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText text = (EditText) findViewById(R.id.display_box);
                text.setText("DO_ONE is clicked!");
                int cnt = 20;
                StringBuilder b = new StringBuilder();
                while (cnt-- > 0) {
                    BigInteger.valueOf(cnt);
                    b.append(cnt);
                    System.out.println("DUY caller: math is executed! pid:" + Process.myPid() + " tid:" + Process.myTid());
                }
                File model_folder = new File("/sdcard/appmod_models");

                try {
                    text.setText("DO_ONE -> " + model_folder.listFiles().length);
                } catch (Exception e) {
                    text.setText("DO_ONE -> " + model_folder.isDirectory());
                }

            }
        });

        /**********************************************************************/
        final MainActivity main = this;
        /**********************************************************************/

        final Button do_two_button = findViewById(R.id.DOTWO);
        do_two_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText text = (EditText) findViewById(R.id.display_box);
                final Locationer locObj = new Locationer(main);

                text.setText("-> " + locObj.getLatitude() + " " + locObj.getLongitude());

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
