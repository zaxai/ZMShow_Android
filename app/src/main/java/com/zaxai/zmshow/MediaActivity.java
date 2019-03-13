package com.zaxai.zmshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Util;
import com.zaxai.util.ZFileFind;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;


public class MediaActivity extends AppCompatActivity {
    public final static int REQUEST_CODE_PLAY=0;
    public final static String EXTRA_FOLDER_ITEM_ID="folder_item_id";
    private FolderItem mFolderItem;
    private MediaAdapter mMediaAdapter;
    private MediaItemDB mMediaItemDB;
    private FolderItemDB mFolderItemDB;
    private SwipeRefreshLayout mMediaListRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_fragment);
        mMediaItemDB=new MediaItemDB(this);
        mFolderItemDB=new FolderItemDB(this);
        int folderItemID=getIntent().getIntExtra(EXTRA_FOLDER_ITEM_ID,-1);
        if(folderItemID!=-1){
            List<FolderItem> folderItemList=mFolderItemDB.selectFull(String.format("SELECT * FROM FolderItem WHERE ID=%d",folderItemID));
            if(folderItemList.size()==1)
                mFolderItem=folderItemList.get(0);
        }
        initView();
    }

    public static void actionStart(Context context, int folderItemID){
        Intent intent=new Intent(context,MediaActivity.class);
        intent.putExtra(EXTRA_FOLDER_ITEM_ID,folderItemID);
        context.startActivity(intent);
    }

    public static void actionStartForResult(Fragment fragment, int folderItemID, int requestCode){
        Intent intent=new Intent(fragment.getContext(),MediaActivity.class);
        intent.putExtra(EXTRA_FOLDER_ITEM_ID,folderItemID);
        fragment.startActivityForResult(intent,requestCode);
    }

    private void initView(){
        Toolbar toolbar=findViewById(R.id.local_toolbar);
        toolbar.setTitle(mFolderItem.getItemName());
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_zmshow_btn_back_white);
        }
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.local_list_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mMediaAdapter=new MediaAdapter(mFolderItem.getMediaItemList());
        mMediaAdapter.setOnRecyclerItemClickListener(new MediaAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(int position) {
                PlayActivity.actionStartForResult(MediaActivity.this,mFolderItem.getID(),position,REQUEST_CODE_PLAY);
            }
        });
        recyclerView.setAdapter(mMediaAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(new MediaActivity.RecyclerViewCallback());
        helper.attachToRecyclerView(recyclerView);
        mMediaListRefresh=(SwipeRefreshLayout)findViewById(R.id.local_list_refresh);
        mMediaListRefresh.setColorSchemeResources(R.color.colorOrange);
        mMediaListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(refreshFolderItem(mFolderItem,MediaActivity.this))
                    mMediaAdapter.notifyDataSetChanged();
                mMediaListRefresh.setRefreshing(false);
            }
        });
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.local_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MediaItem> mediaItemList=mMediaItemDB.select("SELECT * FROM MediaItem WHERE IsFinalPlayed =1 LIMIT 1");
                if(mediaItemList.size()==1){
                    PlayActivity.actionStartForResult(MediaActivity.this,mediaItemList.get(0).getFolderItemID(),mediaItemList.get(0).getPosition(),REQUEST_CODE_PLAY);
                }else {
                    Snackbar.make(v, "没有播放项目", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    //同LocalFragment.refreshFolderItem()
    private boolean refreshFolderItem(@NonNull FolderItem folderItem,Activity activity){
        boolean isNeedUpdate=false;
        for(int i=folderItem.getMediaItemList().size()-1;i>=0;--i){
            File file=new File(folderItem.getMediaItemList().get(i).getItemPath());
            if(!file.exists()){
                if(mMediaItemDB.delete(folderItem.getMediaItemList().get(i))==1) {
                    folderItem.getMediaItemList().remove(i);
                    isNeedUpdate=true;
                }
            }
        }
        List<MediaItem> mediaItemList=getMediaItemList(folderItem.getItemPath(),folderItem.getID(),activity);
        for(MediaItem mediaItem:mediaItemList){
            List<MediaItem> mediaItems=mMediaItemDB.select(String.format("SELECT * FROM MediaItem WHERE Path = '%s'",mediaItem.getItemPath()));
            if(mediaItems.size()==0){
                if(mMediaItemDB.insert(mediaItem)!=-1) {
                    folderItem.getMediaItemList().add(mediaItem);
                    isNeedUpdate=true;
                }
            }
        }
        if(isNeedUpdate) {
            folderItem.setItemInfo(String.format("%d个文件", folderItem.getMediaItemList().size()));
            mFolderItemDB.updateFull(folderItem);
            for (int i = 0; i < folderItem.getMediaItemList().size(); ++i) {
                folderItem.getMediaItemList().get(i).setPosition(i);
            }
            MediaItemDB.sortPosition(activity, folderItem.getMediaItemList());
        }
        return isNeedUpdate;
    }

    //同LocalFragment.getMediaItemList()
    private List<MediaItem> getMediaItemList(String path,int folderItemID,Activity activity){
        List<MediaItem> mediaItemList=new ArrayList<>();
        List<File> files=new ArrayList<>();
        ZFileFind.browseDirectory(new File(path),files,false);
        Collections.sort(files);
        int ID=MediaItemDB.getNextInsertID(activity);
        int position=0;
        for(File file:files){
            if(!file.isDirectory()){
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(activity);
                if(preferences.getBoolean("local_auto_select",true)){
                    int index=file.getPath().lastIndexOf('.');
                    if(index==-1)
                        continue;
                    String unknowFormat=file.getPath().substring(index);
                    if(!ExoPlayerFormatsHelper.isFormatSupported(unknowFormat)){
                        continue;
                    }
                }
                String itemInfo;
                MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
                try {
                    mediaMetadataRetriever.setDataSource(file.getPath());
                    String w=mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                    String h=mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                    String duration=mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    if(w==null)
                        w="0";
                    if(h==null)
                        h="0";
                    if(duration==null)
                        duration="0";
                    StringBuilder formatBuilder = new StringBuilder();
                    Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
                    String durationMs= Util.getStringForTime(formatBuilder,formatter,Long.parseLong(duration));
                    itemInfo=String.format("%s×%s %s",w,h,durationMs);
                }catch(Exception e){
                    itemInfo="文件解码失败";
                }finally {
                    mediaMetadataRetriever.release();
                }
                MediaItem mediaItem=new MediaItem(ID,position,file.getPath(),file.getName(),itemInfo,ResourceHelper.IMAGE_ID_ITEM_FILE,folderItemID,C.TIME_UNSET,0);
                mediaItemList.add(mediaItem);
                ++ID;
                ++position;
            }
        }
        return mediaItemList;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case REQUEST_CODE_PLAY: {
                List<FolderItem> folderItemList=mFolderItemDB.selectFull(String.format("SELECT * FROM FolderItem WHERE ID=%d",mFolderItem.getID()));
                if(folderItemList.size()==1) {
                    mFolderItem.getMediaItemList().clear();
                    mFolderItem.getMediaItemList().addAll(folderItemList.get(0).getMediaItemList());
                    mMediaAdapter.notifyDataSetChanged();
                }
            }
            break;
        }
    }

    private class RecyclerViewCallback extends ItemTouchHelper.SimpleCallback {

        private int draggingFromPosition;
        private int draggingToPosition;

        public RecyclerViewCallback() {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN|ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.START | ItemTouchHelper.END);
            draggingFromPosition = C.INDEX_UNSET;
            draggingToPosition = C.INDEX_UNSET;
        }

        @Override
        public boolean onMove(RecyclerView list, RecyclerView.ViewHolder origin,
                              RecyclerView.ViewHolder target) {
            int fromPosition = origin.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (draggingFromPosition == C.INDEX_UNSET) {
                // A drag has started, but changes to the media queue will be reflected in clearView().
                draggingFromPosition = fromPosition;
            }
            draggingToPosition = toPosition;
            mMediaAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            final MediaItem mediaItem= mFolderItem.getMediaItemList().get(position);
            if(mMediaItemDB.delete(mediaItem)==1) {
                mFolderItem.getMediaItemList().remove(position);
                mFolderItem.setItemInfo(String.format("%d个文件", mFolderItem.getMediaItemList().size()));
                mFolderItemDB.updateFull(mFolderItem);
                for(int i=0;i<mFolderItem.getMediaItemList().size();++i){
                    mFolderItem.getMediaItemList().get(i).setPosition(i);
                }
                MediaItemDB.sortPosition(MediaActivity.this,mFolderItem.getMediaItemList());
                mMediaAdapter.notifyItemRemoved(position);
                Snackbar.make(findViewById(R.id.local_list_view), "已删除1个项目", Snackbar.LENGTH_SHORT)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(mMediaItemDB.insert(mediaItem)!=-1) {
                                    mFolderItem.getMediaItemList().add(position, mediaItem);
                                    mFolderItem.setItemInfo(String.format("%d个文件", mFolderItem.getMediaItemList().size()));
                                    mFolderItemDB.updateFull(mFolderItem);
                                    for(int i=0;i<mFolderItem.getMediaItemList().size();++i){
                                        mFolderItem.getMediaItemList().get(i).setPosition(i);
                                    }
                                    MediaItemDB.sortPosition(MediaActivity.this,mFolderItem.getMediaItemList());
                                    mMediaAdapter.notifyItemInserted(position);
                                }
                            }
                        })
                        .show();
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            if (draggingFromPosition != C.INDEX_UNSET) {
                // A drag has ended. We reflect the media queue change in the player.
                mFolderItem.getMediaItemList().add(draggingToPosition,mFolderItem.getMediaItemList().remove(draggingFromPosition));
                for(int i=0;i<mFolderItem.getMediaItemList().size();++i){
                    mFolderItem.getMediaItemList().get(i).setPosition(i);
                }
                MediaItemDB.sortPosition(MediaActivity.this,mFolderItem.getMediaItemList());
                mMediaAdapter.notifyDataSetChanged();
            }
            draggingFromPosition = C.INDEX_UNSET;
            draggingToPosition = C.INDEX_UNSET;
        }

    }
}
