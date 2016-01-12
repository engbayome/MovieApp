package com.example.mohamed.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.Buffer;

/**
 * Created by Bayome on 1/5/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "movies.db";
    public static final String TABLE_NAME = "favorites";
    public static final String ID = "ID";
    public static final String IMAGE = "IMAGE";
    public static final String TITLE = "TITLE";
    public static final String JSON = "JSON";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME + " (" +
                ID + " TEXT PRIMARY KEY," +
                IMAGE + " TEXT NOT NULL," +
                TITLE + " TEXT NOT NULL," +
                JSON + " TEXT NOT NULL);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean InsertMovie (String id,String image,String title,String json){
        //TODO CHECK IF IT EXIT and remove
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(IMAGE,image);
        contentValues.put(TITLE,title);
        contentValues.put(JSON,json);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        if (result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Item[] getData(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor res =  sqLiteDatabase.rawQuery( "select * from "+TABLE_NAME, null );
        int num = res.getCount();
        Item[] result = new Item[num];

        res.moveToFirst();
        for (int i = 0 ;i < num ; i++)
        {
            Item element = new Item(res.getString(res.getColumnIndex(ID))
                    ,res.getString(res.getColumnIndex(IMAGE))
                    ,res.getString(res.getColumnIndex(TITLE))
                    ,res.getString(res.getColumnIndex(JSON))
            );

            result[i] = element;
            res.moveToNext();
        }
        return result;
    }
}