package com.zaxai.zmshow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper mInstance;

    public static DatabaseHelper getInstance(Context context){
        if(mInstance==null){
            mInstance=new DatabaseHelper(context);
        }
        return mInstance;
    }

    private DatabaseHelper(Context context){
        this(context,"Data.db",null,1);
    }

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE FolderItem (ID INT PRIMARY KEY,Position INT,Path NTEXT,Name NTEXT,Info NTEXT,ImageID INT)");
        db.execSQL("CREATE TABLE MediaItem (ID INT PRIMARY KEY,Position INT,Path NTEXT,Name NTEXT,Info NTEXT,ImageID INT,FolderItemID INT,LastPlayPositionMs LONG,IsFinalPlayed INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
