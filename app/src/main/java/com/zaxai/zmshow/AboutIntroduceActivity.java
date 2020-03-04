package com.zaxai.zmshow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AboutIntroduceActivity extends AppCompatActivity {
    private TextView mTextIntroduce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_introduce_activity);
        initView();
        getIntroduce();
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
        mTextIntroduce=findViewById(R.id.about_introduce_text);
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

    public void getIntroduce(){
        HttpUtil.sendOkHttpRequest("http://www.zaxai.com/download/zmshow/android/introduce.json", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = "加载失败，请检查网络";
                        mTextIntroduce.setText(text);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = "";
                        try {
                            JSONArray jsonArray = new JSONArray(responseData);
                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String strEnter = "\n";
                                String version = jsonObject.getString("version");
                                String introduce = jsonObject.getString("introduce");
                                text += version;
                                text += strEnter;
                                text += introduce;
                                text += strEnter;
                                text += strEnter;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            text = "加载失败";
                        } finally {
                            mTextIntroduce.setText(text);
                        }
                    }
                });
            }
        });
    }
}
