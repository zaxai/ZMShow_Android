package com.zaxai.zmshow;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zaxai.zapp.ZShareDialogFragment;
import com.zaxai.zapp.ZShareItem;

import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity {
    public final static int SHARE_TAG_LINK=1;
    public final static int SHARE_TAG_WECHAT=2;
    public final static int SHARE_TAG_QQ=3;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);
        initView();
    }

    public static void actionStart(Context context){
        Intent intent=new Intent(context,ShareActivity.class);
        context.startActivity(intent);
    }

    private void initView(){
        Toolbar toolbar=findViewById(R.id.share_toolbar);
        toolbar.setTitle("分享");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_zmshow_btn_back_white);
        }
        mRadioGroup=findViewById(R.id.share_source);
        final ImageView imageView=findViewById(R.id.share_image);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.share_zaxai: imageView.setImageResource(R.drawable.ic_zmshow_image_zaxai);break;
                    case R.id.share_baidu: imageView.setImageResource(R.drawable.ic_zmshow_image_baidu);break;
                    default:break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_toolbar,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            break;
            case R.id.share:{
                List<ZShareItem> zShareItemList=new ArrayList<>();
                zShareItemList.add(new ZShareItem(SHARE_TAG_LINK,"复制链接",R.drawable.ic_zmshow_item_link));
                zShareItemList.add(new ZShareItem(SHARE_TAG_WECHAT,"微信",R.drawable.ic_zmshow_item_wechat));
                zShareItemList.add(new ZShareItem(SHARE_TAG_QQ,"QQ",R.drawable.ic_zmshow_item_qq));
                new ZShareDialogFragment().setOnClickListener(new ZShareDialogFragment.OnItemClickListener() {
                    @Override
                    public void onItemClick(ZShareItem zShareItem) {
                        switch(zShareItem.getTag()){
                            case SHARE_TAG_LINK: {
                                String link="";
                                switch(mRadioGroup.getCheckedRadioButtonId()){
                                    case R.id.share_zaxai: link="http://www.zaxai.com/zmshow/android/";break;
                                    case R.id.share_baidu: link="网盘地址 https://pan.baidu.com/s/1XlRzYlnHQrowlvxvKoI4IQ 提取码 1fbh";break;
                                    default:break;
                                }
                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("Link", link);
                                clipboardManager.setPrimaryClip(clipData);
                                Toast.makeText(ShareActivity.this, "链接已复制", Toast.LENGTH_SHORT).show();
                            }
                            break;
                            default:
                                Toast.makeText(ShareActivity.this, "业务洽谈中,请使用复制链接", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).show(getSupportFragmentManager(),zShareItemList);
            }
            break;
            default:
                break;
        }
        return true;
    }
}
