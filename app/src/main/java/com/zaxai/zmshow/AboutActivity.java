package com.zaxai.zmshow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        initView();
    }

    public static void actionStart(Context context){
        Intent intent=new Intent(context,AboutActivity.class);
        context.startActivity(intent);
    }

    public void initView(){
        if(Build.VERSION.SDK_INT>=23) {
            getWindow().setStatusBarColor(ColorHelper.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility( getWindow().getDecorView().getSystemUiVisibility()| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }else if(Build.VERSION.SDK_INT>=21) {
            getWindow().setStatusBarColor(ColorHelper.LTGRAY);
        }
        ImageButton back=findViewById(R.id.about_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView introduce=findViewById(R.id.about_introduce);
        introduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutIntroduceActivity.actionStart(AboutActivity.this);
            }
        });
        TextView update=findViewById(R.id.about_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.zaxai.com/zmshow/android/"));
                startActivity(intent);
            }
        });
    }
}
