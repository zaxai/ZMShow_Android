package com.zaxai.zmshow;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SuggestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggest_activity);
        initView();
    }

    public static void actionStart(Context context){
        Intent intent=new Intent(context,SuggestActivity.class);
        context.startActivity(intent);
    }

    private void initView(){
        Toolbar toolbar=findViewById(R.id.suggest_toolbar);
        toolbar.setTitle("意见");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_zmshow_btn_back_white);
        }
        final EditText suggest=findViewById(R.id.suggest_suggest);
        final Button submit=findViewById(R.id.suggest_submit);
        suggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text=s.toString();
                if(text.isEmpty()){
                    submit.setBackgroundColor(ColorHelper.LTGRAY);
                    submit.setEnabled(false);
                }else{
                    submit.setBackgroundColor(ColorHelper.ORANGE);
                    submit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Suggest", suggest.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(SuggestActivity.this, "文本已复制", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.hao123.com/mail"));
                startActivity(intent);
            }
        });
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
