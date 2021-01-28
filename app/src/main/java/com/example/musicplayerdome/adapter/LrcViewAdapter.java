package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.musicplayerdome.R;

import java.util.ArrayList;

public class LrcViewAdapter extends BaseAdapter {
    ArrayList<String> lrcs;
    Context context;
    View.OnClickListener listener;

    public LrcViewAdapter(ArrayList<String> lrcs, Context context, View.OnClickListener listener) {
        this.lrcs = lrcs;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return lrcs.size();
    }

    @Override
    public Object getItem(int position) {
        return lrcs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String lrcString=lrcs.get(position);
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.lrc_item, parent, false);
            holder=new ViewHolder();
            holder.mainView=convertView.findViewById(R.id.main_view);
            holder.lrcView=convertView.findViewById(R.id.lrc_view);
            holder.choiceRadio=convertView.findViewById(R.id.radio_choice);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.lrcView.setText(lrcString);
        holder.choiceRadio.setTag(position);
        holder.choiceRadio.setOnClickListener(listener);
        holder.mainView.setTag(position);
        holder.mainView.setOnClickListener(listener);
        return convertView;
    }
    class ViewHolder{
        View mainView;
        TextView lrcView;
        RadioButton choiceRadio;
    }
}
