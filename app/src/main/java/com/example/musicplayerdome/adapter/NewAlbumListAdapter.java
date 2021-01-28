package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.MyAlbumBean;
import com.example.musicplayerdome.ui.activity.HomeActivity;
import com.example.musicplayerdome.ui.fragment.FragmentRecommend;
import com.example.musicplayerdome.utils.ImgHelper;

import java.util.ArrayList;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class NewAlbumListAdapter extends RecyclerView.Adapter<NewAlbumListAdapter.NewAlbumHolder> {
    ArrayList<MyAlbumBean> newAlbums;
    Context context;

    public NewAlbumListAdapter(ArrayList<MyAlbumBean> newAlbums, Context context) {
        this.newAlbums = newAlbums;
        this.context = context;
    }

    @NonNull
    @Override
    public NewAlbumListAdapter.NewAlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.new_album_item_view, parent, false);
        return new NewAlbumHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewAlbumListAdapter.NewAlbumHolder holder, int position) {
        MyAlbumBean myAlbumBean=newAlbums.get(position);
        if (myAlbumBean.getName()!=null){
            holder.albumName.setText(myAlbumBean.getName());
        }
        if (myAlbumBean.getSinger()!=null){
            holder.albumSinger.setText(myAlbumBean.getSinger());
        }
        if (myAlbumBean.getCover().contains("http")&&!getProxy().isCached(myAlbumBean.getCover())) {
            String url= getProxy().getProxyUrl(myAlbumBean.getCover());
            myAlbumBean.setCover(url);
        }
        Glide.with(context)
                .load(myAlbumBean.getCover())
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .override(300,300)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap srcBitmap = ImgHelper.drawableToBitmap(resource);
                        srcBitmap=getRoundedCornerBitmap(srcBitmap,12f);
                        holder.coverImg.setImageBitmap(srcBitmap);
                    }
                });
        if (mOnRecyclerViewItemListener != null){
            itemOnClick(holder);
        }
    }
    public class NewAlbumHolder extends RecyclerView.ViewHolder {
        ImageView coverImg;
        TextView albumName,albumSinger;
        public NewAlbumHolder(@NonNull View itemView) {
            super(itemView);
            coverImg=itemView.findViewById(R.id.cover_img);
            albumName=itemView.findViewById(R.id.album_name);
            albumSinger=itemView.findViewById(R.id.album_singer);
        }
    }
    @Override
    public int getItemCount() {
        return newAlbums.size();
    }
    public interface OnRecyclerViewItemListener {
        public void onItemClickListener(View view, int position);
    }

    private OnRecyclerViewItemListener mOnRecyclerViewItemListener;

    public void setOnRecyclerViewItemListener(OnRecyclerViewItemListener listener){
        mOnRecyclerViewItemListener = listener;
    }
    private void itemOnClick(final RecyclerView.ViewHolder holder){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.setTag(FragmentRecommend.newAlbumItemClickTAG);
                int pos = holder.getLayoutPosition();
                mOnRecyclerViewItemListener.onItemClickListener(holder.itemView, pos);
            }
        });
    }
    private HttpProxyCacheServer getProxy() {
        long size= App.getShared().getLong("cacheCapacity",0b10000000000000000000000000000000l);
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(size)       // 2 Gb for cache
                .build();
    }
}
