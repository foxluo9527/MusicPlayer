package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.DownTaskBean;

import java.util.ArrayList;

public class DownLoadListAdapter extends BaseAdapter {
    ArrayList<DownTaskBean> downTaskBeans;
    Context context;
    View.OnClickListener listener;

    public DownLoadListAdapter(ArrayList<DownTaskBean> downTaskBeans, Context context, View.OnClickListener listener) {
        this.downTaskBeans = downTaskBeans;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return downTaskBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return downTaskBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DownTaskBean bean=downTaskBeans.get(position);
        ViewHolder holder;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.down_list_item, null);
            holder=new ViewHolder();
            holder.deleteDown=convertView.findViewById(R.id.btn_delete);
            holder.downProgressView=convertView.findViewById(R.id.down_progress_view);
            holder.downStateView=convertView.findViewById(R.id.down_state_view);
            holder.downTaskName=convertView.findViewById(R.id.down_task_name);
            holder.downStateText=convertView.findViewById(R.id.down_state_text);
            holder.downProgressText=convertView.findViewById(R.id.down_progress_text);
            holder.downProgressBar=convertView.findViewById(R.id.down_progressBar);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.deleteDown.setTag(position);
        holder.deleteDown.setOnClickListener(listener);
        holder.downTaskName.setText(bean.getTaskName());
        holder.downProgressText.setText(bean.getDownSpeed());
        if (bean.isDowning()){
            holder.downProgressView.setVisibility(View.VISIBLE);
            holder.downStateView.setVisibility(View.GONE);

            holder.downProgressBar.setProgress(bean.getProgress());
        }else {
            holder.downProgressView.setVisibility(View.GONE);
            holder.downStateView.setVisibility(View.VISIBLE);
            if (bean.isPause()){
                holder.downStateText.setText("已暂停,点击继续下载");
            }else {
                holder.downStateText.setText("正在准备下载");
            }
        }
        return convertView;
    }
    class ViewHolder{
        View downProgressView,downStateView;
        TextView downTaskName,downStateText,downProgressText;
        ProgressBar downProgressBar;
        ImageView deleteDown;
    }
}
