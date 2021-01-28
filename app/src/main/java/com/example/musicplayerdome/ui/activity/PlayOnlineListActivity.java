package com.example.musicplayerdome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.MusicListAdapter;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.MyPlayListBean;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.fragment.FragmentMine;
import com.example.musicplayerdome.ui.fragment.FramentMusicbar;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.ImgHelper;

import java.util.ArrayList;
import java.util.Date;

import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class PlayOnlineListActivity extends BaseActivity implements View.OnClickListener{
    private ListView listView;
    TextView nameTv;
    TextView numTv;
    MusicListAdapter adapter;
    SearchView searchView;
    ImageView coverImg;
    TextView introduceTv;
    View moreView;
    ScrollView mainScrollView;
    FramentMusicbar musicBar;
    private FragmentManager manager;
    MyPlayListBean playList;
    MyReceiver myReceiver;
    long listId;
    Context context;
    int musicCount=0;
    TextView creatorTv;
    TextView playCount;
    @Override
    protected int getResId() {
        return R.layout.activity_play_online_list;
    }

    @Override
    protected void onConfigView() {
        listView=findViewById(R.id.song_list);
        searchView=findViewById(R.id.list_search_view);
        nameTv=findViewById(R.id.listName);
        moreView=findViewById(R.id.more);
        numTv=findViewById(R.id.list_num);
        coverImg=findViewById(R.id.cover_img);
        introduceTv=findViewById(R.id.introduce_text);
        mainScrollView=findViewById(R.id.main_scroll);
        creatorTv=findViewById(R.id.creator_text);
        playCount=findViewById(R.id.play_count);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        context=this;
        listId=getIntent().getLongExtra("playListId",(long)-1);
        if (listId==-1){
            Toast.makeText(context,"无效的音乐列表id",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        musicBar=new FramentMusicbar();
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.music_bar,musicBar);
        transaction.show(musicBar).commitAllowingStateLoss();
        getPlayList();
        mainScrollView.post(new Runnable() {
            @Override
            public void run() {
                mainScrollView.fullScroll(View.FOCUS_UP);
            }
        });
        if (myReceiver==null){
            myReceiver = new MyReceiver(new Handler());
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(MusicService.MAIN_UPDATE_UI);
            registerReceiver(myReceiver, itFilter);
        }
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                nameTv.setVisibility(View.VISIBLE);
                searchView.setQuery("", false);
                searchView.clearFocus();  //可以收起键盘
                searchView.onActionViewCollapsed();    //可以收起SearchView视图
                adapter=new MusicListAdapter(playList.getMusics(),context,PlayOnlineListActivity.this::onClick);
                listView.setAdapter(adapter);
                musicCount=playList.getMusics().size();
                numTv.setText("("+musicCount+")");
                return false;
            }
        });
        //搜索图标按钮(打开搜索框的按钮)的点击事件
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTv.setVisibility(View.GONE);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println("搜索:"+searchView.getQuery());
                searchView.clearFocus();  //可以收起键盘
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                System.out.println("输入:"+searchView.getQuery());
                String queryString=searchView.getQuery().toString();
                ArrayList<Music> tempMusics=new ArrayList<>();
                for (Music m:
                        playList.getMusics()) {
                    if (m.getName().contains(queryString)||m.getAlbum().contains(queryString)||m.getSinger().contains(queryString)){
                        tempMusics.add(m);
                    }
                }
                adapter=new MusicListAdapter(tempMusics,context,PlayOnlineListActivity.this::onClick);
                listView.setAdapter(adapter);
                musicCount=tempMusics.size();
                numTv.setText("("+musicCount+")");
                return false;
            }
        });
    }
    private class MyReceiver extends BroadcastReceiver {
        private final Handler handler;
        // Handler used to execute code on the UI thread
        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            handler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    numTv.setText("("+musicCount+")");
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver!=null)
            unregisterReceiver(myReceiver);
    }

    private void getPlayList(){
        showLoadingDialog();
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                playList=HttpRequest.getPlayListDetails(listId);
                if (playList!=null)
                    musicCount=playList.getMusics().size();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismissLoadingDialog();
                if (playList!=null){
                    adapter=new MusicListAdapter(playList.getMusics(),context,PlayOnlineListActivity.this::onClick);
                    listView.setAdapter(adapter);
                    if (playList.getCover().contains("http")){
                        playList.setCover(getProxy().getProxyUrl(playList.getCover()));
                    }
                    Glide.with(context)
                            .load(playList.getCover())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(300,300)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(
                                        @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    Bitmap srcBitmap = ImgHelper.drawableToBitmap(resource);
                                    srcBitmap=getRoundedCornerBitmap(srcBitmap,20f);
                                    coverImg.setImageBitmap(srcBitmap);
                                }
                            });
                    introduceTv.setText(playList.getIntroduce());
                    nameTv.setText(playList.getName());
                    numTv.setText("("+musicCount+")");
                    FragmentMine.setListViewHeightBasedOnChildren(listView);
                    creatorTv.setText("创作者:"+playList.getCreater());
                    playCount.setText(formatCount(playList.getListenCount()));
                }else {
                    Toast.makeText(context,"未找到歌单信息",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }.execute();
    }
    public void doClick(View view) {
        switch (view.getId()){
            case R.id.btn_return:
                finish();
                break;
            case R.id.play_all_view:
                Intent intent1=new Intent(context,DetailsActivity.class);
                MusicService.initPlayMsg(0, adapter.getMusics());
                adapter.notifyDataSetChanged();
                startActivity(intent1);
                break;
            case R.id.list_choice_view:
                Intent intent2=new Intent(context,MultipleMusicActivity.class);
                Bundle bundle=new Bundle();
                intent2.putExtra("musicModel",MultipleMusicActivity.ONLINE_MUSIC_MODEL);
                bundle.putParcelableArrayList("choiceMusics",adapter.getMusics());
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            case R.id.more:
                showPopup(view);
                break;
            case R.id.cover_img:
            case R.id.introduce_text:
            case R.id.creator_text:
            case R.id.show_detail_view:
                Intent intent3=new Intent(context,ShowSongListActivity.class);
                intent3.putExtra("isLocalList",false);
                intent3.putExtra("introduce",playList.getIntroduce());
                intent3.putExtra("name",playList.getName());
                intent3.putExtra("url",playList.getCover());
                startActivity(intent3);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int position=(int)v.getTag();
        switch (v.getId()){
            case R.id.music_play_view:
                Intent intent=new Intent(context,DetailsActivity.class);
                if (MusicService.mlastPlayer!=null
                        &&HomeActivity.musicControl.getMusic().getSId()==-1
                        &&HomeActivity.musicControl.getMusic().getId()==adapter.getMusics().get(position).getId())
                    intent.putExtra("continuePlay",true);
                else
                    MusicService.initPlayMsg(position, (ArrayList<Music>) adapter.getMusics());
                adapter.notifyDataSetChanged();
                startActivity(intent);
                break;
            case R.id.music_more_view:
//                showMusicMorePopup(context,songList,position);
                break;
        }
    }
    private void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.online_list_menu, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_local_list:
                        addLocal();
                        break;
                    case R.id.down_all:
                        downMusics();
                        break;
                }
                return false;
            }
        });
        popup.show();
    }
    private void addLocal(){
        SongList songList=new SongList();
        songList.setIntroduce(playList.getIntroduce());
        songList.setName(playList.getName());
        songList.setTime(new Date());
        songList.setCoverImg(playList.getCover());
        MusicDaoImpl.createMusicsSongList(songList,playList.getMusics());
        HomeActivity.songLists=MusicDaoImpl.getSongLists();
        for (int i=0;i<4;i++){
            HomeActivity.songLists.remove(0);
        }
        Toast.makeText(context,"加入收藏成功!",Toast.LENGTH_SHORT).show();
    }
    private void downMusics(){
        ArrayList<Music> tempMusics=new ArrayList<>();
        tempMusics.addAll(playList.getMusics());
        final AlertDialog show;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        View view=View.inflate(context, R.layout.sure_del_list, null);
        View sure,cancel;
        TextView issueText=view.findViewById(R.id.issue_text);
        issueText.setText("确认下载所有歌曲?");
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);
        alertDialog.setView(view);
        show=alertDialog.create();
        show.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int downCount=0;
                for (int i=0;i<tempMusics.size();i++){
                    Music m=tempMusics.get(i);
                    if (m.getUrl().contains("http")){
                        boolean hadDown=false;
                        for (Music music:
                                HomeActivity.downSongList.getMusics()) {
                            if (m.getName().equals(music.getName())&&m.getSinger().equals(music.getSinger())&&m.getAlbum().equals(music.getAlbum())){
                                hadDown=true;
                                break;
                            }
                        }
                        if (!hadDown)
                            hadDown=MusicDaoImpl.checkLocalMusic(m);
                        if (!hadDown){
                            MusicDaoImpl.addMusicToSongList(HomeActivity.downSongList,m);
                            downCount++;
                        }
                    }
                }
                Toast.makeText(context,downCount+"首歌加入下载列表",Toast.LENGTH_SHORT).show();
                show.dismiss();
                if (downCount>0){
                    Intent intent=new Intent(context, DownloadActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }
    public static String formatCount(int count){
        if (count<0){
            count=0-count;
        }
        String countString=String.valueOf(count);
        byte[] countBytes=countString.getBytes();
        if (countBytes.length<=4){//1万以下直接显示
        }else if (countBytes.length<=8){ //1亿以下显示万
            int lastIndex=countBytes.length-5;
            byte[] tempBytes=new byte[lastIndex+1];
            for (int i=0;i<lastIndex+1;i++){
                tempBytes[i]=countBytes[i];
            }
            countString=new String(tempBytes)+"万";
        }else {    //1亿以上显示亿
            int lastIndex=countBytes.length-9;
            byte[] tempBytes=new byte[lastIndex+1];
            for (int i=0;i<lastIndex+1;i++){
                tempBytes[i]=countBytes[i];
            }
            countString=new String(tempBytes)+"亿";
        }
        return countString;
    }
    private HttpProxyCacheServer getProxy() {
        long size= App.getShared().getLong("cacheCapacity",0b10000000000000000000000000000000l);
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(size)       // 2 Gb for cache
                .build();
    }
}
