package com.smu.appmod;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageDependants extends Activity implements View.OnClickListener, DependantInterface {
    UtilityClass utility;
    static Cursor phones;
    List<Pair<String, String>> contactList;
    static ListView contactlistView;
    static ArrayList<Contact> contactArrayList;
    static SelectContactAdapter contactAdapter;
    Button addDependants;
    ContentResolver resolver;
    EditText search;
    Dialog dialog;
    ProgressDialog progressDialog;
    Button backbtn;
    private DBManager dbManager;
    private ListView listView;
    private TextView emptyTV;
    private SwipeRefreshLayout swipeContainer;
    private static final String TAG = "MD";
    private long mLastClickTime = 0;

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
        setContentView(R.layout.manage_dependants);
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Manage Advisees</font>"));
        utility = new UtilityClass(getApplicationContext());
        dialog = new Dialog(this);
        addDependants = (Button) findViewById(R.id.addDependants);
        addDependants.setOnClickListener(this);
        dbManager = new DBManager(this);
        dbManager.open();
        listView = (ListView) findViewById(R.id.dependants_list);
        emptyTV = (TextView) findViewById(R.id.empty);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_green_light);
        backbtn = (Button) findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ManageDependants.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }}
        );
        showList();
    }

    public void fetchTimelineAsync(int page) {
        refresh();
        swipeContainer.setRefreshing(false);
    }

    public void refresh() {
        showList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ManageDependants.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        showList();
    }

    @Override
    public void callAddDependantAsyncTask(String dependantName) {
        String phone = null;
        String query = "SELECT * FROM DEPENDANTS where name='" + dependantName + "'";
        Cursor c1 = dbManager.selectQuery(query);
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    phone = c1.getString(c1.getColumnIndex("phone")).trim();
                    phone = phone.substring(phone.length() - 4);
                } while (c1.moveToNext());
            }
        }
        if (c1 != null)
            c1.close();
        progressDialog = ProgressDialog.show(ManageDependants.this, "", "Adding. Please wait...", true);
        //new AddDependantAsyncTask(dependantName, phone, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //Toast.makeText(ManageDependants.this, "Adding...Please wait...", Toast.LENGTH_SHORT).show();
        new AddDependantAsyncTask(dependantName, phone, false).execute();
    }

    @Override
    public void callDeleteDependantAsyncTask(String dependantName) {
        String phone = null;
        String query = "SELECT * FROM DEPENDANTS where name='" + dependantName + "'";
        Cursor c1 = dbManager.selectQuery(query);
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    phone = c1.getString(c1.getColumnIndex("phone")).trim();
                    phone = phone.substring(phone.length() - 4);
                } while (c1.moveToNext());
            }
        }
        if (phone != null) {
            progressDialog = ProgressDialog.show(ManageDependants.this, "", "Deleting. Please wait...", true);
            //Toast.makeText(ManageDependants.this, "Deleting...Please wait...", Toast.LENGTH_SHORT).show();
            new DeleteDependantAsyncTask(dependantName, phone).execute();
            try {
                long sec = 1000;
                Thread.sleep(sec);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            finish();
            Intent intent = new Intent(ManageDependants.this, ManageDependants.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if (c1 != null)
            c1.close();
    }

    private void showList() {
        ArrayList<Dependant> depenList = new ArrayList<Dependant>();
        depenList.clear();
        String query = "SELECT * FROM DEPENDANTS";
        Cursor c1 = dbManager.selectQuery(query);
        if (c1 != null && c1.getCount() != 0) {
            emptyTV.setVisibility(View.GONE);
            if (c1.moveToFirst()) {
                do {
                    Dependant depenItem = new Dependant();
                    depenItem.setDependantName(c1.getString(c1.getColumnIndex("name")));
                    depenItem.setDependantPhone(c1.getString(c1.getColumnIndex("phone")));
                    depenItem.setDependantStatus(c1.getString(c1.getColumnIndex("status")));
                    depenList.add(depenItem);
                } while (c1.moveToNext());
            }
        } else {
            emptyTV.setText(R.string.noDependants);
        }
        if (c1 != null)
            c1.close();

        DependantListAdapter depenListAdapter = new DependantListAdapter(ManageDependants.this, depenList);
        depenListAdapter.setCallback(this);
        listView.setAdapter(depenListAdapter);
    }

    @Override
    public void onClick(View view) {
//        if (view.getId() == R.id.addDependants) {
//            String query = "SELECT * FROM DEPENDANTS";
//            Cursor c1 = dbManager.selectQuery(query);
//            if (c1 != null && c1.getCount() < 3) {
//                dialog.setContentView(R.layout.displaycontacts);
//                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//                dialog.setTitle(Html.fromHtml("<font color='#E32636'><b>Add an Advisee</font>"));
//                contactArrayList = new ArrayList<Contact>();
//                contactList = new ArrayList<>();
//                resolver = this.getContentResolver();
//                contactlistView = (ListView) dialog.findViewById(R.id.contacts_list);
//                phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//                if (phones != null) {
//                    if (phones.getCount() == 0) {
//                        Toast.makeText(ManageDependants.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
//                    } else {
//                        progressDialog = ProgressDialog.show(ManageDependants.this, "", "Deleting. Please wait...", true);
//                        //Toast.makeText(ManageDependants.this, "Deleting...Please wait...", Toast.LENGTH_SHORT).show();
//                        while (phones.moveToNext()) {
//                            final Contact contact = new Contact();
//                            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                            name = name.replaceAll("[^a-zA-Z0-9]+", " ");
//                            name = name.replaceAll("\\s", " ");
//                            phoneNumber = phoneNumber.replaceAll("\\s", "");
//                            if (contactList.contains(new Pair<String,String>(name, phoneNumber))) {
//                            } else {
//                                if (!name.equals("")) {
//                                    contactList.add(new Pair<>(name, phoneNumber));
//                                    contact.setName(name);
//                                    contact.setPhone(phoneNumber);
//                                    contact.setCheckedBox(false);
//                                    contactArrayList.add(contact);
//                                }
//                            }
//                        }
//                    }
//                    if (progressDialog != null) {
//                        progressDialog.dismiss();
//                        progressDialog = null;
//                    }
//                }
//                phones.close();
//                contactAdapter = new SelectContactAdapter(contactArrayList, ManageDependants.this);
//                contactlistView.setAdapter(contactAdapter);
//                contactlistView.setFastScrollEnabled(true);
//                search = (EditText) dialog.findViewById(R.id.searchView);
//                search.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void afterTextChanged(Editable arg0) {
//                        String text = search.getText().toString().toLowerCase(Locale.getDefault());
//                        contactAdapter.filter(text);
//                    }
//                    @Override
//                    public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {
//                    }
//                    @Override
//                    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//                    }
//                });
//                Button dialogButtonAdd = (Button) dialog.findViewById(R.id.dialogButtonAdd);
//                Button dialogButtonBack = (Button) dialog.findViewById(R.id.dialogButtonBack);
//                dialogButtonAdd.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                            return;
//                        }
//                        mLastClickTime = SystemClock.elapsedRealtime();
//
//                        ArrayList<Contact> selectedList = contactArrayList;
//                        for (int i = 0; i < contactArrayList.size(); i++) {
//                            Contact data = selectedList.get(i);
//                            if (data.getCheckedBox()) {
//                                String nameDependant = data.getName();
//                                String phoneDependant = data.getPhone().trim();
//                                phoneDependant = phoneDependant.substring(phoneDependant.length() - 4);
//                                String phone = null;
//                                String query = "SELECT * FROM DEPENDANTS where name='" + nameDependant + "'";
//                                Cursor c1 = dbManager.selectQuery(query);
//                                if (c1 != null && c1.getCount() != 0) {
//                                    if (c1.moveToFirst()) {
//                                        do {
//                                            phone = c1.getString(c1.getColumnIndex("phone"));
//                                            phone = phone.trim();
//                                        } while (c1.moveToNext());
//                                    }
//                                }
//                                if (c1 != null)
//                                    c1.close();
//                                String loginPhone = utility.getLoginPhone();
//                                if (loginPhone.equals(phoneDependant)) {
//                                    Toast.makeText(ManageDependants.this, "You cannot add yourself as advisee.", Toast.LENGTH_SHORT).show();
//                                } else if (phone != null && phone.equals(phoneDependant)) {
//                                    Toast.makeText(ManageDependants.this, "You cannot add same advisee again.", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    progressDialog = ProgressDialog.show(ManageDependants.this, "", "Adding. Please wait...", true);
//                                    //Toast.makeText(ManageDependants.this, "Adding...Please wait...", Toast.LENGTH_SHORT).show();
//                                    //new AddDependantAsyncTask(nameDependant, phoneDependant, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                                    new AddDependantAsyncTask(nameDependant, phoneDependant, true).execute();
//                                    try {
//                                        long sec = 1000;
//                                        Thread.sleep(sec);
//                                    } catch (Exception e) {
//                                        Log.e(TAG, e.toString());
//                                    }
//                                    finish();
//                                    Intent intent = new Intent(ManageDependants.this, ManageDependants.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(intent);
//                                    //showList();
//                                }
//                            }
//                        }
//                        finish();
//                        Intent intent = new Intent(ManageDependants.this, ManageDependants.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//
//                    }
//                });
//                dialogButtonBack.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (dialog != null) {
//                            dialog.dismiss();
//                            dialog = null;
//                        }
//                        recreate();
//                        showList();
//                    }
//                });
//                if (dialog != null && !dialog.isShowing())
//                    dialog.show();
//            } else {
//                Toast.makeText(ManageDependants.this, "You can only add at most 3 advisees.", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    class AddDependantAsyncTask extends AsyncTask<String, Void, Boolean> {
        String flag = null;
        String nameDependant, phoneDependant;
        Boolean updateFlag;

        public AddDependantAsyncTask(String name, String phone, Boolean updateListFlag) {
            nameDependant = name;
            phoneDependant = phone;
            updateFlag = updateListFlag;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean addfinish = true;
            try {
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/addDependant.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    String loginPhone = utility.getLoginPhone();
                    flag = utility.serverInteraction(urlString, loginPhone + ":" + nameDependant + ":" + phoneDependant).trim();
                } else {
                    ManageDependants.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(ManageDependants.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "***** Error=" + e.toString());
            }
            return addfinish;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //showList();
            String status = null;
            String text = null;
            String value = null;
            if (flag != null) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(flag);
                    value = jsonObj.getString("success");
                } catch (JSONException e) {
                    Log.e(TAG, "***** Error=" + e.toString());
                }
                if (value != null && value.equals("1")) {
                    status = getResources().getString(R.string.sent_request);
                    text = getResources().getString(R.string.notif_sent_request);
                } else if (flag.contains("notregistered")) {
                    status = getResources().getString(R.string.not_registered);
                    text = getResources().getString(R.string.notif_not_registered);
                    String loginPhone = utility.getLoginPhone();
                    //new CheckRegistnAsyncTask(phoneDependant, loginPhone).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    new CheckRegistnAsyncTask(phoneDependant, loginPhone).execute();
                } else if (value != null && value.equals("0")) {
                    status = getResources().getString(R.string.network_problem);
                    text = getResources().getString(R.string.notif_network_problem);
                }
                if (updateFlag) {
                    String query = "INSERT INTO DEPENDANTS(name,phone,unread, status) values ('"
                            + nameDependant + "','" + phoneDependant + "',0,'" + status + "')";
                    dbManager.executeQuery(query);
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    String query1 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                            + currentDateTimeString + "','" + nameDependant + "','" + "You added " + nameDependant + " as one of your advisees. " + text + "')";
                    dbManager.executeQuery(query1);
                    showList();
                } else {
                    String query = "UPDATE DEPENDANTS set status='"+ status + "' WHERE name ='" + nameDependant + "'";
                    dbManager.executeQuery(query);
                    showList();
                    Toast toast = Toast.makeText(ManageDependants.this, "Request Sent!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        }
    }

    class DeleteDependantAsyncTask extends AsyncTask<String, Void, Boolean> {
        String flag = null;
        String nameDependant, phoneDependant;

        public DeleteDependantAsyncTask(String name, String phone) {
            nameDependant = name;
            phoneDependant = phone;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/deleteDependant.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    String loginPhone = utility.getLoginPhone();
                    flag = utility.serverInteraction(urlString, loginPhone + ":" + nameDependant + ":" + phoneDependant).trim();
                } else {
                    ManageDependants.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(ManageDependants.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
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
            String text = null;
            String value = null;
            if (flag != null) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(flag);
                    value = jsonObj.getString("success");
                } catch (JSONException e) {
                    Log.e(TAG, "***** Error=" + e.toString());
                }
                if ((value != null && value.equals("1")) || flag.contains("notregistered")) {
                    String query1 = "DELETE FROM DEPENDANTS WHERE phone ='" + phoneDependant + "'";
                    dbManager.executeQuery(query1);
                    text = getResources().getString(R.string.deleted);
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    String query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                            + currentDateTimeString + "','" + nameDependant + "','" + "You deleted " + nameDependant + " as one of your advisees. " + text + "')";
                    dbManager.executeQuery(query2);
                } else if (value != null && value.equals("0")) {
                    Toast toast = Toast.makeText(ManageDependants.this, "The advisee could not be deleted. Please try again later!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                //showList();
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        }
    }

    class CheckRegistnAsyncTask extends AsyncTask<String, Void, Boolean> {
        String flag = null;
        String loginphone, phoneDependant;

        public CheckRegistnAsyncTask(String phoneDepnt, String phone) {
            phoneDependant = phoneDepnt;
            loginphone = phone;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                final String urlString = "http://flyer.sis.smu.edu.sg/AdviseProject/checkregistration.php";
                if (utility.hasActiveInternetConnection("http://www.google.com")) {
                    flag = utility.serverInteraction(urlString, loginphone + ":" + phoneDependant);
                } else {
                    ManageDependants.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(ManageDependants.this, "Please check internet connectivity and try again!", Toast.LENGTH_SHORT);
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
            //finish();
        }
    }
}
