package com.zaxai.zmshow;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final static int REQUEST_STORAGE=1;
    private List <Fragment> mContentList=new ArrayList<>();
    private int mSystemUiVisibility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initView();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE);
        }
    }

    public static void actionStart(Context context){
        Intent intent=new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }

    private void initView(){
        mSystemUiVisibility=getWindow().getDecorView().getSystemUiVisibility();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_network: {
                        replaceContent(mContentList.get(0));
                        setStatusBar(0);
                    }
                    return true;
                    case R.id.navigation_local: {
                        replaceContent(mContentList.get(1));
                        setStatusBar(1);
                    }
                    return true;
                    case R.id.navigation_me: {
                        replaceContent(mContentList.get(2));
                        setStatusBar(2);
                    }
                    return true;
                }
                return false;
            }
        });
        mContentList.add(new NetworkFragment());
        mContentList.add(new LocalFragment());
        mContentList.add(new MeFragment());
        replaceContent(mContentList.get(1));
        setStatusBar(1);
        navigation.setSelectedItemId(navigation.getMenu().getItem(1).getItemId());
    }

    private void setStatusBar(int fragmentIndex){
        switch(fragmentIndex){
            case 0:{
                if(Build.VERSION.SDK_INT>=23) {
                    getWindow().setStatusBarColor(ColorHelper.WHITE);
                    getWindow().getDecorView().setSystemUiVisibility( mSystemUiVisibility| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }else if(Build.VERSION.SDK_INT>=21) {
                    getWindow().setStatusBarColor(ColorHelper.LTGRAY);
                }
            }
            break;
            case 1:{
                if(Build.VERSION.SDK_INT>=23) {
                    getWindow().setStatusBarColor(ColorHelper.ORANGE);
                    getWindow().getDecorView().setSystemUiVisibility( mSystemUiVisibility);
                }else if(Build.VERSION.SDK_INT>=21) {
                    getWindow().setStatusBarColor(ColorHelper.ORANGE);
                }
            }
            break;
            case 2:{
                if(Build.VERSION.SDK_INT>=23) {
                    getWindow().setStatusBarColor(ColorHelper.ORANGE);
                    getWindow().getDecorView().setSystemUiVisibility( mSystemUiVisibility);
                }else if(Build.VERSION.SDK_INT>=21) {
                    getWindow().setStatusBarColor(ColorHelper.ORANGE);
                }
            }
            break;
        }
    }
    private void replaceContent(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.content,fragment);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_STORAGE:{
                if (grantResults.length > 0){
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else{
                    Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            default:
                break;
        }
    }
}
