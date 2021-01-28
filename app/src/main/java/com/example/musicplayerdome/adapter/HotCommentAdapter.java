package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.MusicCommentsResultBean;
import com.example.musicplayerdome.ui.component.CircleImageView;
import com.example.musicplayerdome.utils.HttpRequest;

import java.util.ArrayList;

public class HotCommentAdapter extends RecyclerView.Adapter<HotCommentAdapter.HotCommentHolder> {
    ArrayList<MusicCommentsResultBean.HotCommentsBean> hotCommentsBeans;
    Context context;

    public HotCommentAdapter(ArrayList<MusicCommentsResultBean.HotCommentsBean> hotCommentsBeans, Context context) {
        this.hotCommentsBeans = hotCommentsBeans;
        this.context = context;
    }
    @NonNull
    @Override
    public HotCommentAdapter.HotCommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view_item, parent, false);
        return new HotCommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotCommentAdapter.HotCommentHolder holder, int position) {
        MusicCommentsResultBean.HotCommentsBean hotCommentsBean=hotCommentsBeans.get(position);
        holder.zanTv.setText(hotCommentsBean.getLikedCount()+"");
        holder.timeTv.setText(HttpRequest.toDate(hotCommentsBean.getTime()));
        holder.nameTv.setText(hotCommentsBean.getUser().getNickname());
        holder.contentTv.setText(hotCommentsBean.getContent());
        Glide.with(context)
                .load(hotCommentsBean.getUser().getAvatarUrl())
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .into(holder.headImg);
        if (hotCommentsBean.getBeReplied()!=null&&hotCommentsBean.getBeReplied().size()>0){
            holder.repliedTv.setVisibility(View.VISIBLE);
            holder.repliedTv.setText("回复(@"+hotCommentsBean.getBeReplied().get(0).getUser().getNickname()+"):"+hotCommentsBean.getBeReplied().get(0).getContent());
        }
        holder.writerFlag.setVisibility(View.GONE);
    }
    public class HotCommentHolder extends RecyclerView.ViewHolder {
        CircleImageView headImg;
        ImageView writerFlag;
        TextView nameTv,timeTv,zanTv;
        TextView contentTv,repliedTv;
        public HotCommentHolder(@NonNull View itemView) {
            super(itemView);
            writerFlag=itemView.findViewById(R.id.writer_flag);
            headImg=itemView.findViewById(R.id.head_img);
            timeTv=itemView.findViewById(R.id.time_tv);
            nameTv=itemView.findViewById(R.id.name_tv);
            zanTv=itemView.findViewById(R.id.zan_num);
            contentTv=itemView.findViewById(R.id.content_tv);
            repliedTv=itemView.findViewById(R.id.replied_tv);
        }
    }
    @Override
    public int getItemCount() {
        return hotCommentsBeans.size();
    }
}
