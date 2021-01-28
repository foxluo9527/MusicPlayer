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
import com.example.musicplayerdome.bean.PlayListSearchBean;
import com.example.musicplayerdome.ui.activity.PlayOnlineListActivity;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.ImgHelper;

import java.util.ArrayList;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class SearchPlayListAdapter extends BaseAdapter {
    ArrayList<PlayListSearchBean.ResultBean.PlaylistsBean> playlistsBeans;
    Context context;

    public SearchPlayListAdapter(ArrayList<PlayListSearchBean.ResultBean.PlaylistsBean> playlistsBeans, Context context) {
        this.playlistsBeans = playlistsBeans;
        this.context = context;
    }

    @Override
    public int getCount() {
        return playlistsBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return playlistsBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlayListSearchBean.ResultBean.PlaylistsBean playlistsBean=playlistsBeans.get(position);
        ViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false);
            holder=new ViewHolder();
            holder.nameTv=convertView.findViewById(R.id.list_name);
            holder.msgTv=convertView.findViewById(R.id.list_msg);
            holder.coverImg=convertView.findViewById(R.id.cover_img);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        String listMsg=playlistsBean.getTrackCount()+"首 ";
        if (playlistsBean.getCreator()!=null){
            listMsg+="by "+playlistsBean.getCreator().getNickname();
        }
        listMsg+=",播放"+ PlayOnlineListActivity.formatCount(playlistsBean.getPlayCount())+"次";
        holder.msgTv.setText(listMsg);
        holder.nameTv.setText(playlistsBean.getName());
        if (playlistsBean.getCoverImgUrl().contains("http")&&!getProxy().isCached(playlistsBean.getCoverImgUrl())) {
            String url= getProxy().getProxyUrl(playlistsBean.getCoverImgUrl());
            playlistsBean.setCoverImgUrl(url);
        }
        Glide.with(context)
                .load(playlistsBean.getCoverImgUrl())
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
        TextView nameTv,msgTv;
    }
    private HttpProxyCacheServer getProxy() {
        long size= App.getShared().getLong("cacheCapacity",0b10000000000000000000000000000000l);
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(size)       // 2 Gb for cache
                .build();
    }
}
