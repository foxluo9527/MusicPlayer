package com.example.musicplayerdome.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

public class SearchDao {
    SQLiteDatabase db;
    public SearchDao(SQLiteDatabase db){
        this.db=db;
    }
    public boolean addRecord(String record){
        boolean isAdded=false;
        ContentValues cv=new ContentValues();
        cv.put("record",record);
        if (db.insert("search_record_tb",null,cv)!=-1){
            isAdded=true;
        }
        return isAdded;
    }
    public ArrayList<Record> getRecords(){
        ArrayList<Record> records=new ArrayList<Record>();
        Cursor c=db.rawQuery("select * from search_record_tb order by id desc",null);
        if (c!=null){
            while (c.moveToNext()) {
                int id=c.getInt(c.getColumnIndex("id"));
                String recordString=c.getString(c.getColumnIndex("record"));
                records.add(new Record(id,recordString));
            }
        }
        return records;
    }
    public void delRecord(int id){
        db.delete("search_record_tb","id=?",new String[]{id+""});
    }
    public void delAllRecord(){
        ArrayList<Record> records=getRecords();
        for (int i=0;i<records.size();i++){
            delRecord(records.get(i).getId());
        }
    }
    public static class Record{
        private int id;
        private String record;

        public Record() {
        }

        public Record(int id, String record) {
            this.id = id;
            this.record = record;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRecord() {
            return record;
        }

        public void setRecord(String record) {
            this.record = record;
        }
    }
}
