package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.activity.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends BaseAdapter {
    private static final String TAG = "MusicAdapterLog";
    private List<Music> listMusic;
    private Context context;
    private View.OnClickListener listener;

    public MusicListAdapter(List<Music> listMusic, Context context, View.OnClickListener listener) {
        this.listMusic = listMusic;
        this.context = context;
        this.listener = listener;
    }
    public ArrayList<Music> getMusics(){
        return (ArrayList<Music>) this.listMusic;
    }
    @Override
    public int getCount() {
        return listMusic.size();
    }

    @Override
    public Object getItem(int arg0) {
        return listMusic.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Music music=listMusic.get(position);
        ViewHolder holder;
        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.music_list_item, parent, false);
            holder=new ViewHolder();
            holder.nameTv=convertView.findViewById(R.id.music_name);
            holder.singerTv=convertView.findViewById(R.id.music_singer);
            holder.downDoneImg=convertView.findViewById(R.id.down_load_done);
            holder.moreView=convertView.findViewById(R.id.music_more_view);
            holder.onPlayImg=convertView.findViewById(R.id.music_onPlay);
            holder.playView=convertView.findViewById(R.id.music_play_view);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        if (music.getProgress()!=100){
            holder.downDoneImg.setImageDrawable(context.getDrawable(R.drawable.undone));
        }else {
            holder.downDoneImg.setImageDrawable(context.getDrawable(R.drawable.done));
        }
        holder.playView.setTag(position);
        holder.playView.setOnClickListener(listener);
        holder.moreView.setTag(position);
        holder.moreView.setOnClickListener(listener);
        Music nowMusic=HomeActivity.musicControl.getMusic();
        if (MusicService.listMusic!=null&&MusicService.mlastPlayer!=null
                && nowMusic.getId().equals(music.getId())){
            holder.onPlayImg.setVisibility(View.VISIBLE);
        }else {
            holder.onPlayImg.setVisibility(View.INVISIBLE);
        }
        holder.nameTv.setText(music.getName());
        holder.singerTv.setText(music.getSinger());
        return convertView;
    }
    class ViewHolder{
        TextView nameTv,singerTv;
        ImageView onPlayImg,downDoneImg;
        View moreView,playView;
    }
}
