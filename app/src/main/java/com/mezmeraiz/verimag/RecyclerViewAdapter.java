package com.mezmeraiz.verimag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by pc on 04.10.2016.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    ArrayList<Map> mFileList;
    Context mContext;
    OnClickFileListener mListener;

    public RecyclerViewAdapter(Context context, ArrayList<Map> fileList, OnClickFileListener listener) {
        mContext = context;
        mFileList = fileList;
        mListener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        final Map map = mFileList.get(position);
        holder.mCardTextView.setText(String.valueOf(map.get(MainActivity.NAME)));
        if((Boolean)map.get(MainActivity.IS_DIRECTORY)){
            holder.mCardImageView.setBackground(mContext.getResources().getDrawable(R.drawable.folder));
        }else{
            holder.mCardImageView.setBackground(mContext.getResources().getDrawable(R.drawable.file));
        }
        holder.mClickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickFile(String.valueOf(map.get(MainActivity.NAME)));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    public void update(ArrayList<Map> fileList){
        mFileList = fileList;
        notifyDataSetChanged();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        public ImageView mCardImageView;
        public TextView mCardTextView;
        public View mClickView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mCardImageView = (ImageView) itemView.findViewById(R.id.itemImageView);
            mCardTextView = (TextView) itemView.findViewById(R.id.itemTextView);
            mClickView = itemView.findViewById(R.id.relative_layout);
        }
    }
}
