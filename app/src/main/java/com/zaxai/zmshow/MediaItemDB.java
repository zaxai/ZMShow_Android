package com.zaxai.zmshow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MediaItemDB {

    private Context mContext;
    private DatabaseHelper mHelper;

    public MediaItemDB(Context context){
        mContext=context;
        mHelper=DatabaseHelper.getInstance(context);
    }

    public long insert(MediaItem mediaItem){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("ID",mediaItem.getID());
        values.put("Position",mediaItem.getPosition());
        values.put("Path",mediaItem.getItemPath());
        values.put("Name",mediaItem.getItemName());
        values.put("Info",mediaItem.getItemInfo());
        values.put("ImageID",mediaItem.getImageID());
        values.put("FolderItemID",mediaItem.getFolderItemID());
        values.put("LastPlayPositionMs",mediaItem.getLastPlayPositionMs());
        values.put("IsFinalPlayed",mediaItem.isFinalPlayed());
        return db.insert("MediaItem",null,values);
    }

    public void insert(List<MediaItem> mediaItemList){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        db.beginTransaction();
        for(MediaItem mediaItem:mediaItemList) {
            ContentValues values = new ContentValues();
            values.put("ID", mediaItem.getID());
            values.put("Position", mediaItem.getPosition());
            values.put("Path", mediaItem.getItemPath());
            values.put("Name", mediaItem.getItemName());
            values.put("Info", mediaItem.getItemInfo());
            values.put("ImageID", mediaItem.getImageID());
            values.put("FolderItemID", mediaItem.getFolderItemID());
            values.put("LastPlayPositionMs", mediaItem.getLastPlayPositionMs());
            values.put("IsFinalPlayed", mediaItem.isFinalPlayed());
            db.insert("MediaItem", null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public int delete(MediaItem mediaItem){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        return db.delete("MediaItem","ID=?", new String[]{String.format("%d", mediaItem.getID())});
    }

    public void delete(List<MediaItem> mediaItemList){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        db.beginTransaction();
        for(MediaItem mediaItem:mediaItemList) {
            db.delete("MediaItem", "ID=?", new String[]{String.format("%d", mediaItem.getID())});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public int update(MediaItem mediaItem){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("Position",mediaItem.getPosition());
        values.put("Path",mediaItem.getItemPath());
        values.put("Name",mediaItem.getItemName());
        values.put("Info",mediaItem.getItemInfo());
        values.put("ImageID",mediaItem.getImageID());
        values.put("FolderItemID",mediaItem.getFolderItemID());
        values.put("LastPlayPositionMs",mediaItem.getLastPlayPositionMs());
        values.put("IsFinalPlayed",mediaItem.isFinalPlayed());
        return db.update("MediaItem", values, "ID=?", new String[]{String.format("%d", mediaItem.getID())});
    }

    public void update(List<MediaItem> mediaItemList){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        db.beginTransaction();
        for(MediaItem mediaItem:mediaItemList) {
            ContentValues values = new ContentValues();
            values.put("Position", mediaItem.getPosition());
            values.put("Path", mediaItem.getItemPath());
            values.put("Name", mediaItem.getItemName());
            values.put("Info", mediaItem.getItemInfo());
            values.put("ImageID", mediaItem.getImageID());
            values.put("FolderItemID", mediaItem.getFolderItemID());
            values.put("LastPlayPositionMs", mediaItem.getLastPlayPositionMs());
            values.put("IsFinalPlayed", mediaItem.isFinalPlayed());
            db.update("MediaItem", values, "ID=?", new String[]{String.format("%d", mediaItem.getID())});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<MediaItem> select(String sql){
        SQLiteDatabase db=mHelper.getReadableDatabase();
        List<MediaItem> mediaItemList=new ArrayList<>();
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do{
                String keys[]=new String[]{"ID","Position","Path","Name","Info","ImageID","FolderItemID","LastPlayPositionMs","IsFinalPlayed"};
                int indices[]=new int[keys.length];
                int i;
                for(i=0;i<keys.length;++i){
                    indices[i]=cursor.getColumnIndex(keys[i]);
                    if(indices[i]==-1){
                        break;
                    }
                }
                if(i<keys.length)
                    break;
                int ID=cursor.getInt(indices[0]);
                int position=cursor.getInt(indices[1]);
                String path=cursor.getString(indices[2]);
                String name=cursor.getString(indices[3]);
                String info=cursor.getString(indices[4]);
                int imageID=cursor.getInt(indices[5]);
                int folderItemID=cursor.getInt(indices[6]);
                long lastPlayPositionMs=cursor.getLong(indices[7]);
                int isFinalPlayed=cursor.getInt(indices[8]);
                mediaItemList.add(new MediaItem(ID,position,path,name,info,imageID,folderItemID,lastPlayPositionMs,isFinalPlayed));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return mediaItemList;
    }

    static public int getNextInsertID(Context context){
        int ID=0;
        MediaItemDB mediaItemDB=new MediaItemDB(context);
        List<MediaItem> mediaItemList=mediaItemDB.select("SELECT * FROM MediaItem ORDER BY ID DESC LIMIT 1");
        if(mediaItemList.size()==1)
            ID=mediaItemList.get(0).getID()+1;
        return ID;
    }

    static public void sortPosition(Context context,List<MediaItem> mediaItemList){
        MediaItemDB mediaItemDB=new MediaItemDB(context);
        mediaItemDB.update(mediaItemList);
    }

}
