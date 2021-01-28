package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.AlbumSearchBean;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.ImgHelper;

import java.util.ArrayList;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class SearchAlbumAdapter extends BaseAdapter {
    ArrayList<AlbumSearchBean.ResultBean.AlbumsBean> albumsBeans;
    Context context;

    public SearchAlbumAdapter(ArrayList<AlbumSearchBean.ResultBean.AlbumsBean> albumsBeans, Context context) {
        this.albumsBeans = albumsBeans;
        this.context = context;
    }

    @Override
    public int getCount() {
        return albumsBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return albumsBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumSearchBean.ResultBean.AlbumsBean albumsBean=albumsBeans.get(position);
        ViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.search_album_item, parent, false);
            holder=new ViewHolder();
            holder.coverImg=convertView.findViewById(R.id.cover_img);
            holder.nameTv=convertView.findViewById(R.id.album_name);
            holder.singerTv=convertView.findViewById(R.id.singer_name);
            holder.dateTv=convertView.findViewById(R.id.time_text);
            holder.onSaleView=convertView.findViewById(R.id.onsale_view);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.nameTv.setText(albumsBean.getName());
        holder.singerTv.setText(albumsBean.getArtist().getName());
        holder.dateTv.setText(HttpRequest.toDate(albumsBean.getPublishTime()));
        if (albumsBean.isOnSale()){
            holder.onSaleView.setVisibility(View.VISIBLE);
        }else {
            holder.onSaleView.setVisibility(View.GONE);
        }
        if (albumsBean.getPicUrl().contains("http")&&!getProxy().isCached(albumsBean.getPicUrl())) {
            String url= getProxy().getProxyUrl(albumsBean.getPicUrl());
            albumsBean.setPicUrl(url);
        }
        Glide.with(context)
                .load(albumsBean.getPicUrl())
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .override(70,70)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap srcBitmap = ImgHelper.drawableToBitmap(resource);
                        srcBitmap=getRoundedCornerBitmap(srcBitmap,7f);
                        holder.coverImg.setImageBitmap(srcBitmap);
                    }
                });
        return convertView;
    }
    class ViewHolder{
        ImageView coverImg;
        TextView nameTv,singerTv,dateTv;
        View onSaleView;
    }
    private HttpProxyCacheServer getProxy() {
        long size= App.getShared().getLong("cacheCapacity",0b10000000000000000000000000000000l);
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(size)       // 2 Gb for cache
                .build();
    }
}
