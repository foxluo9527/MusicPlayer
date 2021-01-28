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
import com.example.musicplayerdome.bean.TopListBean;
import com.example.musicplayerdome.utils.GlideUtil;
import com.example.musicplayerdome.utils.ImgHelper;

import java.util.ArrayList;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class RankListAdapter extends BaseAdapter {
    ArrayList<TopListBean> topListBeans;
    Context context;

    public RankListAdapter(ArrayList<TopListBean> topListBeans, Context context) {
        this.topListBeans = topListBeans;
        this.context = context;
    }

    @Override
    public int getCount() {
        return topListBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return topListBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TopListBean topListBean=topListBeans.get(position);
        ViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.rank_list_litem_view, parent, false);
            holder=new ViewHolder();
            holder.coverImg=convertView.findViewById(R.id.list_cover_img);
            holder.updateTv=convertView.findViewById(R.id.update_text);
            holder.firstTv=convertView.findViewById(R.id.first_task_texgt);
            holder.secondTv=convertView.findViewById(R.id.second_task_text);
            holder.thirdTv=convertView.findViewById(R.id.third_task_text);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        if (topListBean.getTaskBeans().size()>0){
            holder.firstTv.setText("1."+topListBean.getTaskBeans().get(0).getName()+"-"+topListBean.getTaskBeans().get(0).getSinger());
        }else {
            holder.firstTv.setText(topListBean.getName());
        }
        if (topListBean.getTaskBeans().size()>1){
            holder.secondTv.setText("2."+topListBean.getTaskBeans().get(1).getName()+"-"+topListBean.getTaskBeans().get(1).getSinger());
        }else {
            holder.secondTv.setText("");
        }
        if (topListBean.getTaskBeans().size()>2){
            holder.thirdTv.setText("3."+topListBean.getTaskBeans().get(2).getName()+"-"+topListBean.getTaskBeans().get(2).getSinger());
        }else {
            holder.thirdTv.setText("");
        }
        if (topListBean.getPicUrl().contains("http")&&!getProxy().isCached(topListBean.getPicUrl())){
            topListBean.setPicUrl(getProxy().getProxyUrl(topListBean.getPicUrl()));
        }
        Glide.with(context)
                .load(topListBean.getPicUrl())
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
        holder.updateTv.setText(topListBean.getUpdateString());
        return convertView;
    }
    private class ViewHolder{
        TextView firstTv,secondTv,thirdTv;
        TextView updateTv;
        ImageView coverImg;
    }
    private HttpProxyCacheServer getProxy() {
        long size= App.getShared().getLong("cacheCapacity",0b10000000000000000000000000000000l);
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(size)       // 2 Gb for cache
                .build();
    }
}
