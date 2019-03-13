package com.zaxai.zmshow;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MeAdapter extends RecyclerView.Adapter<MeAdapter.ViewHolder> {
    private List<MeItem> mMeItemList;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View mItemView;
        ImageView mItemImage;
        TextView mItemName;
        public ViewHolder(View view) {
            super(view);
            mItemView = view;
            mItemImage = (ImageView) view.findViewById(R.id.me_item_image);
            mItemName = (TextView) view.findViewById(R.id.me_item_name);
        }
    }

    public MeAdapter(List<MeItem> meItemList){
        mMeItemList=meItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.me_item,viewGroup,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                if(mOnRecyclerItemClickListener!=null)
                    mOnRecyclerItemClickListener.onRecyclerItemClick(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MeItem meItem=mMeItemList.get(position);
        holder.mItemImage.setImageResource(meItem.getImageID());
        holder.mItemName.setText(meItem.getItemName());
    }

    @Override
    public int getItemCount() {
        return mMeItemList.size();
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(int position);
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener l) {
        mOnRecyclerItemClickListener = l;
    }
}
