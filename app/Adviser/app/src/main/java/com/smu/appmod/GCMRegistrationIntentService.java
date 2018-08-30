package com.smu.appmod;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import android.util.Log;
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.URLEncoder;

public class GCMRegistrationIntentService extends IntentService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 500;
    private static final Random random = new Random();
    SharedPreferences sharedPreferences;
    String phoneAndRole;
    public GCMRegistrationIntentService() {
        super("User1");
    }
    private static final String TAG = "GCMRIS";

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent registrationComplete = new Intent(UtilityClass.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("prefix", "");
        String token = sharedPreferences.getString(UtilityClass.REG_ID, "");
        phoneAndRole = intent.getExtras().getString("userdetails");
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            if (!intent.getExtras().getBoolean("register")) {
                //client wants to un-register
            } else {
                if (!intent.getExtras().getBoolean("tokenRefreshed") && !token.equals("")) {
                    registrationComplete.putExtra("prefix", "Old Token:\n");
                } else {
                    token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    registrationComplete.putExtra("prefix", "Fresh Token:\n");
                    //Log.d(TAG, "***** TOKEN=" + token);
                }
                sharedPreferences.edit().putString(UtilityClass.REG_ID, token).apply();
                //sharedPreferences.edit().putBoolean(UtilityClass.REG_SUCCESS, true).apply();
                boolean result = registerWithServer(token, instanceID, phoneAndRole);
//                Log.i("AppMod", "register result: " + result);
                if (result) {
                    sharedPreferences.edit().putBoolean(UtilityClass.SENT_TOKEN_TO_SERVER, true).apply();
                    sharedPreferences.edit().putBoolean(UtilityClass.REG_SUCCESS, false).apply();
                    registrationComplete.putExtra("register", true);
                } else {
                    sharedPreferences.edit().putBoolean(UtilityClass.SENT_TOKEN_TO_SERVER, true).apply();
                    sharedPreferences.edit().putBoolean(UtilityClass.REG_SUCCESS, true).apply();
                    registrationComplete.putExtra("register", false);
                }
            }
        } catch (Exception e) {
            sharedPreferences.edit().putBoolean(UtilityClass.REG_SUCCESS, false).apply();
        }

        //if (intent.getExtras().getBoolean("register"))
        //    registrationComplete.putExtra("register", true);
        //else
        //    registrationComplete.putExtra("register", false);

        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private boolean registerWithServer(String token, InstanceID instanceID, String phoneAndRole) throws UnsupportedEncodingException {
        final String serverUrl = "http://flyer.sis.smu.edu.sg/AdviseProject/register.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", token);
        String[] parts = phoneAndRole.split(":");
        String phone = parts[0];
        String role = parts[1];
        params.put("phone", phone);
        params.put("role", role);
        params.put("instanceId", instanceID.toString());
        //long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        Log.i("AppMod", "token: " + token);
        long backoff = 500;
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            try {
                String result = post(serverUrl, params);
                Log.i("AppMod", "Register return result:" + result);
                if (result.contains("Failed!")) {
                    //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                    return false;
                } else if (result.contains("Success!")) {
                    return true;
                }

            } catch (IOException e) {
                try {
                    Thread.sleep(backoff);
                } catch (Exception e1) {
                    Thread.currentThread().interrupt();
                }
                backoff *= 2;
            }
        }
        return false;
    }

    private static String post(String endpoint, Map<String, String> params)throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            //bodyBuilder.append(param.getKey()).append('=').append(URLEncoder.encode(param.getValue(),"UTF-8"));
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        byte[] bytes = body.getBytes("UTF-8");
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //PrintWriter out = new PrintWriter(conn.getOutputStream());
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(body);
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String decodedString = "";
            String response = "";
            while ((decodedString = in.readLine()) != null) {
                response += decodedString;
            }
            in.close();
            Log.d(TAG, "***** response=" + response);
            int status = conn.getResponseCode();
            Log.d(TAG, "***** status=" + status);
            if (status != 200) {
                throw new Exception("Status=" + status);
            }
            return response;
        } catch (Exception e) {
            //Log.e(TAG, "Post Failed. " + e.toString());
            throw new IOException("Post Failed. " + e.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}

