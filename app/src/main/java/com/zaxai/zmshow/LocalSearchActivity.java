package com.zaxai.zmshow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class LocalSearchActivity extends AppCompatActivity {
    private MediaAdapter mMediaAdapter;
    private MediaItemDB mMediaItemDB;
    private List<MediaItem> mMediaItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_search_activity);
        mMediaItemDB=new MediaItemDB(this);
        mMediaItemList=new ArrayList<>();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.local_search_toolbar,menu);
        MenuItem searchItem=menu.findItem(R.id.local_search);
        SearchView searchView=(SearchView)searchItem.getActionView();
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        searchView.setQueryHint("输入关键字查找");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mMediaItemList.clear();
                mMediaItemList.addAll(mMediaItemDB.select(String.format("SELECT * FROM MediaItem WHERE Name LIKE '%%%s%%'",s)));
                mMediaAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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

    public static void actionStart(Context context){
        Intent intent=new Intent(context,LocalSearchActivity.class);
        context.startActivity(intent);
    }

    public void initView(){
        Toolbar toolbar=findViewById(R.id.local_search_toolbar);
        toolbar.setTitle("本地搜索");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_zmshow_btn_back_white);
        }
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.local_search_list_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mMediaAdapter=new MediaAdapter(mMediaItemList);
        mMediaAdapter.setOnRecyclerItemClickListener(new MediaAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(int position) {
                PlayActivity.actionStart(LocalSearchActivity.this,mMediaItemList.get(position).getFolderItemID(),mMediaItemList.get(position).getPosition());
            }
        });
        recyclerView.setAdapter(mMediaAdapter);
    }
}
