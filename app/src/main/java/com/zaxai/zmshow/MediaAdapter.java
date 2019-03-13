package com.zaxai.zmshow;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private List<MediaItem> mMediaItemList;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View mItemView;
        ImageView mItemImage;
        TextView mItemName;
        TextView mItemInfo;
        public ViewHolder(View view) {
            super(view);
            mItemView = view;
            mItemImage = (ImageView) view.findViewById(R.id.media_item_image);
            mItemName = (TextView) view.findViewById(R.id.media_item_name);
            mItemInfo = (TextView) view.findViewById(R.id.media_item_info);
        }
    }

    public MediaAdapter(List<MediaItem> mediaItemList){mMediaItemList=mediaItemList;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.media_item,viewGroup,false);
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
        MediaItem mediaItem=mMediaItemList.get(position);
        RequestOptions requestOptions=new RequestOptions().placeholder(ResourceHelper.getResourceIdByImageId(mediaItem.getImageID())).error(ResourceHelper.getResourceIdByImageId(mediaItem.getImageID())).centerCrop();
        Glide.with(holder.mItemView).load(mediaItem.getItemPath()).apply(requestOptions).into(holder.mItemImage);
        holder.mItemName.setText(mediaItem.getItemName());
        holder.mItemInfo.setText(mediaItem.getItemInfo());
        if(mediaItem.isFinalPlayed()==1){
            holder.mItemName.setTextColor(ColorHelper.ORANGE);
            holder.mItemInfo.setTextColor(ColorHelper.LTORANGE);
        }else{
            holder.mItemName.setTextColor(ColorHelper.DKGRAY);
            holder.mItemInfo.setTextColor(ColorHelper.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return mMediaItemList.size();
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(int position);
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener l) {
        mOnRecyclerItemClickListener = l;
    }
}
