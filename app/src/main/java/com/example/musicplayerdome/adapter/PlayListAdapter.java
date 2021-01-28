package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.activity.HomeActivity;

import java.util.List;

public class PlayListAdapter extends BaseAdapter {
    private static final String TAG = "playListAdapter";
    private List<Music> listMusic;
    private Context context;
    private View.OnClickListener listener;
    public PlayListAdapter(Context context, List<Music> listMusic,View.OnClickListener listener){
        this.context = context;
        this.listMusic = listMusic;
        this.listener=listener;
    }
    @Override
    public int getCount() {
        return listMusic.size();
    }

    @Override
    public Object getItem(int position) {
        return listMusic.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Music music=listMusic.get(position);
        ViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.music_list_popup_item, parent, false);
            holder=new ViewHolder();
            holder.songView=convertView.findViewById(R.id.song_list_item);
            holder.delImg=convertView.findViewById(R.id.img_del);
            holder.nameTv=convertView.findViewById(R.id.text_song_name);
            holder.singerTv=convertView.findViewById(R.id.text_singer);
            holder.onPlay=convertView.findViewById(R.id.img_on_play);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        if (MusicService.listMusic!=null&&MusicService.listMusic.size()>0){
            int index=MusicService.position;
            if (index<0) index=0;
            if (music.getId().equals(MusicService.listMusic.get(index).getId())){
                holder.onPlay.setVisibility(View.VISIBLE);
            }else {
                holder.onPlay.setVisibility(View.INVISIBLE);
            }
            holder.delImg.setTag(position);
            holder.delImg.setOnClickListener(listener);
            holder.songView.setTag(position);
            holder.songView.setOnClickListener(listener);
            holder.singerTv.setText("-"+music.getSinger());
            holder.nameTv.setText(music.getName());
        }
        return convertView;
    }
    private class ViewHolder{
        View songView;
        TextView nameTv,singerTv;
        ImageView delImg,onPlay;
    }
}
