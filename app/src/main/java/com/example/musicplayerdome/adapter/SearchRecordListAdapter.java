package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.dao.SearchDao;
import com.example.musicplayerdome.dao.SearchDao.Record;
import java.util.ArrayList;

public class SearchRecordListAdapter extends BaseAdapter {
    ArrayList<Record> recordStrings=new ArrayList<Record>();
    private Context context;
    private LayoutInflater inflater;
    public SearchRecordListAdapter(ArrayList<Record> recordStrings,Context context){
        this.recordStrings=recordStrings;
        this.context=context;
        this.inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return recordStrings.size();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        //recordStrings.add("");
    }

    @Override
    public Object getItem(int position) {
        return recordStrings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position!=recordStrings.size()-1){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int maxPosition=recordStrings.size()-1;
        String recordString = recordStrings.get(position).getRecord();
        ViewHolder holder;
        if (convertView==null){
            if (position==maxPosition){
                convertView = inflater.inflate(R.layout.search_record_clean_item, parent, false);
                holder = new ViewHolder();
            }else {
                convertView = inflater.inflate(R.layout.search_record_list_item, parent, false);
                holder = new ViewHolder();
                holder.recordTv=convertView.findViewById(R.id.search_record_text);
                holder.deleteBtn=convertView.findViewById(R.id.delete_item_btn);
                holder.deleteBtn.setTag(position);
            }
            convertView.setTag(holder);
        }else {
            if (position==maxPosition){
                convertView = inflater.inflate(R.layout.search_record_clean_item, parent, false);
                holder = new ViewHolder();
            }else{
                holder=(ViewHolder)convertView.getTag();
            }
        }
        if (position!=maxPosition){
            holder.recordTv.setText(recordString);
        }
        return convertView;
    }
    public static class ViewHolder {
        public TextView recordTv;
        public Button deleteBtn;
    }
}
