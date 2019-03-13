package com.zaxai.zmshow;


public class MediaItem{
    private int mID;
    private int mPosition;
    private String mItemPath;
    private String mItemName;
    private String mItemInfo;
    private int mImageID;
    private int mFolderItemID;
    private long mLastPlayPositionMs;
    private int mIsFinalPlayed;

    public MediaItem(int ID,int position,String itemPath,String itemName,String itemInfo,int imageID,int folderItemID,long lastPlayPositionMs,int isFinalPlayed){
        mID=ID;
        mPosition=position;
        mItemPath=itemPath;
        mItemName=itemName;
        mItemInfo=itemInfo;
        mImageID=imageID;
        mFolderItemID=folderItemID;
        mLastPlayPositionMs=lastPlayPositionMs;
        mIsFinalPlayed=isFinalPlayed;
    }

    public int getID() {
        return mID;
    }

    public int getPosition() {
        return mPosition;
    }

    public String getItemPath() {
        return mItemPath;
    }

    public String getItemName() {
        return mItemName;
    }

    public String getItemInfo() {
        return mItemInfo;
    }

    public int getImageID() {
        return mImageID;
    }

    public int getFolderItemID() {
        return mFolderItemID;
    }

    public long getLastPlayPositionMs() {
        return mLastPlayPositionMs;
    }

    public int isFinalPlayed() {
        return mIsFinalPlayed;
    }

    public void setID(int ID) {
        this.mID = ID;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void setItemPath(String itemPath) {
        this.mItemPath = itemPath;
    }

    public void setItemName(String itemName) {
        this.mItemName = itemName;
    }

    public void setItemInfo(String itemInfo) {
        this.mItemInfo = itemInfo;
    }

    public void setImageID(int imageID) {
        this.mImageID = imageID;
    }

    public void setmFolderItemID(int folderItemID) {
        this.mFolderItemID = folderItemID;
    }

    public void setLastPlayPositionMs(long lastPlayPositionMs) {
        this.mLastPlayPositionMs = lastPlayPositionMs;
    }

    public void setFinalPlayed(int isFinalPlayed) {
        this.mIsFinalPlayed = isFinalPlayed;
    }
}
