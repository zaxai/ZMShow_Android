package com.zaxai.zmshow;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MeFragment extends Fragment {
    private View mContentView;
    private MeAdapter mMeAdapter;
    private List<MeItem> mMeItemList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mContentView==null) {
            mContentView = inflater.inflate(R.layout.me_fragment, container, false);
            initData();
            initView();
        }
        return mContentView;
    }

    public void initView(){
        Toolbar toolbar=mContentView.findViewById(R.id.me_toolbar);
        toolbar.setTitle("我");
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        RecyclerView recyclerView=mContentView.findViewById(R.id.me_list_view);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mMeAdapter=new MeAdapter(mMeItemList);
        mMeAdapter.setOnRecyclerItemClickListener(new MeAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(int position) {
                switch(position){
                    case 0:ShareActivity.actionStart(getContext());break;
                    case 1:SuggestActivity.actionStart(getContext());break;
                    case 2:SettingsActivity.actionStart(getContext());break;
                    case 3:AboutActivity.actionStart(getContext());break;
                    default:Toast.makeText(getContext(),"功能开发中",Toast.LENGTH_SHORT).show();break;
                }
            }
        });
        recyclerView.setAdapter(mMeAdapter);
    }

    public void initData(){
        if(mMeItemList==null){
            mMeItemList=new ArrayList<>();
            mMeItemList.add(new MeItem("分享", R.drawable.ic_zmshow_item_share));
            mMeItemList.add(new MeItem("意见", R.drawable.ic_zmshow_item_suggest));
            mMeItemList.add(new MeItem("设置", R.drawable.ic_zmshow_item_setting));
            mMeItemList.add(new MeItem("关于", R.drawable.ic_zmshow_item_about));
        }
    }
}
