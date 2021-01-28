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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    ArrayList<MusicCommentsResultBean.CommentsBean> commentsBeans;
    Context context;

    public CommentAdapter(ArrayList<MusicCommentsResultBean.CommentsBean> commentsBeans, Context context) {
        this.commentsBeans = commentsBeans;
        this.context = context;
    }
    @NonNull
    @Override
    public CommentAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view_item, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentHolder holder, int position) {
        MusicCommentsResultBean.CommentsBean commentsBean=commentsBeans.get(position);
        holder.zanTv.setText(commentsBean.getLikedCount()+"");
        holder.timeTv.setText(HttpRequest.toDate(commentsBean.getTime()));
        holder.nameTv.setText(commentsBean.getUser().getNickname());
        holder.contentTv.setText(commentsBean.getContent());
        Glide.with(context)
                .load(commentsBean.getUser().getAvatarUrl())
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .into(holder.headImg);
        if (commentsBean.getBeReplied()!=null&&commentsBean.getBeReplied().size()>0){
            holder.repliedTv.setVisibility(View.VISIBLE);
            holder.repliedTv.setText("回复(@"+commentsBean.getBeReplied().get(0).getUser().getNickname()+"):"+commentsBean.getBeReplied().get(0).getContent());
        }
        holder.writerFlag.setVisibility(View.GONE);
    }
    public class CommentHolder extends RecyclerView.ViewHolder {
        CircleImageView headImg;
        ImageView writerFlag;
        TextView nameTv,timeTv,zanTv;
        TextView contentTv,repliedTv;
        public CommentHolder(@NonNull View itemView) {
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
        return commentsBeans.size();
    }
}
