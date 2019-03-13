package com.zaxai.zmshow;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zaxai.zapp.ZFFAdapter;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter <FolderAdapter.ViewHolder> {
    private List<FolderItem> mFolderItemList;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;
    class ViewHolder extends RecyclerView.ViewHolder{
        View mItemView;
        ImageView mItemImage;
        TextView mItemName;
        TextView mItemInfo;
        public ViewHolder(View view) {
            super(view);
            mItemView = view;
            mItemImage = (ImageView) view.findViewById(R.id.folder_item_image);
            mItemName = (TextView) view.findViewById(R.id.folder_item_name);
            mItemInfo = (TextView) view.findViewById(R.id.folder_item_info);
        }
    }
    public FolderAdapter(List<FolderItem> folderItemList){mFolderItemList=folderItemList;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.folder_item,viewGroup,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                if (mOnRecyclerItemClickListener != null)
                    mOnRecyclerItemClickListener.onRecyclerItemClick(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FolderItem folderItem=mFolderItemList.get(position);
        holder.mItemImage.setImageResource(ResourceHelper.getResourceIdByImageId(folderItem.getImageID()));
        holder.mItemName.setText(folderItem.getItemName());
        holder.mItemInfo.setText(folderItem.getItemInfo());
        Drawable drawable=holder.mItemImage.getDrawable().mutate();
        if(drawable!=null&& Build.VERSION.SDK_INT>=21)
            drawable.setTint(ColorHelper.DKGRAY);
        holder.mItemName.setTextColor(ColorHelper.DKGRAY);
        holder.mItemInfo.setTextColor(ColorHelper.GRAY);
        for(MediaItem mediaItem:folderItem.getMediaItemList()){
            if(mediaItem.isFinalPlayed()==1){
                if(drawable!=null&& Build.VERSION.SDK_INT>=21)
                    drawable.setTint(ColorHelper.ORANGE);
                holder.mItemName.setTextColor(ColorHelper.ORANGE);
                holder.mItemInfo.setTextColor(ColorHelper.LTORANGE);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFolderItemList.size();
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(int position);
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener l) {
        mOnRecyclerItemClickListener = l;
    }

}
