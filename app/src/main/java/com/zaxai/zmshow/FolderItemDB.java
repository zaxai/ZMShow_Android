package com.zaxai.zmshow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FolderItemDB {

    private Context mContext;
    private DatabaseHelper mHelper;
    private MediaItemDB mMediaItemDB;

    public FolderItemDB(Context context){
        mContext=context;
        mHelper=DatabaseHelper.getInstance(context);
        mMediaItemDB=new MediaItemDB(context);
    }

    private long insert(FolderItem folderItem){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("ID",folderItem.getID());
        values.put("Position",folderItem.getPosition());
        values.put("Path",folderItem.getItemPath());
        values.put("Name",folderItem.getItemName());
        values.put("Info",folderItem.getItemInfo());
        values.put("ImageID",folderItem.getImageID());
        return db.insert("FolderItem",null,values);
    }

    public long insertFull(FolderItem folderItem){
        mMediaItemDB.insert(folderItem.getMediaItemList());
        return insert(folderItem);
    }

    private int delete(FolderItem folderItem){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        return db.delete("FolderItem","ID=?", new String[]{String.format("%d", folderItem.getID())});
    }

    public int deleteFull(FolderItem folderItem){
        List<MediaItem> mediaItemList=mMediaItemDB.select(String.format("SELECT * FROM MediaItem WHERE FolderItemID=%d",folderItem.getID()));
        mMediaItemDB.delete(mediaItemList);
        return delete(folderItem);
    }

    private int update(FolderItem folderItem){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("Position",folderItem.getPosition());
        values.put("Path",folderItem.getItemPath());
        values.put("Name",folderItem.getItemName());
        values.put("Info",folderItem.getItemInfo());
        values.put("ImageID",folderItem.getImageID());
        return db.update("FolderItem", values, "ID=?", new String[]{String.format("%d", folderItem.getID())});
    }

    public int updateFull(FolderItem folderItem){
        mMediaItemDB.update(folderItem.getMediaItemList());
        return update(folderItem);
    }

    private List<FolderItem> select(String sql){
        SQLiteDatabase db=mHelper.getReadableDatabase();
        List<FolderItem> folderItemList=new ArrayList<>();
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do{
                String keys[]=new String[]{"ID","Position","Path","Name","Info","ImageID"};
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
                List<MediaItem> mediaItemList=mMediaItemDB.select(String.format("SELECT * FROM MediaItem WHERE FolderItemID=%d ORDER BY Position",ID));
                folderItemList.add(new FolderItem(ID,position,path,name,info,imageID,mediaItemList));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return folderItemList;
    }

    public List<FolderItem> selectFull(String sql){
        return select(sql);
    }

    static public int getNextInsertID(Context context) {
        int ID=0;
        FolderItemDB folderItemDB=new FolderItemDB(context);
        List<FolderItem> folderItemList=folderItemDB.select("SELECT * FROM FolderItem ORDER BY ID DESC LIMIT 1");
        if(folderItemList.size()==1)
            ID=folderItemList.get(0).getID()+1;
        return ID;
    }

    static public void sortPosition(Context context,List<FolderItem> folderItemList){
        FolderItemDB folderItemDB=new FolderItemDB(context);
        for(FolderItem folderItem:folderItemList){
            folderItemDB.update(folderItem);
        }
    }

}
