package com.smu.appmod;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GcmListener extends GcmListenerService {
    public static final int TYPE_STACK = 0;
    private static final String TAG = "GCMLtn";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = "";
        String type = "";
        String query1 = "";
        for (String not_type : UtilityClass.notificationType) {
            if (data.get(not_type) != null) {
                message = data.getString(not_type);
                type = not_type;
            }
        }
        // Anomaly case
        if (type.equals("default")) {
            final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date()).trim();
            DBManager dbManager = new DBManager(this);
            dbManager.open();
            if (message.startsWith("[msg]")) {
                //query1 = "INSERT INTO DEPENDANT_NOTIFICATIONS(depen_notif_read,depen_notif_category,depen_notif_value,depen_notif_date) values ('"
                //        + "read" + "','" + "message" + "','" + message + "','" + currentDateTimeString.trim() + "')";
                message = message.split("\\[msg\\]")[1].trim();
                query1 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                        + currentDateTimeString + "','" + "myself" + "','" + message + "')";
                dbManager.executeQuery(query1);
                Intent i = new Intent(this, ShowMessageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("date", currentDateTimeString);
                i.putExtra("message", message);
                startActivity(i);
            } else {
                query1 = "INSERT INTO DEPENDANT_NOTIFICATIONS(depen_notif_read,depen_notif_category,depen_notif_value,depen_notif_date) values ('"
                        + "unread" + "','" + "anomaly" + "','" + message + " Click on this notification to take an action." + "','" + currentDateTimeString.trim() + "')";
                dbManager.executeQuery(query1);
                Intent i = new Intent(this, DialogActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("date", currentDateTimeString.trim());
                i.putExtra("message", message.trim());
                startActivity(i);
            }
        } else if (type.equals("followed")) {
            sendNotification(message, "followed");
        } else if (type.equals("deletedependant")) {
            final UtilityClass utility = new UtilityClass(getApplicationContext());
            final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            DBManager dbManager = new DBManager(this);
            dbManager.open();
            String name = utility.getAdviserName();
            utility.setAdviserName("notset");
            utility.setAdviser(false);
            query1 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                    + currentDateTimeString + "','" + "myself" + "','" + name + " has deleted you as an advisee." + "')";
            dbManager.executeQuery(query1);
            Intent i = new Intent(this, DeleteActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("name", name);
            startActivity(i);

        } else if (type.equals("dependantregistered")) {
            sendNotification(message, "dependantregistered");
        } else if (type.equals("approvalrequest")) {
            sendNotification(message, "approvalrequest");
        } else if (type.equals("replyToAdviser")) {
            sendNotification(message, "replyToAdviser");
        } else if (type.equals("helpRequired")) {
            sendNotification(message, "helpRequired");
        } else if (type.equals("adviceReceived")) {
            sendNotification(message, "adviceReceived");
        } else if (type.equals("notfollowed")) {
            sendNotification(message, "notfollowed");
        }

    }

    private void sendNotification(String message, String type) {
        String text = null;
        final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        final UtilityClass utility = new UtilityClass(getApplicationContext());
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        Intent intent = null;
        if (type.equalsIgnoreCase("approvalrequest")) {
            intent = new Intent(this, MainActivity.class);
            String finalStr = null;
            String name = getContactName(this, message);
            //  String zipCode=utility.getCountryZipCode();
            utility.setAdviserPhone(message);
            String name1 = getContactName(this, message.substring(3));
            if (name != null) {
                finalStr = name;
            } else if (name1 != null) {
                finalStr = name1;
            } else {
                finalStr = message;
            }
            utility.setAdviserName(finalStr);
            text = finalStr + " has added you as an advisee.";
            String query1 = "INSERT INTO DEPENDANT_NOTIFICATIONS(depen_notif_read,depen_notif_category,depen_notif_value,depen_notif_date) values ('"
                    + "unread" + "','" + "approvalrequest" + "','" + finalStr + " has added you as an advisee. Please click on this notification to take an action." + "','" + currentDateTimeString + "')";
            dbManager.executeQuery(query1);
            String query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                    + currentDateTimeString + "','" + "myself" + "','" + finalStr + " has added you as an advisee." + "')";
            dbManager.executeQuery(query2);
        } else if (type.equalsIgnoreCase("dependantregistered")) {
            String dependantPhone = message;
            String finalName = null;
            String name = getName(dbManager, dependantPhone);
            String name1 = getName(dbManager, dependantPhone.substring(3));
            if (name != null)
                finalName = name;
            else {
                finalName = name1;
                dependantPhone = dependantPhone.substring(3);
            }
            String status = getResources().getString(R.string.advisee_registered);
            String updatequery = "UPDATE DEPENDANTS set status='"
                    + status + "' WHERE name ='" + finalName + "'";
            dbManager.executeQuery(updatequery);
            text = finalName + " has registered with the application.";
            String notifText = text + " Please send request to this advisee for getting paired up.";
            String query1 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                    + currentDateTimeString + "','" + finalName + "','" + notifText + "')";
            dbManager.executeQuery(query1);
            intent = new Intent(this, ReplytoAdviserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("msg", notifText);
        } else if (type.equalsIgnoreCase("replyToAdviser")) {
            String status = null;
            String notifText = null;
            String[] parts = message.split(":");
            String response = parts[0];
            String dependantPhone = parts[1];
            String finalName = null;
            String name = getName(dbManager, dependantPhone);
            String name1 = getName(dbManager, dependantPhone.substring(3));
            if (name != null)
                finalName = name;
            else {
                finalName = name1;
                dependantPhone = dependantPhone.substring(3);
            }
            if (response.equals("Accepted")) {
                text = finalName + " " + getResources().getString(R.string.accepted_req);
                status = getResources().getString(R.string.paired);
                notifText = text + " " + status;
            } else {
                text = finalName + " " + getResources().getString(R.string.not_accepted_req);
                status = getResources().getString(R.string.not_accepted);
                notifText = text + " " + getResources().getString(R.string.notif_not_accepted);
            }
            String query1 = "UPDATE DEPENDANTS set status='"
                    + status + "' WHERE phone ='" + dependantPhone + "'";
            dbManager.executeQuery(query1);
            String query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly) values ('" + "depen" + "','"
                    + currentDateTimeString + "','" + finalName + "','" + notifText + "')";
            dbManager.executeQuery(query2);
            intent = new Intent(this, ReplytoAdviserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("msg", notifText);
        } else if (type.equals("adviceReceived")) {
            intent = new Intent(this, MainActivity.class);
            String adviserName = utility.getAdviserName();
            text = "You have received advice from " + adviserName;
            String[] parts = message.split(":");
            String reply = parts[0].toLowerCase();
            String anomalyid = parts[1];
            String anomaly = parts[2];
            if (anomaly.contains(" ")) {
                String query1 = "INSERT INTO DEPENDANT_NOTIFICATIONS(depen_notif_read,depen_notif_category,depen_notif_value,depen_notif_date) values ('"
                        + "unread" + "','" + "advice:" + anomalyid + ":" + anomaly + "','" + "You have received advice from " + adviserName + " for suspicious activity by " + anomaly.substring(0, anomaly.indexOf(' ')) + "." + "','" + currentDateTimeString + "')";
                dbManager.executeQuery(query1);
            }
            dbManager.updateAdvice(reply, anomalyid);
        } else if (type.equalsIgnoreCase("followed")) {
            intent = new Intent(this, FollowActivity.class);
            String[] parts = message.split(":");
            String phone = parts[0];
            String anomaly = parts[1];
            String anomalyid = parts[2];
            String finalName = null;
            String name = getName(dbManager, phone);
            String name1 = getName(dbManager, phone.substring(3));
            if (name != null)
                finalName = name;
            else {
                finalName = name1;
            }
            text = finalName + " has followed your advice.";
            String finalStr = finalName + " has followed your advice for the anomaly - " + anomaly;
            intent.putExtra("text", finalStr);
            dbManager.updateFollowed(anomalyid, "yes");
        } else if (type.equalsIgnoreCase("notfollowed")) {
            intent = new Intent(this, FollowActivity.class);
            String[] parts = message.split(":");
            String phone = parts[0];
            String anomaly = parts[1];
            String anomalyid = parts[2];
            String finalName = null;
            String name = getName(dbManager, phone);
            String name1 = getName(dbManager, phone.substring(3));
            if (name != null)
                finalName = name;
            else {
                finalName = name1;
            }
            text = finalName + " has not followed your advice.";
            String finalStr = finalName + " has not followed your advice for the anomaly - " + anomaly;
            intent.putExtra("text", finalStr);
            dbManager.updateFollowed(anomalyid, "no");
        } else if (type.equalsIgnoreCase("helpRequired")) {
            intent = new Intent(this, MainActivity.class);
            String[] parts = message.split(":");
            String dependantPhone = parts[0];
            String anomaly = parts[1];
            String anomalyId = parts[2];
            String finalName = null;
            String name = getName(dbManager, dependantPhone);
            String name1 = getName(dbManager, dependantPhone.substring(3));
            if (name != null)
                finalName = name;
            else {
                finalName = name1;
                dependantPhone = dependantPhone.substring(3);
            }
            text = finalName + " is seeking your help.";
            String notifText = text + " Please advise this dependant for an anomaly.";
            String query1 = "INSERT INTO ADVISER_NOTIFICATIONS(adviser_notif_read,adviser_notif_category,adviser_notif_value,adviser_notif_date) values ('"
                    + "unread" + "','" + "advice" + "','" + notifText + "','" + currentDateTimeString + "')";
            dbManager.executeQuery(query1);
            String query2 = "INSERT INTO ADVICES(_id,date,seeker_name,anomaly,advice,followed,pending) values ('" + anomalyId + "','"
                    + currentDateTimeString + "','" + finalName + "','" + anomaly + "','" + "Pending" + "','" + "null" + "','" + "yes" + "')";
            dbManager.executeQuery(query2);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<StatusBarNotification> groupedNotifications = new ArrayList<>();
            for (StatusBarNotification sbn : notificationManager.getActiveNotifications()) {
                if (sbn.getId() != TYPE_STACK) {
                    groupedNotifications.add(sbn);
                }
            }
            if (groupedNotifications.size() > 1) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
                builder.setContentTitle("Advisor")
                        .setContentText(String.format("%d new activities", groupedNotifications.size()));
                NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
                {
                    for (StatusBarNotification activeSbn : groupedNotifications) {
                        String stackNotificationLine = (String) activeSbn.getNotification().extras.get(NotificationCompat.EXTRA_TITLE);
                        if (stackNotificationLine != null) {
                            inbox.addLine(stackNotificationLine);
                        }
                    }
                    inbox.setSummaryText(String.format("%d new activities", groupedNotifications.size()));
                }
                builder.setStyle(inbox);
                builder.setGroup("0").setGroupSummary(true);
                builder.setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH);
                final int requestCode = (int) System.currentTimeMillis() / 1000;
                builder.setContentIntent(PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT));
                Notification stackNotification = builder.build();
                stackNotification.defaults = Notification.DEFAULT_ALL;
                notificationManager.notify("AppMod", 0, stackNotification);
            }
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Advisor")
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(0, notificationBuilder.build());
        }
        if (intent != null) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } catch (Exception e) {
                //Log.e(TAG, "***** Error=" + e.toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    public static String getName(DBManager dbManager, String phoneNumber) {
        String contactName = null;
        String query = "SELECT * FROM DEPENDANTS where phone='" + phoneNumber + "'";
        Cursor c1 = dbManager.selectQuery(query);
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    contactName = c1.getString(c1.getColumnIndex("name"));
                    contactName = contactName.trim();
                } while (c1.moveToNext());
            }
        }
        if (c1 != null)
            c1.close();
        return contactName;
    }
}