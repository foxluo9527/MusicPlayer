package com.example.musicplayerdome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.MusicListAdapter;
import com.example.musicplayerdome.adapter.PlayListAdapter;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.component.CircleImageView;
import com.example.musicplayerdome.ui.fragment.FragmentMine;
import com.example.musicplayerdome.utils.FileUtils;
import com.example.musicplayerdome.utils.ImgHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.musicplayerdome.service.MusicService.VAL_UPDATE_UI_PLAY;
import static com.example.musicplayerdome.service.MusicService.position;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.changeRuleImg;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.setRuleText;
import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class SongListActivity extends BaseActivity implements View.OnClickListener{
    SongList songList;
    int listPosition;
    private MyReceiver myReceiver;
    private ListView listView;
    private View mainView;
    TextView nameTv;
    TextView numTv;
    MusicListAdapter adapter;
    ImageView playImg;
    Animation animation;
    CircleImageView songImg;
    TextView songNameTv,singerTv;
    TextView backPlay;
    private PlayListAdapter playAdapter;
    SearchView searchView;
    View moreView;
    ImageView coverImg;
    TextView introduceTv;
    ScrollView mainScrollView;
    static int nowPosition=-1;
    @Override
    protected int getResId() {
        return R.layout.activity_song_list;
    }

    @Override
    protected void onConfigView() {
        listView=findViewById(R.id.song_list);
        numTv=findViewById(R.id.list_num);
        mainView=findViewById(R.id.show_music_view);
        playImg=findViewById(R.id.home_song_play);
        songImg=findViewById(R.id.home_song_img);
        songNameTv=findViewById(R.id.home_song_name);
        singerTv=findViewById(R.id.home_song_lrc);
        backPlay=findViewById(R.id.back_play);
        searchView=findViewById(R.id.list_search_view);
        nameTv=findViewById(R.id.listName);
        moreView=findViewById(R.id.more);
        coverImg=findViewById(R.id.cover_img);
        introduceTv=findViewById(R.id.introduce_text);
        mainScrollView=findViewById(R.id.main_scroll);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        long sId=getIntent().getLongExtra("songListId",0);
        listPosition=getIntent().getIntExtra("listPosition",0);
        songList= MusicDaoImpl.getSongList(sId);
        if (songList==null){
            return;
        }
        Glide.with(this)
                .load(songList.getCoverImg())
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap srcBitmap = ImgHelper.drawableToBitmap(resource);
                        srcBitmap=getRoundedCornerBitmap(srcBitmap,20f);
                        coverImg.setImageBitmap(srcBitmap);
                    }
                });
        introduceTv.setText(songList.getIntroduce());
        nameTv.setText(songList.getName());
        animation= AnimationUtils.loadAnimation(this,R.anim.rotate);
        songImg.setAnimation(animation);
        numTv.setText("("+songList.getMusics().size()+")");
        adapter=new MusicListAdapter(songList.getMusics(),this,this::onClick);
        listView.setAdapter(adapter);
        updateMusicView();
        if (myReceiver==null){
            myReceiver = new MyReceiver(new Handler());
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(MusicService.MAIN_UPDATE_UI);
            registerReceiver(myReceiver, itFilter);
        }
        mainScrollView.post(new Runnable() {
            @Override
            public void run() {
                mainScrollView.fullScroll(View.FOCUS_UP);
            }
        });
        initListener();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                finish();
                break;
            case R.id.play_view:
                HomeActivity.musicControl.play();
                updateMusicView();
                break;
            case R.id.list_view:
                showListPopupMenu(SongListActivity.this);
                break;
            case R.id.show_music_view:
                Intent intent=new Intent(SongListActivity.this,DetailsActivity.class);
                intent.putExtra("continuePlay",true);
                startActivity(intent);
                break;
            case R.id.back_play:
                long playId=HomeActivity.musicControl.getMusic().getId();
                for (int i=0;i<songList.getMusics().size();i++){
                    Music music=songList.getMusics().get(i);
                    if (music.getId().equals(playId)){
                        listView.setSelection(i);
                    }
                }
                break;
            case R.id.more:
                showPopup(v);
                break;
            case R.id.play_all_view:
                Intent intent1=new Intent(SongListActivity.this,DetailsActivity.class);
                MusicService.initPlayMsg(0, (ArrayList<Music>) songList.getMusics());
                adapter.notifyDataSetChanged();
                startActivity(intent1);
                break;
            case R.id.list_choice_view:
                Intent intent2=new Intent(SongListActivity.this,MultipleMusicActivity.class);
                intent2.putExtra("listPosition",listPosition);
                startActivity(intent2);
                break;
            case R.id.cover_img:
            case R.id.introduce_text:
                Intent intent3=new Intent(SongListActivity.this,ShowSongListActivity.class);
                intent3.putExtra("isLocalList",true);
                intent3.putExtra("position",listPosition-4);
                intent3.putExtra("introduce",songList.getIntroduce());
                intent3.putExtra("name",songList.getName());
                intent3.putExtra("url",songList.getCoverImg());
                startActivity(intent3);
                break;
        }
    }
    @Override
    public void onClick(View v) {
        int position=(int)v.getTag();
        switch (v.getId()){
            case R.id.music_play_view:
                Intent intent=new Intent(SongListActivity.this,DetailsActivity.class);
                if (MusicService.mlastPlayer!=null
                        &&HomeActivity.musicControl.getMusic().getSId()==songList.getId()
                        &&HomeActivity.musicControl.getMusic().getId()==songList.getMusics().get(position).getId())
                    intent.putExtra("continuePlay",true);
                else
                    MusicService.initPlayMsg(position, (ArrayList<Music>) songList.getMusics());
                adapter.notifyDataSetChanged();
                startActivity(intent);
                break;
            case R.id.music_more_view:
                showMusicMorePopup(SongListActivity.this,songList,position);
                break;
        }
    }
    private void initListener(){
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                nameTv.setVisibility(View.VISIBLE);
                searchView.setQuery("", false);
                searchView.clearFocus();  //可以收起键盘
                searchView.onActionViewCollapsed();    //可以收起SearchView视图
                MusicDaoImpl.orderListMusicByDefault(songList);
                initOrderList();
                adapter=new MusicListAdapter(songList.getMusics(),SongListActivity.this,SongListActivity.this::onClick);
                listView.setAdapter(adapter);
                numTv.setText("("+songList.getMusics().size()+")");
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
                List<Music> musics=MusicDaoImpl.queryListMusic(songList,searchView.getQuery().toString());
                songList.getMusics().clear();
                songList.getMusics().addAll(musics);
                adapter=new MusicListAdapter(songList.getMusics(),SongListActivity.this,SongListActivity.this::onClick);
                listView.setAdapter(adapter);
                numTv.setText("("+songList.getMusics().size()+")");
                return false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (MusicService.mlastPlayer!=null
                        &&HomeActivity.musicControl.getMusic().getSId()==songList.getId()){
                    if (scrollState == SCROLL_STATE_IDLE) {//停止滚动
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                //execute the task
                                backPlay.setVisibility(View.GONE);
                            }
                        }, 2000);
                    } else {
                        backPlay.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
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
                    updateMusicView();
                    adapter=new MusicListAdapter(songList.getMusics(),SongListActivity.this,SongListActivity.this::onClick);
                    listView.setAdapter(adapter);
                    numTv.setText("("+songList.getMusics().size()+")");
                }
            });
        }
    }
    private static final int PICTURE = 10086; //requestcode
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            switch (requestCode) {
                case PICTURE:
                    Uri uri = data.getData();
                    String image = FileUtils.getUriPath(this, uri); //（因为4.4以后图片uri发生了变化）通过文件工具类 对uri进行解析得到图片路径
                    File file = new File(Environment.getExternalStorageDirectory() + "/MusicPlayer/cover/");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    file = new File(Environment.getExternalStorageDirectory() + "/MusicPlayer/cover/" + UUID.randomUUID() + ".jpg");
                    if (file.exists()) {    //如果目标文件已经存在
                        file.delete();    //则删除旧文件
                    } else {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    UCrop.of(uri, Uri.fromFile(file))
                            .withAspectRatio(1, 1)
                            .start(this);
                    break;
                case UCrop.REQUEST_CROP:
                    if (resultCode == RESULT_OK) {
                        final Uri resultUri = UCrop.getOutput(data);
                        String coverString = resultUri.toString();
                        if (nowPosition!=-1){
                            Music music=songList.getMusics().get(nowPosition);
                            int playIndex=MusicService.listMusic.indexOf(music);
                            music.setAlbumImg(coverString);
                            MusicDaoImpl.updateSongListMsg(songList);
                            if (playIndex!=-1){ //音乐列表中有此歌曲信息
                                MusicService.listMusic.get(playIndex).setAlbumImg(coverString);
                                PlayMusicBean playMusicBean=MusicDaoImpl.getPlayListDao().load(MusicService.listMusic.get(playIndex).getId());
                                playMusicBean.setAlbumImg(coverString);
                                MusicDaoImpl.getPlayListDao().update(playMusicBean);
                            }
                        }
                        LocalSongActivity.refreshView(SongListActivity.this,VAL_UPDATE_UI_PLAY);
                        nowPosition=-1;
                    } else if (resultCode == UCrop.RESULT_ERROR) {
                        final Throwable cropError = UCrop.getError(data);
                        cropError.printStackTrace();
                    }
                    break;
            }
        }
    }
    private void initOrderList(){
        int order=HomeActivity.shared.getInt("orderBy",0);
        switch (order){
            case 0:
                MusicDaoImpl.orderListMusicByDefault(songList);
                break;
            case 1:
                MusicDaoImpl.orderListMusicByName(songList);
                break;
            case 2:
                MusicDaoImpl.orderListMusicBySinger(songList);
                break;
            case 3:
                MusicDaoImpl.orderListMusicByAlbum(songList);
                break;
        }
    }
    private void updateMusicView(){
        if (MusicService.listMusic==null||MusicService.listMusic.size()==0){
            mainView.setVisibility(View.GONE);
        }else {
            if (MusicService.mPosition==-1|| position==-1){
                return;
            }
            mainView.setVisibility(View.VISIBLE);
            songNameTv.setText(MusicService.listMusic.get(MusicService.position).getName());
            singerTv.setText(MusicService.listMusic.get(MusicService.position).getSinger());
            Glide.with(this)
                    .load(MusicService.listMusic.get(MusicService.position).getAlbumImg())
                    .placeholder(R.drawable.music)
                    .error(R.drawable.music)
                    .into(songImg);
            new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void run() {
                    //execute the task
                    if (!HomeActivity.musicControl.isPlaying()) {
                        playImg.setImageDrawable(SongListActivity.this.getDrawable(R.drawable.play));
                        songImg.clearAnimation();
                    } else {
                        playImg.setImageDrawable(SongListActivity.this.getDrawable(R.drawable.pause));
                        songImg.startAnimation(animation);
                    }
                }
            }, 600);
        }
        if (playAdapter!=null) playAdapter.notifyDataSetChanged();
        FragmentMine.setListViewHeightBasedOnChildren(listView);
    }
    private void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.local_list_menu, popup.getMenu());
        popup.getMenu().removeItem(R.id.add_local_music);
        popup.getMenu().removeItem(R.id.match_lrc);
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.select_order:
                        showOrderPopup();
                        break;
                    case R.id.edit_song_list:
                        Intent intent=new Intent(SongListActivity.this, EditSongListActivity.class);
                        intent.putExtra("position",listPosition-4);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });

        popup.show();
    }
    private void showOrderPopup(){
        int order=HomeActivity.shared.getInt("orderBy",0);
        final View popView = View.inflate(SongListActivity.this,R.layout.select_order_menu,null);
        View defaultOrderView=popView.findViewById(R.id.default_order_view);
        View nameOrderView=popView.findViewById(R.id.name_order_view);
        View singerOrderView=popView.findViewById(R.id.singer_order_view);
        View albumOrderView=popView.findViewById(R.id.album_order_view);
        ImageView defaultOrderImg=popView.findViewById(R.id.order_default_c);
        ImageView nameOrderImg=popView.findViewById(R.id.order_name_c);
        ImageView singerOrderImg=popView.findViewById(R.id.order_singer_c);
        ImageView albumOrderImg=popView.findViewById(R.id.order_zhuanji_c);
        switch (order){
            case 0:
                defaultOrderImg.setVisibility(View.VISIBLE);
                break;
            case 1:
                nameOrderImg.setVisibility(View.VISIBLE);
                break;
            case 2:
                singerOrderImg.setVisibility(View.VISIBLE);
                break;
            case 3:
                albumOrderImg.setVisibility(View.VISIBLE);
                break;
        }
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels*3/4;
        int height=800;
//        int height = getResources().getDisplayMetrics().heightPixels/2;
        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                LocalSongActivity.refreshView(SongListActivity.this,VAL_UPDATE_UI_PLAY);
                WindowManager.LayoutParams lp = SongListActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                SongListActivity.this.getWindow().setAttributes(lp);
            }
        });
        defaultOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicDaoImpl.orderListMusicByDefault(songList);
                HomeActivity.shared.putInt("orderBy",0);
                popupWindow.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        nameOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicDaoImpl.orderListMusicByName(songList);
                HomeActivity.shared.putInt("orderBy",1);
                popupWindow.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        singerOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicDaoImpl.orderListMusicBySinger(songList);
                HomeActivity.shared.putInt("orderBy",2);
                popupWindow.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        albumOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicDaoImpl.orderListMusicByAlbum(songList);
                HomeActivity.shared.putInt("orderBy",3);
                popupWindow.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = SongListActivity.this.getWindow().getAttributes();
        lp.alpha = 0.5f;
        SongListActivity.this.getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,(getResources().getDisplayMetrics().heightPixels-height)/2);

    }
    void showMusicMorePopup(Context context, SongList songList, int position){
        Music music=songList.getMusics().get(position);
        final View popView = View.inflate(context,R.layout.music_more_popup,null);
        View insertPlayView=popView.findViewById(R.id.insert_play_list);
        View addListView=popView.findViewById(R.id.add_list);
        View setCoverView=popView.findViewById(R.id.set_cover);
        TextView singerTv,nameTv,albumTv;
        View showSongView=popView.findViewById(R.id.show_song_more);
        showSongView.setVisibility(View.GONE);
        View deleteSongView=popView.findViewById(R.id.delete_song);
        deleteSongView.setVisibility(View.VISIBLE);
        singerTv=popView.findViewById(R.id.song_singer);
        nameTv=popView.findViewById(R.id.song_name);
        albumTv=popView.findViewById(R.id.song_album);
        if (music!=null){
            singerTv.setText(music.getSinger());
            nameTv.setText(music.getName());
            albumTv.setText(music.getAlbum());
        }
        //获取屏幕宽高
        int weight = ((Activity)context).getResources().getDisplayMetrics().widthPixels;
        int height = ((Activity)context).getResources().getDisplayMetrics().heightPixels*2/3;

//        int height = getResources().getDisplayMetrics().heightPixels/2;
        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                LocalSongActivity.refreshView(context,VAL_UPDATE_UI_PLAY);
                WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);

        insertPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextPlayPosition=MusicService.playList.indexOf(MusicService.position+1);//获取下一首在播放顺序的位置
                if (nextPlayPosition==-1)
                    nextPlayPosition=0;
                if (MusicService.listMusic.contains(music)){//播放列表已有此歌曲
                    int position=MusicService.listMusic.indexOf(music);//获取在播放列表的位置
                    for (int i=0;i<MusicService.playList.size();i++){
                        if(MusicService.playList.get(i)==position){
                            //与下一首交换播放顺序的位置
                            int nextMusicPosition=MusicService.playList.get(nextPlayPosition);
                            MusicService.playList.set(nextPlayPosition, MusicService.playList.get(i));
                            MusicService.playList.set(i, nextMusicPosition);
                            break;
                        }
                    }
                }else {     //播放列表中没有
                    //插入在最后一个
                    int index=MusicService.listMusic.size();
                    MusicService.playList.add(nextPlayPosition,index);
                    MusicDaoImpl.addPlayMusic(new PlayMusicBean(music.getId(),music.getSId(),music.getMId(),music.getTitle(),music.getSinger(),music.getAlbum(),music.getUrl(),
                            music.getSize(),music.getTime(),music.getName(),music.getAlbumImg(),music.getProgress()));
                    MusicService.listMusic.add(index,music);
                }
                Toast.makeText(context,"添至下一曲播放成功",Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
        addListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Music> musics=new ArrayList<>();
                musics.add(music);
                LocalSongActivity.addMusicToSongList(context,musics);
                popupWindow.dismiss();
            }
        });
        setCoverView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowPosition=position;
                LocalSongActivity.changeCoverImg(context,position);
                popupWindow.dismiss();
            }
        });
        deleteSongView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                final AlertDialog show;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                View view=View.inflate(context, R.layout.sure_del_list, null);
                View sure,cancel;
                TextView issueText=view.findViewById(R.id.issue_text);
                issueText.setText("确认将此歌曲从列表中移除");
                final CheckBox checkBox;
                sure=view.findViewById(R.id.sure);
                cancel=view.findViewById(R.id.cancel);

                alertDialog.setView(view);
                show=alertDialog.create();
                show.show();
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,"删除歌曲:"+music.getName(),Toast.LENGTH_SHORT).show();
                        MusicDaoImpl.deleteMusicFromSonglist(songList,music);
                        LocalSongActivity.refreshView(context,VAL_UPDATE_UI_PLAY);
                        show.dismiss();
                        popupWindow.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });
            }
        });

        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showListPopupMenu(Context context) {
        final View popView = View.inflate(context,R.layout.music_list_popup,null);
        ListView playList=popView.findViewById(R.id.play_list_view);
        View ruleView=popView.findViewById(R.id.change_play_rule);
        final TextView numText=popView.findViewById(R.id.play_list_num);
        View addListView=popView.findViewById(R.id.add_list);
        View delAllView=popView.findViewById(R.id.del_all_list);

        DetailsActivity.setRuleImg(context,ruleView.findViewById(R.id.img_play_rule));
        setRuleText(context,ruleView.findViewById(R.id.text_play_rule));

        if (MusicService.listMusic==null){
            return;
        }
        numText.setText("( "+MusicService.listMusic.size()+" )");


        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*3/4;

        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                LocalSongActivity.refreshView(context,VAL_UPDATE_UI_PLAY);
                WindowManager.LayoutParams lp = SongListActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                SongListActivity.this.getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = SongListActivity.this.getWindow().getAttributes();
        lp.alpha = 0.5f;
        SongListActivity.this.getWindow().setAttributes(lp);

        View.OnClickListener listener=new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                int position= (int) v.getTag();
                if (v.getId()==R.id.song_list_item){//点击播放
                    HomeActivity.musicControl.playPosition(position);
                }else {//点击删除
                    //删除播放列表中的歌曲
                    if (MusicService.listMusic.size()==1){//若只有一首歌
                        MusicService.mPosition=-1;
                        MusicService.position=-1;
                        MusicService.listMusic=null;
                        MusicService.playList=null;
                        MusicService.mlastPlayer.reset();
                        MusicService.mlastPlayer=null;
                        popupWindow.dismiss();
                        return;
                    }
                    if (position==MusicService.mPosition){//判断是否为当前歌曲
                        //位置向前移一首后,播放下一曲
                        MusicService.mPosition -= 1;
                        MusicService.position -= 1;
                        Intent intent = new Intent(MusicService.ACTION);
                        Bundle bundle = new Bundle();
                        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_NEXT);
                        intent.putExtras(bundle);
                        sendBroadcast(intent);
                    }else if (position<MusicService.mPosition) {//在当前歌曲之前
                        MusicService.mPosition -= 1;
                        MusicService.position -= 1;
                        //播放位置向前移一首
                    }
                    Music m=MusicService.listMusic.get(position);
                    MusicDaoImpl.removePlayMusic(new PlayMusicBean(m.getId(),m.getSId(),m.getMId(),m.getTitle(),m.getSinger(),m.getAlbum(),m.getUrl(),
                            m.getSize(),m.getTime(),m.getName(),m.getAlbumImg(),m.getProgress()));
                    MusicService.listMusic.remove(position);
                    HomeActivity.musicControl.setPlayModel(MusicService.NOW_PlAY_MODEL);
                    numText.setText("( "+MusicService.listMusic.size()+" )");
                }
                playAdapter.notifyDataSetChanged();
            }
        };
        playAdapter=new PlayListAdapter(context,MusicService.listMusic,listener);
        playList.setAdapter(playAdapter);
        playList.setSelection(MusicService.position);

        ruleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRuleImg(context,v.findViewById(R.id.img_play_rule));
                setRuleText(context,v.findViewById(R.id.text_play_rule));
            }
        });

        delAllView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog show;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SongListActivity.this);
                View view=View.inflate(context, R.layout.sure_del_list, null);
                View sure,cancel;
                sure=view.findViewById(R.id.sure);
                cancel=view.findViewById(R.id.cancel);

                alertDialog.setView(view);
                show=alertDialog.create();
                show.show();
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MusicService.mPosition=-1;
                        MusicService.position=-1;
                        MusicService.listMusic=null;
                        MusicService.playList=null;
                        if (MusicService.mlastPlayer!=null){
                            MusicService.mlastPlayer.reset();
                            MusicService.mlastPlayer=null;
                        }
                        MusicDaoImpl.emptyPlayMusicList();
                        show.dismiss();
                        popupWindow.dismiss();
                        return;
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });
            }
        });
        addListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                LocalSongActivity.addMusicToSongList(context,MusicService.listMusic);
            }
        });

        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
}
