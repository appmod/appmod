package com.smu.appmod;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DEPENDANTS_TABLE = "DEPENDANTS";
    public static final String ADVISER_NOTIF_TABLE = "ADVISER_NOTIFICATIONS";
    public static final String DEPENDANT_NOTIF_TABLE = "DEPENDANT_NOTIFICATIONS";
    public static final String ADVICES_TABLE = "ADVICES";
    // Table columns
    public static final String KEY_ID = "_id";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String PHONE = "phone";
    public static final String UNREAD = "unread";

    public static final String ADVISER_NOTIF_CATEGORY = "adviser_notif_category";
    public static final String ADVISER_NOTIF_VALUE = "adviser_notif_value";
    public static final String ADVISER_NOTIF_DATE = "adviser_notif_date";
    public static final String ADVISER_NOTIF_READ = "adviser_notif_read";

    public static final String DEPENDANT_NOTIF_CATEGORY = "depen_notif_category";
    public static final String DEPENDANT_NOTIF_VALUE = "depen_notif_value";
    public static final String DEPENDANT_NOTIF_DATE = "depen_notif_date";
    public static final String DEPENDANT_NOTIF_READ = "depen_notif_read";

    public static final String ADVICE_ASKED_DATE = "date";
    public static final String SEEKER_NAME = "seeker_name";
    public static final String ANOMALY = "anomaly";
    public static final String ADVICE = "advice";
    public static final String PENDING = "pending";
    public static final String FOLLOWED = "followed";
    public static final String SENT_TO_ADVISOR_DATE = "advisordate";

    static final String DB_NAME = "Adviser";
    static final int DB_VERSION = 1;
    static Context ctx = null;

    private static final String CREATE_DEPENDANTS_TABLE = "create table " + DEPENDANTS_TABLE + "(" + KEY_ID + " integer primary key autoincrement, " + NAME + " TEXT NOT NULL, " + PHONE + " TEXT NOT NULL, " + UNREAD + " TEXT NOT NULL, " + STATUS + " TEXT NOT NULL);";
    private static final String CREATE_ADVISER_NOTIF_TABLE = "create table " + ADVISER_NOTIF_TABLE + "(" + KEY_ID + " integer primary key autoincrement, " + ADVISER_NOTIF_READ + " TEXT NOT NULL, " + ADVISER_NOTIF_CATEGORY + " TEXT NOT NULL, " + ADVISER_NOTIF_VALUE + " TEXT NOT NULL, " + ADVISER_NOTIF_DATE + " TEXT NOT NULL);";
    private static final String CREATE_DEPEN_NOTIF_TABLE = "create table " + DEPENDANT_NOTIF_TABLE + "(" + KEY_ID + " integer primary key autoincrement, " + DEPENDANT_NOTIF_READ + " TEXT NOT NULL, " + DEPENDANT_NOTIF_CATEGORY + " TEXT NOT NULL, " + DEPENDANT_NOTIF_VALUE + " TEXT NOT NULL, " + DEPENDANT_NOTIF_DATE + " TEXT NOT NULL);";
    private static final String CREATE_ADVICES_TABLE = "create table " + ADVICES_TABLE + "(" + KEY_ID + " TEXT NOT NULL, " + ADVICE_ASKED_DATE + " TEXT NOT NULL, " + SEEKER_NAME + " TEXT NOT NULL, " + ANOMALY + " TEXT NOT NULL, " + SENT_TO_ADVISOR_DATE + " TEXT," + PENDING + " TEXT , " + FOLLOWED + " TEXT , " + ADVICE + " TEXT );";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DEPENDANTS_TABLE);
        db.execSQL(CREATE_ADVICES_TABLE);
        UtilityClass utility = new UtilityClass(ctx);
        if (utility.getRole().trim().equals("Adviser")) {
            db.execSQL(CREATE_ADVISER_NOTIF_TABLE);
        } else if (utility.getRole().trim().equals("Dependant")) {
            db.execSQL(CREATE_DEPEN_NOTIF_TABLE);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + DEPENDANTS_TABLE + "';");
        db.execSQL("DROP TABLE IF EXISTS '" + ADVISER_NOTIF_TABLE + "';");
        db.execSQL("DROP TABLE IF EXISTS '" + DEPENDANT_NOTIF_TABLE + "';");
        db.execSQL("DROP TABLE IF EXISTS '" + CREATE_ADVICES_TABLE + "';");
        onCreate(db);
    }
}