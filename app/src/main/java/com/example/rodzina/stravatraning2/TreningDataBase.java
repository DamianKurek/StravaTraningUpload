package com.example.rodzina.stravatraning2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rodzina on 18.11.2016.
 */

public class TreningDataBase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "treningi";
    public static final String DATABASE_TABLE = "trening";
    public static final String ID = "ID";
    public static final String NAZWA_TRENINGU = "NAZWA";
    public static final String OPIS_TRENINGU = "OPIS";
    public static final String CZAS_TRENINGU = "CZAS";
    public TreningDataBase(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+DATABASE_TABLE+
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "NAZWA TEXT, OPIS TEXT, CZAS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public boolean insertData(String nazwa,String opis,Integer czas){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues value = new ContentValues();
        value.put(NAZWA_TRENINGU,nazwa);
        value.put(OPIS_TRENINGU,opis);
        value.put(CZAS_TRENINGU,czas);
        long result = db.insert(DATABASE_TABLE,null,value);
        if(result == -1){
            return false;
        }else
            return true;
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+DATABASE_TABLE,null);
        return res;
    }
    public boolean UpdateData(String id,String nazwa,String opis,Integer czas){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues value = new ContentValues();
        value.put(NAZWA_TRENINGU,nazwa);
        value.put(OPIS_TRENINGU,opis);
        value.put(CZAS_TRENINGU,czas);
        long result = db.update(DATABASE_TABLE,value,"ID =? ",
                new String[]{id});
        if(result == -1){
            return false;
        }else
            return true;
    }
    public Integer DeleteData(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(DATABASE_TABLE,"ID =? ",
                new String[]{id});
    }
    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            checkDB = SQLiteDatabase.openDatabase(db.getPath(), null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
           return false;
        }
        return checkDB != null;
    }
}
