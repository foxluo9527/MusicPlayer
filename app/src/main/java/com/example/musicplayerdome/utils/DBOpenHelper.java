package com.example.musicplayerdome.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {


    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
        //context.openOrCreateDatabase(name,MODE_PRIVATE,null);
    }
    /**
     *@param
     *
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists remember_tb(now_remember int,account text unique,password text,head_img text)");
        db.execSQL("create table if not exists search_record_tb(id integer primary key autoincrement,record text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
