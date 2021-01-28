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
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.ui.activity.HomeActivity;
import com.example.musicplayerdome.utils.ImgHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class SongListAdapter extends BaseAdapter {
    private ArrayList<SongList> songLists;
    private Context context;
    private View.OnClickListener listener;
    public SongListAdapter(ArrayList<SongList> songLists, Context context, View.OnClickListener listener) {
        this.songLists = songLists;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getCount() { return songLists.size(); }

    @Override
    public Object getItem(int position) {
        return songLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SongList songList=songLists.get(position);
        List<Music> musics=songList.getMusics();
        ViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.song_list_item, parent, false);
            holder=new ViewHolder();
            holder.listImg=convertView.findViewById(R.id.song_list_img);
            holder.listMsg=convertView.findViewById(R.id.song_list_msg);
            holder.listName=convertView.findViewById(R.id.song_list_name);
            holder.moreImg=convertView.findViewById(R.id.img_song_list_more);
            holder.onPlayImg=convertView.findViewById(R.id.img_on_play);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.listName.setText(songList.getName());
        if (musics.size()!=0){
            int count=musics.size();
            int downCount=0;//当前列表中已经下载的歌曲数
            for (Music m:
                 musics) {
                if (m.getProgress()==100){
                    downCount+=1;
                }
            }
            if (downCount>0) holder.listMsg.setText(count+"首"+",已下载"+downCount+"首");
            else holder.listMsg.setText(count+"首");
        }else{
            holder.listMsg.setText("0首");
        }
        if (HomeActivity.nowListIndex>3&&HomeActivity.nowListIndex-4==position){ //播放列表索引为当前歌曲列表，-4是前面四个默认歌曲列表
            holder.onPlayImg.setVisibility(View.VISIBLE);
        }else {
            holder.onPlayImg.setVisibility(View.INVISIBLE);
        }
        Glide.with(context)
                .load(songList.getCoverImg())
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .override(300,300)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap srcBitmap = ImgHelper.drawableToBitmap(resource);
                        srcBitmap=getRoundedCornerBitmap(srcBitmap,20f);
                        holder.listImg.setImageBitmap(srcBitmap);
                    }
                });
        holder.moreImg.setTag(position);
        holder.moreImg.setOnClickListener(listener);
        return convertView;
    }
    private class ViewHolder{
        TextView listName,listMsg;
        ImageView listImg,onPlayImg,moreImg;
    }
}
