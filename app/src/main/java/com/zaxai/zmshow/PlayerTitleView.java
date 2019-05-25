package com.zaxai.zmshow;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerTitleView extends FrameLayout {
    private ImageButton mBack;
    private TextView mTitle;
    private ImageButton mOrientation;
    private int mScreenOrientation=ActivityInfo.SCREEN_ORIENTATION_SENSOR;
    public PlayerTitleView(final Context context){
        this(context,"");
    }
    public PlayerTitleView(final Context context,String title){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.exo_player_title_view, this);
        mBack=findViewById(R.id.exo_back);
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
            }
        });
        mTitle= findViewById(R.id.exo_title);
        mTitle.setText(title);
        mOrientation=findViewById(R.id.exo_orientation);
        mOrientation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(mScreenOrientation){
                    case ActivityInfo.SCREEN_ORIENTATION_SENSOR:{
                        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                        mOrientation.setImageResource(R.drawable.exo_titles_orientation_locked);
                        mScreenOrientation=ActivityInfo.SCREEN_ORIENTATION_LOCKED;
                        Toast.makeText(context,"锁定旋转",Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case ActivityInfo.SCREEN_ORIENTATION_LOCKED:{
                        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        mOrientation.setImageResource(R.drawable.exo_titles_orientation_sensor);
                        mScreenOrientation=ActivityInfo.SCREEN_ORIENTATION_SENSOR;
                        Toast.makeText(context,"重力感应",Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        });
        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public void setTitle(String title) {
        if (mTitle != null)
            mTitle.setText((title));
    }
}
