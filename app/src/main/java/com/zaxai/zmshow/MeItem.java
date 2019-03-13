package com.zaxai.zmshow;

public class MeItem {
    private String mItemName;
    private int mImageID;

    public MeItem(String itemName,int imageID){
        mItemName=itemName;
        mImageID=imageID;
    }

    public String getItemName() {
        return mItemName;
    }

    public int getImageID() {
        return mImageID;
    }

    public void setItemName(String itemName) {
        this.mItemName = itemName;
    }

    public void setImageID(int imageID) {
        this.mImageID = imageID;
    }
}
