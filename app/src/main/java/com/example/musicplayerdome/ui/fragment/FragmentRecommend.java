package com.example.musicplayerdome.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.HotListAdapter;
import com.example.musicplayerdome.adapter.NewAlbumListAdapter;
import com.example.musicplayerdome.adapter.RankListAdapter;
import com.example.musicplayerdome.bean.BannerBean;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.MyAlbumBean;
import com.example.musicplayerdome.bean.MyPlayListBean;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.bean.TopListBean;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.activity.HomeActivity;
import com.example.musicplayerdome.ui.activity.HotListActivity;
import com.example.musicplayerdome.ui.activity.NewAlbumActivity;
import com.example.musicplayerdome.ui.activity.PlayOnlineAlbumActivity;
import com.example.musicplayerdome.ui.activity.PlayOnlineListActivity;
import com.example.musicplayerdome.ui.activity.TopListActivity;
import com.example.musicplayerdome.ui.activity.WebActivity;
import com.example.musicplayerdome.ui.activity.WelcomeActivity;
import com.example.musicplayerdome.ui.base.BaseFragment;
import com.example.musicplayerdome.ui.component.MaxRecyclerView;
import com.example.musicplayerdome.utils.GlideUtil;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.ImgHelper;

import java.util.ArrayList;
import java.util.Arrays;

import cn.bingoogolapple.bgabanner.BGABanner;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class FragmentRecommend extends BaseFragment implements View.OnClickListener,HotListAdapter.OnRecyclerViewItemListener,NewAlbumListAdapter.OnRecyclerViewItemListener{
    ArrayList<BannerBean.BannersBean> banners;
    BGABanner mContentBanner;
    SwipeRefreshLayout refreshLayout;
    boolean bannerRefreshed=false,hotListRefreshed=false,newListRefreshed=false,newSongRefreshed=false;
    ArrayList<MyAlbumBean> myAlbumBeans=new ArrayList<>();
    ArrayList<MyPlayListBean> myPlayListBeans=new ArrayList<>();
    ArrayList<TopListBean> topListBeans=new ArrayList<>();
    HotListAdapter hotListAdapter;
    NewAlbumListAdapter newAlbumListAdapter;
    RankListAdapter rankListAdapter;
    MaxRecyclerView hotListRecyclerView,newAlbumRecyclerView;
    Button hotListMore,newAlbumMore,rankListMore;
    ListView rankListView;
    public static final int hotListItemClickTAG=1,newAlbumItemClickTAG=2;
    Handler refreshHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (bannerRefreshed&&hotListRefreshed&&newListRefreshed&&newSongRefreshed){
                if (refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                }
            }
        }
    };
    @Override
    protected int getResId() {
        return R.layout.fragment_recommend;
    }

    @Override
    protected void onConfigView() {
        refreshLayout=getActivity().findViewById(R.id.recommend_refresh);
        mContentBanner=getActivity().findViewById(R.id.recommend_banner);
        hotListRecyclerView=getActivity().findViewById(R.id.hot_song_view);
        newAlbumRecyclerView=getActivity().findViewById(R.id.new_album_view);
        rankListView=getActivity().findViewById(R.id.rank_list_View);
        hotListMore=getActivity().findViewById(R.id.hot_song_btn_more);
        hotListMore.setOnClickListener(this::onClick);
        newAlbumMore=getActivity().findViewById(R.id.new_album_btn_more);
        newAlbumMore.setOnClickListener(this::onClick);
        rankListMore=getActivity().findViewById(R.id.rank_list_btn_more);
        rankListMore.setOnClickListener(this::onClick);
        initListener();
    }

    @Override
    protected void initData() {
        banners=new ArrayList<>();
        StaggeredGridLayoutManager hotGridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        StaggeredGridLayoutManager newGridLayoutManager=new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        hotListRecyclerView.setLayoutManager(hotGridLayoutManager);
        newAlbumRecyclerView.setLayoutManager(newGridLayoutManager);

        hotListAdapter=new HotListAdapter(myPlayListBeans,getContext());
        hotListRecyclerView.setAdapter(hotListAdapter);
        hotListAdapter.setOnRecyclerViewItemListener(FragmentRecommend.this::onItemClickListener);
        hotListRecyclerView.setHasFixedSize(true);

        newAlbumListAdapter=new NewAlbumListAdapter(myAlbumBeans,getContext());
        newAlbumRecyclerView.setAdapter(newAlbumListAdapter);
        newAlbumListAdapter.setOnRecyclerViewItemListener(FragmentRecommend.this::onItemClickListener);
        newAlbumRecyclerView.setHasFixedSize(true);

        getBanners();
        getHotList();
        getNewAlbum();
        getRankList();
    }
    private void initListener(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bannerRefreshed=false;
                hotListRefreshed=false;
                newSongRefreshed=false;
                newListRefreshed=false;
                getBanners();
                getHotList();
                getNewAlbum();
                getRankList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!bannerRefreshed||!hotListRefreshed||!newListRefreshed||!newSongRefreshed){
                            Toast.makeText(getContext(),"网络好像开小差了...",Toast.LENGTH_SHORT).show();
                            if (refreshLayout.isRefreshing()){
                                refreshLayout.setRefreshing(false);
                            }
                            bannerRefreshed=false;
                            hotListRefreshed=false;
                            newSongRefreshed=false;
                            newListRefreshed=false;
                        }
                    }
                }, 10000);
            }
        });
        rankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), PlayOnlineListActivity.class);
                intent.putExtra("playListId",topListBeans.get(position).getId());
                startActivity(intent);
            }
        });
    }
    private void getBanners(){
        banners.clear();
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                banners.addAll(HttpRequest.getBanners());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (banners.size()>0){
                    String[][] bannerStrings=new String[2][banners.size()];
                    for (int i=0;i<banners.size();i++) {
                        BannerBean.BannersBean banner=banners.get(i);
                        bannerStrings[0][i]=banner.getPic();
                        if (banner.getTargetType()==1){
                            bannerStrings[1][i]="单曲";
                        }else if (banner.getTargetType()==10){
                            bannerStrings[1][i]="专辑";
                        }else if (banner.getTargetType()==1000){
                            bannerStrings[1][i]="歌单";
                        }else if (banner.getTargetType()==1004){
                            bannerStrings[1][i]="MV";
                        }
                    }
                    mContentBanner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
                        @Override
                        public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                            Glide.with(getActivity())
                                    .load(model)
                                    .placeholder(R.drawable.ic_menu_gallery)
                                    .error(R.drawable.ic_menu_gallery)
                                    .centerCrop()
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .sizeMultiplier(0.8f)
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(
                                                @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            Bitmap srcBitmap = ImgHelper.drawableToBitmap(resource);
                                            if (itemView.getWidth()>0&&itemView.getHeight()>0)
                                                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, itemView.getWidth(), itemView.getHeight());
                                            srcBitmap=getRoundedCornerBitmap(srcBitmap,30f);
                                            itemView.setImageBitmap(srcBitmap);
                                        }
                                    });
                        }
                    });
                    mContentBanner.setDelegate(new BGABanner.Delegate<ImageView, String>() {
                        @Override
                        public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                            String idString=banners.get(position).getUrl();
                            int id=banners.get(position).getTargetId();
                            if (banners.get(position).getTargetType()==1){
                                getMusicDetail(id);
                            }else if (banners.get(position).getTargetType()>1000&&idString!=null){
                                Intent intent= new Intent(getActivity(), WebActivity.class);
                                intent.putExtra("URLString",idString);
                                startActivity(intent);
                            }else if (banners.get(position).getTargetType()==1000){
                                Intent intent=new Intent(getActivity(), PlayOnlineListActivity.class);
                                intent.putExtra("playListId",(long)id);
                                startActivity(intent);
                            }else if (banners.get(position).getTargetType()==10){
                                Intent intent=new Intent(getActivity(), PlayOnlineAlbumActivity.class);
                                intent.putExtra("albumId",(long)id);
                                startActivity(intent);
                            }
                        }
                    });
                    mContentBanner.setData(
                            Arrays.asList(bannerStrings[0]),
                            Arrays.asList(bannerStrings[1]));
                    bannerRefreshed=true;
                }else {
                    bannerRefreshed=false;
                }
                refreshHandler.sendEmptyMessage(0);
            }
        }.execute();
    }
    Music music=null;
    private void getMusicDetail(int id){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                music=HttpRequest.getMusicDetail(id);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (music!=null){
                    if (MusicService.listMusic!=null&&MusicService.listMusic.size()>0){
                        int nextPlayPosition=MusicService.playList.indexOf(MusicService.position+1);//获取下一首在播放顺序的位置
                        boolean isHad=false;
                        for (int i=0;i<MusicService.listMusic.size();i++){//播放列表有此歌曲
                            if (music.getMId().equals(MusicService.listMusic.get(i).getMId())){
                                MusicService.initPlayMsg(i,MusicService.listMusic);
                                if (MusicService.position==i||!MusicService.mlastPlayer.isPlaying()){
                                    MusicService.position=i;    //交换摆放位置
                                    HomeActivity.musicControl.playPosition(i);
                                }
                                isHad=true;
                                break;
                            }
                        }
                        if (!isHad){//播放列表没有此歌曲
                            int index= MusicService.listMusic.size();
                            if (nextPlayPosition!=-1)
                                MusicService.playList.add(nextPlayPosition,index);
                            else
                                MusicService.playList.add(MusicService.playList.indexOf(MusicService.position)+1,index);
                            MusicService.listMusic.add(index,music);
                            MusicDaoImpl.addPlayMusic(new PlayMusicBean(music.getId(),music.getSId(),music.getMId(),music.getTitle(),music.getSinger(),music.getAlbum(),music.getUrl(),
                                    music.getSize(),music.getTime(),music.getName(),music.getAlbumImg(),music.getProgress()));
                            HomeActivity.musicControl.next(1);
                        }
                    }else {
                        ArrayList<Music> musics=new ArrayList<>();
                        musics.add(music);
                        MusicService.initPlayMsg(0,musics);
                        HomeActivity.musicControl.play();
                    }
                }
            }
        }.execute();
    }
    private void getHotList(){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<MyPlayListBean> tempList=HttpRequest.getRecommendList(6,0);
                if (tempList!=null){
                    myPlayListBeans.clear();
                    myPlayListBeans.addAll(tempList);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (myPlayListBeans.size()>0){
                    hotListRefreshed=true;
                    hotListAdapter.notifyDataSetChanged();
                }else {
                    hotListRefreshed=false;
                }
                refreshHandler.sendEmptyMessage(0);
            }
        }.execute();
    }
    private void getNewAlbum(){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<MyAlbumBean> tempList=HttpRequest.getNewAlbumList(6,0);
                if (tempList!=null){
                    myAlbumBeans.clear();
                    myAlbumBeans.addAll(tempList);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (myAlbumBeans.size()>0){
                    newListRefreshed=true;
                    newAlbumListAdapter.notifyDataSetChanged();
                }else {
                    newListRefreshed=false;
                }
                refreshHandler.sendEmptyMessage(0);
            }
        }.execute();
    }
    private void getRankList(){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<TopListBean> tempList=HttpRequest.getTopList();
                topListBeans.clear();
                if (tempList!=null&&tempList.size()>4)
                    for (int i=0;i<4;i++){
                        topListBeans.add(tempList.get(i));
                    }
                else if (tempList!=null&&tempList.size()<4)
                    topListBeans.addAll(tempList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (topListBeans.size()>0){
                    rankListAdapter=new RankListAdapter(topListBeans,getContext());
                    rankListView.setAdapter(rankListAdapter);
                    FragmentMine.setListViewHeightBasedOnChildren(rankListView);
                    newSongRefreshed=true;
                }else {
                    newSongRefreshed=false;
                }
                refreshHandler.sendEmptyMessage(0);
            }
        }.execute();
    }

    @Override
    public void onItemClickListener(View view, int position) {
        if(((int)view.getTag())==hotListItemClickTAG){
            Intent intent=new Intent(getActivity(), PlayOnlineListActivity.class);
            intent.putExtra("playListId",myPlayListBeans.get(position).getId());
            startActivity(intent);
//            Toast.makeText(getContext(),"推荐点击"+(position+1),Toast.LENGTH_SHORT).show();
        }else {
            Intent intent=new Intent(getActivity(), PlayOnlineAlbumActivity.class);
            intent.putExtra("albumId",myAlbumBeans.get(position).getId());
            startActivity(intent);
//            Toast.makeText(getContext(),"新碟点击"+(position+1),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rank_list_btn_more:
                startActivity(new Intent(getContext(), TopListActivity.class));
                break;
            case R.id.hot_song_btn_more:
                startActivity(new Intent(getContext(), HotListActivity.class));
                break;
            case R.id.new_album_btn_more:
                startActivity(new Intent(getContext(), NewAlbumActivity.class));
                break;
        }
    }
}
