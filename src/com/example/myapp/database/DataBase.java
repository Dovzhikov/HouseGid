package com.example.myapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper implements BaseColumns {

    private static final String DATABASE_NAME = "task_database.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "accesspoint";
    public static final String SSID = "ssid";
    public static final String BSSID = "bssid";
    public static final String LEVEL = "level";


    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME
            + " (" + DataBase._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SSID + " VARCHAR(255), " + BSSID + " VARCHAR(255), " + LEVEL + " INTEGER);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("LOG_TAG", "Обновление базы данных с версии " + oldVersion
                + " до версии " + newVersion + ", которое удалит все старые данные");
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
        onCreate(db);
    }


    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}

