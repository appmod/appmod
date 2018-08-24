package com.smu.appmod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UtilityClass {

    static Context cntxt;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREFER_NAME = "Adviser";
    public static final String LOGIN_PHONE = "login_phone";
    public static final String ROLE = "role";
    public static final String FIRSTTIMEFLAG = "firsttime";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    private static final String IS_ADVISER_APPROVED = "IsAdviserApproved";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static String notificationType[] = {"default", "type1", "approvalrequest", "replyToAdviser", "helpRequired", "adviceReceived", "followed", "notfollowed", "deletedependant", "dependantregistered"};
    public static final String SENT_TOKEN_TO_SERVER = "SENT_TOKEN_TO_SERVER";
    public static final String REG_SUCCESS = "REG_SUCCESS";
    public static final String REGISTRATION_COMPLETE = "REGISTRATION_COMPLETE";
    public static final String REG_ID = "";
    public static final String ADVISER_PHONE = "adviser_phone";
    public static final String ADVISER_NAME = "adviser_name";
    //public static final String SERVER_URL = "http://flyer.sis.smu.edu.sg/AdviseProject/";
    private static final String WITHDRAWFLAG = "withdraw";
    private static final String TAG = "UC";

    UtilityClass(Context context) {
        cntxt = context;
        pref = cntxt.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String phone, String option) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(LOGIN_PHONE, phone);
        editor.putString(ROLE, option);
        editor.commit();
    }

    public void setAdviser(Boolean value) {
        editor.putBoolean(IS_ADVISER_APPROVED, value);
        editor.commit();
    }

    public void setAdviserPhone(String phone) {
        editor.putString(ADVISER_PHONE, phone);
        editor.commit();
    }

    public void setAdviserName(String name) {
        editor.putString(ADVISER_NAME, name);
        editor.commit();
    }

    public void setWithdraw() {
        editor.putBoolean(WITHDRAWFLAG, true);
        editor.commit();
    }

    public boolean hasWithdraw() {
        return pref.getBoolean(WITHDRAWFLAG, true);
    }

    public String getCountryZipCode() {
        String countryID = "";
        String countryZipCode = "";
        TelephonyManager manager = (TelephonyManager) cntxt.getSystemService(Context.TELEPHONY_SERVICE);
        countryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = cntxt.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(countryID.trim())) {
                countryZipCode = g[0];
                break;
            }
        }
        return countryZipCode;
    }

    public boolean checkLogin() {
        if (!this.isUserLoggedIn()) {
            Intent i = new Intent(cntxt, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cntxt.startActivity(i);
            return true;
        }
        return false;
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public boolean isFirstTime() {
        return pref.getBoolean(FIRSTTIMEFLAG, true);
    }

    public void setFirstTime() {
        editor.putBoolean(FIRSTTIMEFLAG, false);
        editor.putBoolean(WITHDRAWFLAG, false);
        editor.commit();
    }

    public String getLoginPhone() {
        String phone = pref.getString(LOGIN_PHONE, null);
        return phone;
    }

    public String getRole() {
        String role = pref.getString(ROLE, null);
        return role;
    }

    public String getAdviserPhone() {
        String adviserPhone = pref.getString(ADVISER_PHONE, null);
        return adviserPhone;
    }

    public String getAdviserName() {
        String adviserName = pref.getString(ADVISER_NAME, null);
        return adviserName;
    }

    public String getUserDetails() {
        String phone = pref.getString(LOGIN_PHONE, null);
        String role = pref.getString(ROLE, null);
        return phone + ":" + role;
    }

    private byte[] getPostDataString(String text) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        result.append(text);
        return result.toString().getBytes("UTF-8");
    }

    public String serverInteraction(String urlString, String text) throws IOException {
        if (hasActiveInternetConnection(urlString)) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(getPostDataString(text).length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(getPostDataString(text));
                int length = conn.getContentLength();
                if (length > 0) {
                    StringBuilder response = new StringBuilder();
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
//                    for (int c = in.read(); c != -1; c = in.read()) {
//                       stringBuilder.append(String.valueOf((char) c));
//                    }
                    //Log.d(TAG, "***** response=" + response.toString());
                    return response.toString();
                } else if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    StringBuilder result = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    //Log.d(TAG, "***** result=" + result.toString());
                    return result.toString();
                }
            } catch (Exception e) {
                Log.e(TAG, "***** Error=" + e.toString());
            } finally {
                conn.disconnect();
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(cntxt, "Server is down. Please try again later!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
            );
        }
        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) cntxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public boolean hasActiveInternetConnection(String url) {
        if (isNetworkAvailable()) {
            try {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    HttpURLConnection urlc = (HttpURLConnection) (new URL(url).openConnection());
                    urlc.setRequestProperty("User-Agent", "Test");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(10000);
                    urlc.connect();
                    return (urlc.getResponseCode() == 200);
                }
            } catch (IOException e) {
            }
        }
        return false;
    }

    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toast toast = Toast.makeText(cntxt, "This device is not supported.", Toast.LENGTH_SHORT);
                toast.show();
                activity.finish();
            }
            return false;
        }
        return true;
    }
}
