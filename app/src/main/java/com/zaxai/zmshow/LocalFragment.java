package com.zaxai.zmshow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Util;
import com.zaxai.util.ZFileFind;
import com.zaxai.zapp.ZFFDialogFragment;
import com.zaxai.zapp.ZFolderDialogFragment;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static android.content.Context.STORAGE_SERVICE;

public class LocalFragment extends Fragment {
    public final static int REQUEST_CODE_PLAY=0;
    public final static int REQUEST_CODE_MEDIA=1;
    private View mContentView;
    private FolderAdapter mFolderAdapter;
    private List<FolderItem> mFolderItemList;
    private int mIndexOfFolderItem;
    private MediaItemDB mMediaItemDB;
    private FolderItemDB mFolderItemDB;
    private SwipeRefreshLayout mFolderListRefresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mContentView==null) {
            mContentView = inflater.inflate(R.layout.local_fragment, container, false);
            initData();
            initView();
        }
        return mContentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.local_toolbar,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_folder:{
                String[] paths = getStoragePaths();
                if (paths != null && paths.length > 0) {
                    ZFolderDialogFragment folderDialog = new ZFolderDialogFragment();
                    folderDialog.setTitle("添加目录")
                            .setTitleColor(ColorHelper.ORANGE)
                            .setBackColor(ColorHelper.ORANGE)
                            .setPositiveButton("确定", new ZFFDialogFragment.OnClickListener() {
                                @Override
                                public void onClick(String[] paths) {
                                    addFolder(paths);
                                }
                            })
                            .show(getFragmentManager(), paths[0]);
                }
            }
            break;
            case R.id.search:
                LocalSearchActivity.actionStart(getContext());
                break;
            case R.id.setting:
                SettingsActivity.actionStart(getContext());
                break;
            default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void initView(){
        setHasOptionsMenu(true);
        Toolbar toolbar=mContentView.findViewById(R.id.local_toolbar);
        toolbar.setTitle("本地");
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        RecyclerView recyclerView=(RecyclerView)mContentView.findViewById(R.id.local_list_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mFolderAdapter=new FolderAdapter(mFolderItemList);
        mFolderAdapter.setOnRecyclerItemClickListener(new FolderAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(int position) {
                mIndexOfFolderItem=position;
                MediaActivity.actionStartForResult(LocalFragment.this,mFolderItemList.get(position).getID(),REQUEST_CODE_MEDIA);
            }
        });
        recyclerView.setAdapter(mFolderAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(new LocalFragment.RecyclerViewCallback());
        helper.attachToRecyclerView(recyclerView);
        mFolderListRefresh=(SwipeRefreshLayout)mContentView.findViewById(R.id.local_list_refresh);
        mFolderListRefresh.setColorSchemeResources(R.color.colorOrange);
        mFolderListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean isNeedSetChanged=false;
                for(FolderItem folderItem:mFolderItemList){
                    if(refreshFolderItem(folderItem,getActivity()))
                        isNeedSetChanged=true;
                }
                if(isNeedSetChanged)
                    mFolderAdapter.notifyDataSetChanged();
                mFolderListRefresh.setRefreshing(false);
            }
        });
        FloatingActionButton fab=(FloatingActionButton)mContentView.findViewById(R.id.local_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MediaItem> mediaItemList=mMediaItemDB.select("SELECT * FROM MediaItem WHERE IsFinalPlayed =1 LIMIT 1");
                if(mediaItemList.size()==1){
                    PlayActivity.actionStartForResult(getActivity(),mediaItemList.get(0).getFolderItemID(),mediaItemList.get(0).getPosition(),REQUEST_CODE_PLAY);
                }else {
                    Snackbar.make(v, "没有播放项目", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initData(){
        if(mFolderItemList==null) {
            mMediaItemDB=new MediaItemDB(getContext());
            mFolderItemDB = new FolderItemDB(getContext());
            mFolderItemList = mFolderItemDB.selectFull("SELECT * FROM FolderItem ORDER BY Position");
        }
    }

    public String[] getStoragePaths() {
        try {
            StorageManager sm = (StorageManager) getActivity().getSystemService(STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm);
            return paths;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addFolder(String[] paths){
        for (String path : paths) {
            if(!isPathInList(path)) {
                int ID=FolderItemDB.getNextInsertID(getContext());
                File file = new File(path);
                List<MediaItem> mediaItemList=getMediaItemList(path,ID,getActivity());
                String info = "";
                if (file.list() != null)
                    info = String.format("%d个文件", mediaItemList.size());
                FolderItem folderItem = new FolderItem(ID,ID,path, file.getName(), info, ResourceHelper.IMAGE_ID_TIEM_FOLDER,mediaItemList);
                if(mFolderItemDB.insertFull(folderItem)!=-1) {
                    mFolderItemList.add(folderItem);
                }
            }
        }
        mFolderAdapter.notifyDataSetChanged();
    }

    private boolean isPathInList(String path){
        for(FolderItem item:mFolderItemList){
            if(item.getItemPath().equals(path))
                return true;
        }
        return false;
    }

    //同MediaActivity.refreshFolderItem()
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

    //同MediaActivity.getMediaItemList()
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case REQUEST_CODE_PLAY:{

            }
            break;
            case REQUEST_CODE_MEDIA: {
                List<FolderItem> folderItemList=mFolderItemDB.selectFull("SELECT * FROM FolderItem ORDER BY Position");
                mFolderItemList.clear();
                mFolderItemList.addAll(folderItemList);
                mFolderAdapter.notifyDataSetChanged();
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
            mFolderAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            final FolderItem folderItem=mFolderItemList.get(position);
            if(mFolderItemDB.deleteFull(folderItem)==1) {
                mFolderItemList.remove(position);
                mFolderAdapter.notifyItemRemoved(position);
                Snackbar.make(mContentView, "已删除1个项目", Snackbar.LENGTH_SHORT)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(mFolderItemDB.insertFull(folderItem)!=-1) {
                                    mFolderItemList.add(position, folderItem);
                                    mFolderAdapter.notifyItemInserted(position);
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
                mFolderItemList.add(draggingToPosition,mFolderItemList.remove(draggingFromPosition));
                for(int i=0;i<mFolderItemList.size();++i){
                    mFolderItemList.get(i).setPosition(i);
                }
                FolderItemDB.sortPosition(LocalFragment.this.getContext(),mFolderItemList);
                mFolderAdapter.notifyDataSetChanged();
            }
            draggingFromPosition = C.INDEX_UNSET;
            draggingToPosition = C.INDEX_UNSET;
        }

    }
}
