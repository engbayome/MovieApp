package com.example.mohamed.movieapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bayome on 1/5/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "movies.db";
    public static final String TABLE_NAME = "favorites";
    public static final String ID = "ID";
    public static final String IMAGE = "IMAGE";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME + " ("+ID+" INTEGER PRIMARY KEY,"+IMAGE+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}