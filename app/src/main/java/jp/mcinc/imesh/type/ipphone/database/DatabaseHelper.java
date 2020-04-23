package jp.mcinc.imesh.type.ipphone.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "CONTACT";
    public static final String TABLE_HISTORY_NAME = "CONTACTHISTORY";

    // Table columns
    public static final String _ID = "_id";
    public static final String OWNERNAME = "ownername";
    public static final String OWNERNUMBER = "ownernumber";
    public static final String CONTACTDATE = "contactDate";
    public static final String CONTACTTIME = "contactTime";
    public static final String CALLTYPE = "callType";

    // Database Information
    static final String DB_NAME = "IPCONTACT.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_USER_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + OWNERNAME + " TEXT NOT NULL, " + OWNERNUMBER
            + " TEXT NOT NULL);";

    // Creating table query
    private static final String CREATE_HISTORY_TABLE = "create table " + TABLE_HISTORY_NAME
            + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + OWNERNAME + " TEXT NOT NULL, "
            + OWNERNUMBER + " TEXT NOT NULL, " + CONTACTDATE + " TEXT NOT NULL, " + CONTACTTIME
            + " TEXT NOT NULL, " + CALLTYPE + " INTEGER NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + CREATE_HISTORY_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
