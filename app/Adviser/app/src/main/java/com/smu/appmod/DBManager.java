package com.smu.appmod;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBManager {

    private DBHelper dbHelper;
    private Context context;
    SQLiteDatabase sqlDatabase;
    private static final String TAG = "DBM";

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        return this;
    }

    public boolean updateReadDepndnt(String date) {
        if (sqlDatabase != null && sqlDatabase.isOpen()) {
            sqlDatabase.close();
        }
        sqlDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.DEPENDANT_NOTIF_READ, "read");
        try {
            String[] args = new String[]{date};
            sqlDatabase.update(DBHelper.DEPENDANT_NOTIF_TABLE, values, DBHelper.DEPENDANT_NOTIF_DATE + "=?", args);
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "***** Error=" + e.toString());
            return false;
        } finally {
            sqlDatabase.close();
        }
    }

    public boolean updateAdvice(String advice, String anomalyid) {
        if (sqlDatabase != null && sqlDatabase.isOpen()) {
            sqlDatabase.close();
        }
        sqlDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.PENDING, "no");
        values.put(DBHelper.ADVICE, advice);
        try {
            String[] args = new String[]{anomalyid};
            sqlDatabase.update(DBHelper.ADVICES_TABLE, values, DBHelper.KEY_ID + "=?", args);
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "***** Error=" + e.toString());
            return false;
        } finally {
            sqlDatabase.close();
        }
    }

    public boolean updateFollowed(String anomalyid, String flag) {
        if (sqlDatabase != null && sqlDatabase.isOpen()) {
            sqlDatabase.close();
        }
        sqlDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (flag.equals("yes")) {
            values.put(DBHelper.FOLLOWED, "This advice was followed");
        } else {
            values.put(DBHelper.FOLLOWED, "This advice was not followed");
        }
        try {
            String[] args = new String[]{anomalyid};
            sqlDatabase.update(DBHelper.ADVICES_TABLE, values, DBHelper.KEY_ID + "=?", args);
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "***** Error=" + e.toString());
            return false;
        } finally {
            sqlDatabase.close();
        }
    }

    public boolean updateAdviceReceived(String anomalyid) {
        if (sqlDatabase != null && sqlDatabase.isOpen()) {
            sqlDatabase.close();
        }
        sqlDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(DBHelper.FOLLOWED, "actiontaken");
        values.put(DBHelper.FOLLOWED, "Advice followed");
        try {
            String[] args = new String[]{anomalyid};
            sqlDatabase.update(DBHelper.ADVICES_TABLE, values, DBHelper.KEY_ID + "=?", args);
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "***** Error=" + e.toString());
            return false;
        } finally {
            sqlDatabase.close();
        }
    }

    public boolean updateAnomaly(String date, String anomalyId) {
        if (sqlDatabase != null && sqlDatabase.isOpen()) {
            sqlDatabase.close();
        }
        sqlDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.DEPENDANT_NOTIF_CATEGORY, "advice:" + anomalyId);
        try {
            String[] args = new String[]{date};
            sqlDatabase.update(DBHelper.DEPENDANT_NOTIF_TABLE, values, DBHelper.DEPENDANT_NOTIF_DATE + "=?", args);
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "***** Error=" + e.toString());
            return false;
        } finally {
            sqlDatabase.close();
        }
    }

    public void executeQuery(String query) {
        try {
            if (sqlDatabase != null && sqlDatabase.isOpen()) {
                sqlDatabase.close();
            }
            sqlDatabase = dbHelper.getWritableDatabase();
            sqlDatabase.execSQL(query);
        } catch (Exception e) {
            Log.e(TAG, "***** Error=" + e.toString());
            //System.out.println("DATABASE ERROR " + e);
        } finally {
            sqlDatabase.close();
        }
    }

    public Cursor selectQuery(String query) {
        Cursor c1 = null;
        try {
            if (sqlDatabase != null && sqlDatabase.isOpen()) {
                sqlDatabase.close();
            }
            sqlDatabase = dbHelper.getWritableDatabase();
            c1 = sqlDatabase.rawQuery(query, null);
        } catch (Exception e) {
            Log.e(TAG, "***** Error=" + e.toString());
            //System.out.println("DATABASE ERROR " + e);
        } finally {
            //sqlDatabase.close();
        }
        return c1;
    }

    public Cursor selectQueryWithArgs(String query, String arg) {
        Cursor c1 = null;
        try {
            if (sqlDatabase != null && sqlDatabase.isOpen()) {
                sqlDatabase.close();
            }
            sqlDatabase = dbHelper.getWritableDatabase();
            c1 = sqlDatabase.rawQuery(query, new String[]{arg});

        } catch (Exception e) {
            Log.e(TAG, "***** Error=" + e.toString());
            //System.out.println("DATABASE ERROR " + e);
        } finally {
            //sqlDatabase.close();
        }
        return c1;
    }

    /*public String getTableAsString() {
        String tableString = String.format("Table %s:\n", "ADVICES");
        if (sqlDatabase != null && sqlDatabase.isOpen()) {
            sqlDatabase.close();
        }
        sqlDatabase = dbHelper.getWritableDatabase();
        Cursor allRows = sqlDatabase.rawQuery("SELECT * FROM  ADVICES", null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        return tableString;
    }*/

}