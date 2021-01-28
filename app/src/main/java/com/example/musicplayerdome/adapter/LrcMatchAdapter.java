package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.ui.activity.SearchLrcActivity;
import com.example.musicplayerdome.utils.Strings;

import java.util.ArrayList;

public class LrcMatchAdapter extends RecyclerView.Adapter<LrcMatchAdapter.LrcMatchHolder> {
    public static ArrayList<String[]> lrcs=new ArrayList<>();
    View.OnClickListener listener;
    Context context;

    public LrcMatchAdapter(ArrayList<String[]> lrcs, View.OnClickListener listener, Context context) {
        LrcMatchAdapter.lrcs.clear();
        this.lrcs .addAll(lrcs) ;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public LrcMatchAdapter.LrcMatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.lrc_item, parent, false);
        return new LrcMatchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LrcMatchAdapter.LrcMatchHolder holder, int position) {
        if (position<SearchLrcActivity.choice.length&&SearchLrcActivity.choice[position]){
            holder.radioChoice.setChecked(true);
        }else {
            holder.radioChoice.setChecked(false);
        }
        holder.radioChoice.setTag(position);
        holder.radioChoice.setOnClickListener(listener);
        String lrc=lrcs.get(position)[0];
        if (lrc==null){
            return;
        }
        String tempLrc=new String(lrc.getBytes());
        holder.lrcView.setText(Strings.ClearBracket(tempLrc));
        if (mOnRecyclerViewItemListener != null){
            itemOnClick(holder);
        }
    }
    public class LrcMatchHolder extends RecyclerView.ViewHolder {
        RadioButton radioChoice;
        EditText lrcView;
        public LrcMatchHolder(@NonNull View itemView) {
            super(itemView);
            radioChoice=itemView.findViewById(R.id.radio_choice);
            lrcView=itemView.findViewById(R.id.lrc_view);
        }
    }
    @Override
    public int getItemCount() {
        return lrcs.size();
    }
    public interface OnRecyclerViewItemListener {
        public void onItemClickListener(View view,int position);
    }

    private OnRecyclerViewItemListener mOnRecyclerViewItemListener;

    public void setOnRecyclerViewItemListener(OnRecyclerViewItemListener listener){
        mOnRecyclerViewItemListener = listener;
    }

    private void itemOnClick(final RecyclerView.ViewHolder holder){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                mOnRecyclerViewItemListener.onItemClickListener(holder.itemView, pos);
            }
        });
    }
}
