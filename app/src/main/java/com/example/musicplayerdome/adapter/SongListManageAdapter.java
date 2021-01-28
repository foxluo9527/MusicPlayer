package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
import com.example.musicplayerdome.ui.activity.ManageSongListActivity;
import com.example.musicplayerdome.utils.ImgHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class SongListManageAdapter extends BaseAdapter {
    private ArrayList<SongList> songLists;
    private Context context;
    private View.OnClickListener listener;

    public SongListManageAdapter(ArrayList<SongList> songLists, Context context, View.OnClickListener listener) {
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
            convertView= LayoutInflater.from(context).inflate(R.layout.song_manage_list_view_item, parent, false);
            holder=new ViewHolder();
            holder.mainView=convertView.findViewById(R.id.list_item_view);
            holder.checkBox=convertView.findViewById(R.id.checkBox);
            holder.listImg=convertView.findViewById(R.id.list_cover_img);
            holder.listName=convertView.findViewById(R.id.song_list_name);
            holder.listMsg=convertView.findViewById(R.id.song_list_msg);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
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
        Glide.with(context)
                .load(songList.getCoverImg())
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap srcBitmap = ImgHelper.drawableToBitmap(resource);
                        srcBitmap=getRoundedCornerBitmap(srcBitmap,7f);
                        holder.listImg.setImageBitmap(srcBitmap);
                    }
                });
        holder.mainView.setTag(position);
        holder.mainView.setOnClickListener(listener);
        holder.listName.setText(songList.getName());
        holder.checkBox.setChecked(ManageSongListActivity.choices[position]);
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(listener);
        return convertView;
    }
    private class ViewHolder{
        View mainView;
        CheckBox checkBox;
        TextView listName,listMsg;
        ImageView listImg;
    }
}
