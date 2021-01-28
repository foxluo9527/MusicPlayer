package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.MyMusicSearchBean;

import java.util.ArrayList;

public class SearchMusicListAdapter extends BaseAdapter {
    Context context;
    View.OnClickListener listener;
    ArrayList<Music> songs;

    public SearchMusicListAdapter(Context context, View.OnClickListener listener, ArrayList<Music> songs) {
        this.context = context;
        this.listener = listener;
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Music song=songs.get(position);
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.search_music_list_item, parent, false);
            holder=new ViewHolder();
            holder.musicMoreView=convertView.findViewById(R.id.music_more_view);
            holder.musicView=convertView.findViewById(R.id.music_play_view);
            holder.nameTv=convertView.findViewById(R.id.music_name);
            holder.singerTv=convertView.findViewById(R.id.music_singer);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.singerTv.setText(song.getSinger());
        holder.nameTv.setText(song.getName());
        holder.musicMoreView.setTag(position);
        holder.musicMoreView.setOnClickListener(listener);
        holder.musicView.setTag(position);
        holder.musicView.setOnClickListener(listener);
        return convertView;
    }
    class ViewHolder{
        View musicView,musicMoreView;
        TextView singerTv,nameTv;
    }
    public String toTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }
}
