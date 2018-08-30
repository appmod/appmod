package com.smu.appmod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.Date;
import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends Activity {
    Button agree_btn;
    EditText phone_edit;
    UtilityClass utility;
    static String phone, role;
    private PrefManager prefManager;
    RadioGroup radGroup;
    RadioButton radioButton;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    private DBManager dbManager;
    ProgressDialog progressDialog;
    static final int ACCOUNTS = 0x6;
    private boolean permissionFlag = false;
    private static final String TAG = "LA";

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        setContentView(R.layout.login);
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>AppMod </font>"));
        dbManager = new DBManager(this);
        dbManager.open();
        utility = new UtilityClass(getApplicationContext());
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(UtilityClass.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //this code is for user study only.
                    if (sharedPreferences.getBoolean(UtilityClass.REG_SUCCESS, false)){
                        Toast.makeText(LoginActivity.this, "Login failed. Please input the unique ID we send you via email.", Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    } else {
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }

                        String phone = utility.getLoginPhone();
                        String prefix = phone.substring(0, 3);


                        if (utility.getRole().trim().equals("Dependant")) {
                            String query1 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                                    + currentDateTimeString + "','" + "myself" + "','" + "Registered successfully. Wait till your advisor sends you an approval request for getting paired up." + "')";
                            dbManager.executeQuery(query1);

                            String adviserName = prefix + "b";
                            String query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                                    + currentDateTimeString + "','" + "myself" + "','" + "You have approved " + adviserName.trim() + " as your advisor." + "')";
                            dbManager.executeQuery(query2);
                            utility.setAdviser(true);

                            utility.setAdviserName(adviserName);
                        }
                        else if(utility.getRole().trim().equals("Adviser")){
                            String phoneDependant = prefix + "a";
                            String nameDependant = "AdviseeForUserStudy";
                            String status = getResources().getString(R.string.paired);
                            String text = getResources().getString(R.string.notif_sent_request);

                            String query = "INSERT INTO DEPENDANTS(name,phone,unread, status) values ('"
                                    + nameDependant + "','" + phoneDependant + "',1,'" + status + "')";
                            dbManager.executeQuery(query);

                            String query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                                    + currentDateTimeString + "','" + nameDependant + "','" + "You added " + nameDependant + " as one of your advisees. " + text + "')";
                            dbManager.executeQuery(query2);

                        }

                        launchHomeScreen();
                    }

//                    if (sharedPreferences.getBoolean(UtilityClass.REG_SUCCESS, false)) {
//                        Toast.makeText(LoginActivity.this, "Registration failed. User already exists", Toast.LENGTH_SHORT).show();
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                            progressDialog = null;
//                        }
//                    } else {
//                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                            progressDialog = null;
//                        }
//                        if (utility.getRole().trim().equals("Dependant")) {
//                            String query1 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
//                                    + currentDateTimeString + "','" + "myself" + "','" + "Registered successfully. Wait till your advisor sends you an approval request for getting paired up." + "')";
//                            dbManager.executeQuery(query1);
//                        }
//                        launchHomeScreen();
//                    }
                } else {
                    Toast.makeText(LoginActivity.this,"Please check internet connectivity and then try again!", Toast.LENGTH_LONG).show();
                }
            }
        };
        TextView termsConditions = (TextView) findViewById(R.id.termsConditions);
        termsConditions.setText(Html.fromHtml(" <br /><u>Terms and Conditions</u>"));
        termsConditions.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, TermsAndConditionsActivity.class));
            }
        });
        //country_code = (EditText) findViewById(R.id.countryCode);
        //String zipCode = utility.getCountryZipCode();
        //country_code.setText(zipCode);
        phone_edit = (EditText) findViewById(R.id.phone_edit);
        phone_edit.requestFocus();
        radGroup = (RadioGroup) findViewById(R.id.radiogrp);
        agree_btn = (Button) findViewById(R.id.agree_btn);
        agree_btn.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View view) {
                                             int selectedId = radGroup.getCheckedRadioButtonId();
                                             radioButton = (RadioButton) findViewById(selectedId);
                                             //String countrycode = country_code.getText().toString();
                                             phone = phone_edit.getText().toString().trim();
                                             //if (!countrycode.startsWith("+"))
                                             //    countrycode = "+" + countrycode;
                                             //if (!countrycode.equals("+65")) {
                                             //    Toast.makeText(getApplicationContext(),
                                             //            "Only Singapore (+65) phone numbers are accepted. Please try again.", Toast.LENGTH_SHORT).show();
                                             //} else
//                                             if (phone.length() != 4 || !TextUtils.isDigitsOnly(phone)) {
//                                                 Toast.makeText(getApplicationContext(),
//                                                         "Only last 4 digits of your phone number is required. Please try again.", Toast.LENGTH_SHORT).show();
//                                             }
                                             if (phone.length() != 3 || !TextUtils.isDigitsOnly(phone)) {
                                                 Toast.makeText(getApplicationContext(),
//                                                         "Only last 4 digits of your phone number is required. Please try again.", Toast.LENGTH_SHORT).show();
                                                         "Only 3 digits is required. Please try again.", Toast.LENGTH_SHORT).show();
                                             }
                                             else if ((!phone.equals("")) && (radioButton != null)) {
                                                 //phone = phone_edit.getText().toString();
                                                 role = radioButton.getText().toString();
                                                 if (role.startsWith("Advisee")) {
                                                     role = "Dependant";
                                                 } else if (role.startsWith("Advisor")) {
                                                     role = "Adviser";
                                                 }
                                                 if (phone.matches("[0-9]+") || phone.matches("(.*)+(.*)")) {
                                                     if("Dependant".equals(role)){
                                                         phone = phone + "a";
                                                     }
                                                     else if("Adviser".equals(role)){
                                                         phone = phone + "b";
                                                     }
                                                     utility.createUserLoginSession(phone, role);
                                                     progressDialog = ProgressDialog.show(LoginActivity.this, "", "Registering. Please wait...", true);
                                                     askForPermission(READ_CONTACTS,ACCOUNTS);
                                                     //startRegistrationService(true, false);
                                                 } else {
                                                     Toast.makeText(getApplicationContext(),
                                                             "Phone number should be numeric.", Toast.LENGTH_SHORT).show();
                                                 }
                                             } else if ((!phone.equals(""))) {
                                                 Toast.makeText(getApplicationContext(),
                                                         "Please choose role.", Toast.LENGTH_SHORT).show();
                                             } else if (radioButton != null) {
                                                 Toast.makeText(getApplicationContext(),
                                                         "Please provide phone number.", Toast.LENGTH_SHORT).show();
                                             } else {
                                                 Toast.makeText(getApplicationContext(),
                                                         "Please provide phone number and role.", Toast.LENGTH_SHORT).show();
                                             }
                                         }
                                     }
        );
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(UtilityClass.REGISTRATION_COMPLETE));
        //String zipCode = utility.getCountryZipCode();
        //country_code.setText(zipCode);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void startRegistrationService(boolean reg, boolean tkr) {
        if (utility.checkPlayServices(this)) {
            Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            intent.putExtra("register", reg);
            String userdetails = utility.getUserDetails();
            intent.putExtra("userdetails", userdetails);
            intent.putExtra("tokenRefreshed", tkr);
            startService(intent);
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            /*
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permission)) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);
            }
            */
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            startRegistrationService(true, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case ACCOUNTS:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        Toast.makeText(LoginActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                        startRegistrationService(true, false);
                    } else {
                        // Permission Denied
                        Toast.makeText(LoginActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                    return;
            }
        }
    }
}


