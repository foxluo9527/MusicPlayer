package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.musicplayerdome.dao.SearchDao.Record;
import com.example.musicplayerdome.R;

import java.util.ArrayList;

public class HotSearchListAdapter extends BaseAdapter {
    ArrayList<Record> searchStrings=new ArrayList<Record>();
    private Context context;
    private LayoutInflater inflater;
    public HotSearchListAdapter(ArrayList<Record> searchStrings, Context context){
        this.searchStrings=searchStrings;
        this.context=context;
        this.inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return searchStrings.size();
    }

    @Override
    public Object getItem(int position) {
        return searchStrings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String searchString=searchStrings.get(position).getRecord();
        ViewHolder holder;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.hot_search_list_item, parent, false);
            holder = new ViewHolder();
            holder.hotSearchTv=convertView.findViewById(R.id.hot_search_text);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.hotSearchTv.setText(searchString);
        return convertView;
    }
    public static class ViewHolder {
        public TextView hotSearchTv;
    }
}
