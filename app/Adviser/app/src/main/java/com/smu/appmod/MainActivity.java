package com.smu.appmod;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
    UtilityClass utility;
    Button manageDependants, logsBtn, withdrawBtn;
    static Context context;
    private DBManager dbManager;
    private ListView listView;
    private SwipeRefreshLayout swipeContainer;
    private TextView emptyTV;
    private static final String TAG = "MA";
    Dialog dialog, dialog1;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        utility = new UtilityClass(getApplicationContext());
        if (utility.checkLogin()) {
            finish();
        }
        if (getApplicationContext() != null) {
            NotificationManager nManager = ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
            nManager.cancelAll();
        }
        emptyTV = (TextView) findViewById(R.id.emptytv);
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));

        manageDependants = (Button) findViewById(R.id.manageDependants);
        logsBtn = (Button) findViewById(R.id.manageLogs);
        logsBtn.setOnClickListener(this);
        withdrawBtn = (Button) findViewById(R.id.withdraw);
        withdrawBtn.setOnClickListener(this);
        dbManager = new DBManager(this);
        dbManager.open();
        listView = (ListView) findViewById(R.id.notif_list);
        if (utility.isFirstTime()) {
            utility.setFirstTime();
            dialog1 = new Dialog(MainActivity.this);
            dialog1.setContentView(R.layout.notif_dialog);
            dialog1.setTitle(Html.fromHtml("<font color='#08457E'><b>Registration</font>"));
            TextView str = (TextView) dialog1.findViewById(R.id.tv);
            Button dialogButtonok = (Button) dialog1.findViewById(R.id.ok);
            if (utility.getRole().trim().equals("Adviser")) {
                dialogButtonok.setText("Manage Advisees");
                str.setText("Registered successfully. You can manage your advisees by clicking on the button below.");
                if (dialog1 != null && !dialog1.isShowing())
                    dialog1.show();
                dialogButtonok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                        startActivity(new Intent(MainActivity.this, ManageDependants.class));
                    }
                });
            } else {
                dialogButtonok.setText("Ok");
                str.setText("Registered successfully. Please wait for your advisor to send you an approval request to get paired up.");
                if (dialog1 != null && !dialog1.isShowing())
                    dialog1.show();
                dialogButtonok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog1 != null) {
                            dialog1.dismiss();
                            dialog1 = null;
                        }
                    }
                });
            }
            //dialog.show();
        }
        if (utility.getRole().trim().equals("Adviser")) {
            manageDependants.setOnClickListener(this);
            getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>AppMod: Advisor</font>"));
            showAdviserNotifList();
        } else {
            getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>AppMod: Advisee</font>"));
            manageDependants.setVisibility(View.GONE);
            LinearLayout p = (LinearLayout) findViewById(R.id.buttons);
            p.setWeightSum(2);
            showDependantNotifList();

        }
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_green_light
        );

    }

    public void fetchTimelineAsync(int page) {
        refresh();
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manageDependants:
                startActivity(new Intent(MainActivity.this, ManageDependants.class));
                break;

            case R.id.manageLogs:
                if (utility.getRole().trim().equals("Adviser")) {
                    startActivity(new Intent(MainActivity.this, ManageLogsForAdviser.class));
                } else {
                    startActivity(new Intent(MainActivity.this, ManageLogsForDpndnt.class));
                }
                break;

            case R.id.withdraw:
                if (!utility.hasWithdraw()) {
                    startActivity(new Intent(MainActivity.this, WithdrawActivity.class));
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "Your participation has already been withdrawn. Thank you.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        refresh();
    }

    public void refresh() {
        if (getApplicationContext() != null) {
            NotificationManager nManager = ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
            nManager.cancelAll();
        }
        if (utility.getRole().trim().equals("Adviser")) {
            showAdviserNotifList();
        } else {
            showDependantNotifList();
        }
    }

    private void showAdviserNotifList() {
        final ArrayList<Advice> notifList = new ArrayList<Advice>();
        notifList.clear();
        final String query = "SELECT * FROM ADVICES WHERE pending=? order by date desc";
        final Cursor c1 = dbManager.selectQueryWithArgs(query, "yes");
        if (c1 != null && c1.getCount() != 0) {
            emptyTV.setVisibility(View.GONE);
            if (c1.moveToFirst()) {
                do {
                    Advice notifItem = new Advice();
                    notifItem.setAnomaly(c1.getString(c1.getColumnIndex("anomaly")));
                    notifItem.setSeeker(c1.getString(c1.getColumnIndex("seeker_name")));
                    notifItem.setAnomalyDate(c1.getString(c1.getColumnIndex("date")));
                    notifItem.setAnomalyId(c1.getString(c1.getColumnIndex("_id")));
                    notifList.add(notifItem);
                } while (c1.moveToNext());
            }
        } else {
            emptyTV.setText(R.string.nopending);
        }
        if (c1 != null)
            c1.close();
        final AdviserNotificationAdapter notifListAdapter = new AdviserNotificationAdapter(MainActivity.this, notifList);
        listView.setAdapter(notifListAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (swipeContainer != null) {
                    if (firstVisibleItem == 0) {
                        swipeContainer.setEnabled(true);
                    } else swipeContainer.setEnabled(false);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                String name = notifList.get(position).getSeeker();
                String anomaly = notifList.get(position).getAnomaly();
                String anomalyId = notifList.get(position).getAnomalyId();
                Intent intent = new Intent(MainActivity.this, AnomalyActvAdviser.class);
                intent.putExtra("dependantName", name);
                intent.putExtra("anomaly", anomaly);
                intent.putExtra("anomalyId", anomalyId);
                startActivity(intent);
            }
        });
    }

    private void showDependantNotifList() {
        ArrayList<DependantNotifications> notifList = new ArrayList<DependantNotifications>();
        notifList.clear();
        final String query = "SELECT * FROM DEPENDANT_NOTIFICATIONS where depen_notif_read=? ORDER BY depen_notif_date DESC";
        final Cursor c1 = dbManager.selectQueryWithArgs(query, "unread");
        if (c1 != null && c1.getCount() != 0) {
            emptyTV.setVisibility(View.GONE);
            if (c1.moveToFirst()) {
                do {
                    DependantNotifications notifItem = new DependantNotifications();
                    notifItem.setRead(c1.getString(c1
                            .getColumnIndex("depen_notif_read")));
                    notifItem.setCategory(c1.getString(c1
                            .getColumnIndex("depen_notif_category")));
                    notifItem.setValue(c1.getString(c1
                            .getColumnIndex("depen_notif_value")));
                    notifItem.setDate(c1.getString(c1
                            .getColumnIndex("depen_notif_date")));
                    notifList.add(notifItem);
                } while (c1.moveToNext());
            }
        } else {
            emptyTV.setText(R.string.nopending);
        }
        if (c1 != null)
            c1.close();
        final DependantNotificationAdapter notifListAdapter = new DependantNotificationAdapter(
                MainActivity.this, notifList);
        listView.setAdapter(notifListAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (swipeContainer != null) {
                    if (firstVisibleItem == 0) {
                        swipeContainer.setEnabled(true);
                    } else swipeContainer.setEnabled(false);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                String value, date;
                TextView valuetextview = (TextView) view.findViewById(R.id.value);
                TextView datetextview = (TextView) view.findViewById(R.id.date);
                value = valuetextview.getText().toString();
                date = datetextview.getText().toString();
                final String query = "SELECT * FROM DEPENDANT_NOTIFICATIONS WHERE depen_notif_date=? order by depen_notif_date desc";
                final Cursor c1 = dbManager.selectQueryWithArgs(query, date);
                String category = null;
                if (c1 != null && c1.getCount() != 0) {
                    if (c1.moveToFirst()) {
                        do {
                            category = c1.getString(c1.getColumnIndex("depen_notif_category"));
                        } while (c1.moveToNext());
                    }
                }
                if (category != null) {
                    if (category.equals("anomaly")) {
                        final String query1 = "SELECT * FROM ADVICES WHERE date=? order by date desc";
                        final Cursor c = dbManager.selectQueryWithArgs(query1, date);
                        String pending = null;
                        if (c != null && c.getCount() != 0) {
                            if (c.moveToFirst()) {
                                do {
                                    pending = c.getString(c.getColumnIndex("pending"));
                                } while (c.moveToNext());
                            }
                        }
                        if (c != null && c.getCount() == 0) {
                            String adviserName = utility.getAdviserName();
                            if (!adviserName.equals("notset")) {
                                Intent intent = new Intent(MainActivity.this, GetAdvice.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("date", date);
                                //intent.putExtra("message", value.split(" of your device")[0]);
                                intent.putExtra("message", value.split("Click")[0].trim());
                                startActivity(intent);
                            } else {
                                Toast toast = Toast.makeText(MainActivity.this, "Wait till your advisor sends you a request to be paired up.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } else if (pending != null && pending.equals("yes")) {
                            dialog = new Dialog(MainActivity.this);
                            dialog.setContentView(R.layout.notif_dialog);
                            dialog.setTitle(Html.fromHtml("<font color='#08457E'><b>Advice Pending</font>"));
                            TextView str = (TextView) dialog.findViewById(R.id.tv);
                            str.setText("You have already asked advice for this anomaly. Please wait.");
                            Button dialogButtonok = (Button) dialog.findViewById(R.id.ok);
                            dialogButtonok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                        dialog = null;
                                    }
                                }
                            });
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    showDependantNotifList();
                                    if (dialog != null) {
                                        dialog.dismiss();
                                        dialog = null;
                                    }
                                }
                            });
                            if (dialog != null && !dialog.isShowing())
                                dialog.show();
                        }
                    } else if (category.equals("approvalrequest")) {
                        Intent intent = new Intent(MainActivity.this, ApproveAdviser.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        String firstWord = value.substring(0, value.indexOf("has"));
                        intent.putExtra("adviserName", firstWord);
                        intent.putExtra("date", date);
                        startActivity(intent);
                    } else if (category.contains("advice")) {
                        String[] parts = category.split(":");
                        String anomalyid = parts[1];
                        String anomaly = parts[2];
                        final String query1 = "SELECT * FROM ADVICES WHERE _id=?";
                        final Cursor c = dbManager.selectQueryWithArgs(query1, anomalyid);
                        String reply = null;
                        String followed = null;
                        if (c != null && c.getCount() != 0) {
                            if (c.moveToFirst()) {
                                do {
                                    reply = c.getString(c
                                            .getColumnIndex("advice"));
                                    followed = c.getString(c
                                            .getColumnIndex("followed"));
                                } while (c.moveToNext());
                            }
                        }
                        //if (followed != null && !followed.equals("actiontaken")) {
                        if (followed != null && followed.equals("null")) {
                            Intent intent = new Intent(MainActivity.this, AdviceReceived.class);
                            intent.putExtra("reply", reply);
                            intent.putExtra("anomalyid", anomalyid);
                            intent.putExtra("anomaly", anomaly);
                            intent.putExtra("date", date);
                            startActivity(intent);
                        }
                    }
                }
                if (c1 != null)
                    c1.close();
            }
        });
    }

}
