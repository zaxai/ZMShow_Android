package com.zaxai.zmshow;

public class ResourceHelper {
    public final static int IMAGE_ID_TIEM_FOLDER=1;
    public final static int IMAGE_ID_ITEM_FILE=2;
    public static int getResourceIdByImageId(int imageId){
        switch(imageId){
            case IMAGE_ID_TIEM_FOLDER:return R.drawable.ic_zmshow_item_folder;
            case IMAGE_ID_ITEM_FILE:return R.drawable.ic_zmshow_item_file;
        }
        return 0;
    }
}
