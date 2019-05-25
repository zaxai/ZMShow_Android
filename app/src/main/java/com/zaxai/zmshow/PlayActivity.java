package com.zaxai.zmshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class PlayActivity extends AppCompatActivity {
    public final static String EXTRA_FOLDER_ITEM_ID="folder_item_id";
    public final static String EXTRA_CURRENT_WINDOW_INDEX="current_window_index";
    private FolderItem mFolderItem;
    private int mCurrentPlayIndex;
    private int mLastPlayIndex;
    private long mLastPlayPositionMs;
    private PlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;
    ConcatenatingMediaSource mMediaSources;
    PlayEventListener mPlayEventListener;
    private MediaItemDB mMediaItemDB;
    private FolderItemDB mFolderItemDB;
    boolean mIsTurnedFromButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);
        mCurrentPlayIndex=getIntent().getIntExtra(EXTRA_CURRENT_WINDOW_INDEX,0);
        mMediaItemDB=new MediaItemDB(this);
        mFolderItemDB=new FolderItemDB(this);
        int folderItemID=getIntent().getIntExtra(EXTRA_FOLDER_ITEM_ID,-1);
        if(folderItemID!=-1){
            List<FolderItem> folderItemList=mFolderItemDB.selectFull(String.format("SELECT * FROM FolderItem WHERE ID=%d",folderItemID));
            if(folderItemList.size()==1)
                mFolderItem=folderItemList.get(0);
        }
        mIsTurnedFromButton=false;
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPlayer!=null) {
            int nState = mPlayer.getPlaybackState();
            if (nState != Player.STATE_IDLE && nState != Player.STATE_ENDED) {
                long lastPlayPositionMs = mPlayer.getCurrentPosition();
                saveLastPlayPositionMs(mCurrentPlayIndex, lastPlayPositionMs);
                setFinalPlayedMediaItem(mCurrentPlayIndex);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static boolean actionStart(Context context, int folderItemID,int currentWindowIndex){
        if(Build.VERSION.SDK_INT>=21) {
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra(EXTRA_FOLDER_ITEM_ID, folderItemID);
            intent.putExtra(EXTRA_CURRENT_WINDOW_INDEX, currentWindowIndex);
            context.startActivity(intent);
            return true;
        }else{
            Toast.makeText(context,"无法播放，请升级到安卓5.0以上系统",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean actionStartForResult(Activity activity, int folderItemID, int currentWindowIndex, int requestCode){
        if(Build.VERSION.SDK_INT>=21) {
            Intent intent = new Intent(activity, PlayActivity.class);
            intent.putExtra(EXTRA_FOLDER_ITEM_ID, folderItemID);
            intent.putExtra(EXTRA_CURRENT_WINDOW_INDEX, currentWindowIndex);
            activity.startActivityForResult(intent, requestCode);
            return true;
        }else{
            Toast.makeText(activity,"无法播放，请升级到安卓5.0以上系统",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void initView(){
        mPlayerView=(PlayerView)findViewById(R.id.player_view);
        mPlayerView.setTitle(mFolderItem.getMediaItemList().get(mCurrentPlayIndex).getItemName());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕常亮
    }

    private void initPlayer(){
        if(mPlayer==null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
            mPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            mPlayerView.setPlayer(mPlayer);
            mPlayerView.setControllerButtonClickListener(new ControllerButtonClickListener());
            List<MediaSource> mediaSourceList = new ArrayList<>();
            for (MediaItem mediaItem : mFolderItem.getMediaItemList()) {
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "ZMShow"));
                Uri mediaUri = Uri.parse(mediaItem.getItemPath());
                mediaSourceList.add(new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mediaUri));
            }
            mMediaSources = new ConcatenatingMediaSource();
            mMediaSources.addMediaSources(mediaSourceList);
            mPlayEventListener = new PlayEventListener();
            mPlayer.addListener(mPlayEventListener);
            long positionMs=C.TIME_UNSET;
            SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
            if(preferences.getBoolean("local_play_progress",true)){
                positionMs=mFolderItem.getMediaItemList().get(mCurrentPlayIndex).getLastPlayPositionMs();
            }
            mPlayer.seekTo(mCurrentPlayIndex, positionMs);
            mPlayer.prepare(mMediaSources, false, false);
            mPlayer.setPlayWhenReady(true);
        }
        if(Build.VERSION.SDK_INT>=19) {
            mPlayerView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }else{
            mPlayerView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    private void releasePlayer(){
        if(mPlayer!=null){
            mPlayer.stop();
            mPlayer.removeListener(mPlayEventListener);
            mPlayer.release();
            mPlayer=null;
        }
    }

    private void saveLastPlayPositionMs(int windowIndex,long lastPlayPositionMs){
        mFolderItem.getMediaItemList().get(windowIndex).setLastPlayPositionMs(lastPlayPositionMs);
        mMediaItemDB.update(mFolderItem.getMediaItemList().get(windowIndex));
    }

    private void setFinalPlayedMediaItem(int windowIndex){
        List<MediaItem> mediaItemList=mMediaItemDB.select("SELECT * FROM MediaItem WHERE IsFinalPlayed =1");
        for(MediaItem mediaItem:mediaItemList)
            mediaItem.setFinalPlayed(0);
        mMediaItemDB.update(mediaItemList);
        mFolderItem.getMediaItemList().get(windowIndex).setFinalPlayed(1);
        mMediaItemDB.update(mFolderItem.getMediaItemList().get(windowIndex));
    }

    class PlayEventListener implements Player.EventListener{
        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            int windowIndex = mPlayer.getCurrentWindowIndex();
            if(windowIndex!=C.INDEX_UNSET&&windowIndex!=mCurrentPlayIndex) {
                if(mIsTurnedFromButton){
                    mIsTurnedFromButton=false;
                }else{
                    mLastPlayIndex=mPlayer.getPreviousWindowIndex();
                    mLastPlayPositionMs=C.TIME_UNSET;
                }
                saveLastPlayPositionMs(mLastPlayIndex,mLastPlayPositionMs);//保存上一项目播放进度
                mPlayerView.setTitle(mFolderItem.getMediaItemList().get(windowIndex).getItemName());
                long positionMs=C.TIME_UNSET;
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(PlayActivity.this);
                if(preferences.getBoolean("local_play_progress",true)){
                    positionMs=mFolderItem.getMediaItemList().get(windowIndex).getLastPlayPositionMs();
                }
                mPlayer.seekTo(positionMs);
                mCurrentPlayIndex=windowIndex;
            }
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch(playbackState){
                case Player.STATE_ENDED:{
                    saveLastPlayPositionMs(mCurrentPlayIndex,C.TIME_UNSET);
                }
                break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Toast.makeText(PlayActivity.this,"播放出错",Toast.LENGTH_SHORT).show();
            int windowIndex = mPlayer.getCurrentWindowIndex();
            if(windowIndex!=C.INDEX_UNSET&&windowIndex!=mCurrentPlayIndex) {
                if(mIsTurnedFromButton){
                    mIsTurnedFromButton=false;
                }else{
                    mLastPlayIndex=mPlayer.getPreviousWindowIndex();
                    mLastPlayPositionMs=C.TIME_UNSET;
                }
                saveLastPlayPositionMs(mLastPlayIndex,mLastPlayPositionMs);//保存上一项目播放进度
            }
            int nextWindowIndex = mPlayer.getNextWindowIndex();
            if(nextWindowIndex!=C.INDEX_UNSET) {
                mPlayer.seekTo(nextWindowIndex, C.TIME_UNSET);
                mPlayer.prepare(mMediaSources, false, false);
                mPlayer.setPlayWhenReady(true);
            }else{
                finish();
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    }

    class ControllerButtonClickListener implements PlayerControlView.ButtonClickListener{
        @Override
        public void onButtonClick(int buttonType) {
            switch(buttonType){
                case PlayerControlView.BUTTON_TYPE_NEXT:
                case PlayerControlView.BUTTON_TYPE_PREVIOUS:{
                    mIsTurnedFromButton=true;
                    mLastPlayIndex=mCurrentPlayIndex;
                    int nState=mPlayer.getPlaybackState();
                    if(nState!=Player.STATE_IDLE && nState!=Player.STATE_ENDED) {
                        mLastPlayPositionMs = mPlayer.getCurrentPosition();
                    }else{
                        mLastPlayPositionMs=C.TIME_UNSET;
                    }
                }
                break;
            }
        }
    }
}
