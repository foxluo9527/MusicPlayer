package com.example.musicplayerdome.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.MyPlayListBean;
import com.example.musicplayerdome.ui.activity.PlayOnlineListActivity;
import com.example.musicplayerdome.ui.fragment.FragmentRecommend;
import com.example.musicplayerdome.utils.ImgHelper;

import java.util.ArrayList;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class HotListAdapter extends RecyclerView.Adapter<HotListAdapter.HotListHolder> {
    ArrayList<MyPlayListBean> hotLists;
    Context context;

    public HotListAdapter(ArrayList<MyPlayListBean> hotLists, Context context) {
        this.hotLists = hotLists;
        this.context = context;
    }

    @NonNull
    @Override
    public HotListAdapter.HotListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_list_item_view, parent, false);
        return new HotListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotListAdapter.HotListHolder holder, int position) {
        MyPlayListBean hotListBean=hotLists.get(position);
        if (hotListBean.getCover().contains("http")&&!getProxy().isCached(hotListBean.getCover())) {
            String url= getProxy().getProxyUrl(hotListBean.getCover());
            hotListBean.setCover(url);
        }
        Glide.with(context)
                .load(hotListBean.getCover())
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .override(300,300)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap srcBitmap = ImgHelper.drawableToBitmap(resource);
                        srcBitmap=getRoundedCornerBitmap(srcBitmap,12f);
                        holder.coverImg.setImageBitmap(srcBitmap);
                    }
                });
        holder.playCountTv.setText(PlayOnlineListActivity.formatCount(hotListBean.getListenCount()));
        if (hotListBean.getName()!=null)
            holder.nameTv.setText(hotListBean.getName()+"");
        if (mOnRecyclerViewItemListener != null){
            itemOnClick(holder);
        }
    }
    public class HotListHolder extends RecyclerView.ViewHolder {
        ImageView coverImg;
        TextView playCountTv,nameTv;
        public HotListHolder(@NonNull View itemView) {
            super(itemView);
            coverImg=itemView.findViewById(R.id.cover_img);
            playCountTv=itemView.findViewById(R.id.play_count);
            nameTv=itemView.findViewById(R.id.list_name);
        }
    }
    @Override
    public int getItemCount() {
        return hotLists.size();
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
                holder.itemView.setTag(FragmentRecommend.hotListItemClickTAG);
                int pos = holder.getLayoutPosition();
                mOnRecyclerViewItemListener.onItemClickListener(holder.itemView, pos);
            }
        });
    }
    private HttpProxyCacheServer getProxy() {
        long size= App.getShared().getLong("cacheCapacity",0b10000000000000000000000000000000l);
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(size)       // 2 Gb for cache
                .build();
    }
}
