package com.smu.appmod;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TARGET_APP = "target_app";
    private String target_app = null;

    public boolean killApp(String packageName) {
//        PackageManager pm = this.getApplicationContext().getPackageManager();

        ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(startMain);

        am.killBackgroundProcesses(packageName);

        //get a list of installed apps.

//        ActivityManager mActivityManager = (ActivityManager) MainActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
//        String myPackage = getApplicationContext().getPackageName();
//        for (ApplicationInfo packageInfo : pm.getInstalledApplications(0)) {
//
//            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
//                continue;
//            }
//            if (packageInfo.packageName.equals(myPackage)) {
//                continue;
//            }
//            if (packageInfo.packageName.compareTo(packageName) == 0) {
//                mActivityManager.killBackgroundProcesses(packageInfo.packageName);
//            }
//
//        }
        boolean flag = true;

//        for (ActivityManager.RunningAppProcessInfo activity : mActivityManager.getRunningAppProcesses()) {
//            if (activity.processName.compareTo(packageName)==0) {
//                System.out.println("DEBUG: checking "+activity.processName.toString()+" "+activity.pid);
//                android.os.Process.sendSignal(activity.pid, android.os.Process.SIGNAL_KILL);
//                android.os.Process.killProcess(activity.pid);
//                mActivityManager.killBackgroundProcesses(packageName);
//                flag = false;
//            }
//        }
        return flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        if (intent.hasExtra(TARGET_APP)) {
            target_app = (String) intent.getExtras().get(TARGET_APP);
            EditText thebox = findViewById(R.id.thebox);
            thebox.setText("DEBUG: Called by " + target_app);
        }

        Button donothing_button = findViewById(R.id.donothing);
        donothing_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });


        Button kill_button = findViewById(R.id.kill_button);
        kill_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (target_app != null) {
                    System.out.println("DEBUG: start killing " + target_app);

                    while (!killApp(target_app)) {
                        System.out.println("DEBUG: keep killing " + target_app);
                    }
//                    finish();
//                    System.exit(0);

                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.smu.appmod");
                    if (launchIntent != null) {

                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                } else {
                    EditText thebox = findViewById(R.id.thebox);
                    thebox.setText("DEBUG: Cannot find " + target_app);
                }
            }
        });

        Button uninstall_button = findViewById(R.id.uninstall_button);
        uninstall_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (target_app!=null){
//                    Uri packageUri = Uri.parse(target_app);
//                    Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,packageUri);
//                    startActivity(uninstallIntent);

                    Uri uri = Uri.parse("package:"+target_app);
                    Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }
}
