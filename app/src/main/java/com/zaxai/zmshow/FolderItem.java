package com.zaxai.zmshow;

import java.util.List;

public class FolderItem{
    private int mID;
    private int mPosition;
    private String mItemPath;
    private String mItemName;
    private String mItemInfo;
    private int mImageID;
    private List<MediaItem> mMediaItemList;

    public FolderItem(int ID,int position,String itemPath,String itemName,String itemInfo,int imageID,List<MediaItem> mediaItemList){
        mID=ID;
        mPosition=position;
        mItemPath=itemPath;
        mItemName=itemName;
        mItemInfo=itemInfo;
        mImageID=imageID;
        mMediaItemList=mediaItemList;
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

    public List<MediaItem> getMediaItemList() {
        return mMediaItemList;
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

    public void setMediaItemList(List<MediaItem> mediaItemList) {
        this.mMediaItemList = mediaItemList;
    }
}
