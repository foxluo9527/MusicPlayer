package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.ui.activity.ManageSongListActivity;
import com.example.musicplayerdome.ui.activity.MultipleMusicActivity;

import java.util.ArrayList;
import java.util.List;

public class MusicMultipleAdapter extends BaseAdapter {
    private ArrayList<Music> musicList;
    private Context context;
    private View.OnClickListener listener;

    public MusicMultipleAdapter(ArrayList<Music> musicList, Context context, View.OnClickListener listener) {
        this.musicList = musicList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getCount() { return musicList.size(); }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Music music=musicList.get(position);
        ViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.choice_music_item, parent, false);
            holder=new ViewHolder();
            holder.mainView=convertView.findViewById(R.id.music_item_main_view);
            holder.checkBox=convertView.findViewById(R.id.choice);
            holder.musicName=convertView.findViewById(R.id.music_name);
            holder.musicSinger=convertView.findViewById(R.id.music_singer);
            holder.downDoneImg=convertView.findViewById(R.id.down_load_done);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        if (music.getProgress()==100){
            holder.downDoneImg.setVisibility(View.VISIBLE);
        }else {
            holder.downDoneImg.setVisibility(View.GONE);
        }
        holder.mainView.setTag(position);
        holder.mainView.setOnClickListener(listener);
        holder.musicName.setText(music.getName());
        holder.musicSinger.setText(music.getSinger());
        holder.checkBox.setChecked(MultipleMusicActivity.choices[position]);
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(listener);
        return convertView;
    }
    private class ViewHolder{
        View mainView;
        CheckBox checkBox;
        TextView musicName,musicSinger;
        ImageView downDoneImg;
    }
}
