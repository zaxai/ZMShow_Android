package com.zaxai.zmshow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class AboutIntroduceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_introduce_activity);
        initView();
    }

    public static void actionStart(Context context){
        Intent intent=new Intent(context,AboutIntroduceActivity.class);
        context.startActivity(intent);
    }

    private void initView(){
        Toolbar toolbar=findViewById(R.id.about_introduce_toolbar);
        toolbar.setTitle("功能介绍");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_zmshow_btn_back_white);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
