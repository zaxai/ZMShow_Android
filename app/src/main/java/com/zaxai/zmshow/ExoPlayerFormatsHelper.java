package com.zaxai.zmshow;

public class ExoPlayerFormatsHelper {
    public static boolean isFormatSupported(String unknowFormat){
        unknowFormat=unknowFormat.toLowerCase();
        String[] supportedFormats=new String[]{".mp4",".m4a",".fmp4",".webm",".mkv",".mp3",".ogg",".wav",".mpg",".mpeg",".flv",".aac",".amr"};
        for(String format:supportedFormats){
            if(unknowFormat.equals(format)){
                return true;
            }
        }
        return false;
    }
}
