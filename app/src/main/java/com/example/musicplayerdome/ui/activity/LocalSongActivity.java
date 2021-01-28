package com.example.musicplayerdome.ui.activity;

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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.MusicListAdapter;
import com.example.musicplayerdome.adapter.PlayListAdapter;
import com.example.musicplayerdome.adapter.SimpleSongListAdapter;
import com.example.musicplayerdome.adapter.SongListAdapter;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.bean.MyMusicSearchBean;
import com.example.musicplayerdome.bean.YunSearchResult;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.component.CircleImageView;
import com.example.musicplayerdome.ui.fragment.FragmentMine;
import com.example.musicplayerdome.utils.DownloadLrcUtils;
import com.example.musicplayerdome.utils.FileUtils;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.SharedPreferencesUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.musicplayerdome.service.MusicService.MAIN_UPDATE_UI;
import static com.example.musicplayerdome.service.MusicService.VAL_UPDATE_UI_PAUSE;
import static com.example.musicplayerdome.service.MusicService.VAL_UPDATE_UI_PLAY;
import static com.example.musicplayerdome.service.MusicService.position;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.changeRuleImg;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.setRuleText;

public class LocalSongActivity extends BaseActivity implements View.OnClickListener{
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
    View addLocalView;
    private PlayListAdapter playAdapter;
    SearchView searchView;
    View moreView;
    TextView matchState,matchNum;
    ProgressBar matchProgress;
    MyMusicSearchBean.SongsBean song;
    int nowMatchPosition=0;
    static int nowPosition=-1;
    @Override
    protected int getResId() {
        return R.layout.activity_local_song;
    }

    @Override
    protected void onConfigView() {
        listView=findViewById(R.id.song_list);
        nameTv=findViewById(R.id.listName);
        numTv=findViewById(R.id.list_num);
        mainView=findViewById(R.id.show_music_view);
        playImg=findViewById(R.id.home_song_play);
        songImg=findViewById(R.id.home_song_img);
        songNameTv=findViewById(R.id.home_song_name);
        singerTv=findViewById(R.id.home_song_lrc);
        backPlay=findViewById(R.id.back_play);
        addLocalView=findViewById(R.id.add_local_list_view);
        searchView=findViewById(R.id.list_search_view);
        moreView=findViewById(R.id.more);
        matchState=findViewById(R.id.match_state);
        matchNum=findViewById(R.id.match_num);
        matchProgress=findViewById(R.id.match_progress);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        if (HomeActivity.localSongList.getMusics().size()==0){
            addLocalView.setVisibility(View.VISIBLE);
        }else {
            addLocalView.setVisibility(View.GONE);
        }
        animation=AnimationUtils.loadAnimation(this,R.anim.rotate);
        songImg.setAnimation(animation);
        nameTv.setText(HomeActivity.localSongList.getName());
        numTv.setText("("+HomeActivity.localSongList.getMusics().size()+")");
        initOrderList();
        adapter=new MusicListAdapter(HomeActivity.localSongList.getMusics(),this,this::onClick);
        listView.setAdapter(adapter);
        updateMusicView();
        if (myReceiver==null){
            myReceiver = new MyReceiver(new Handler());
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(MusicService.MAIN_UPDATE_UI);
            registerReceiver(myReceiver, itFilter);
        }
        initListener();
    }
    private void initListener(){
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                nameTv.setVisibility(View.VISIBLE);
                moreView.setVisibility(View.VISIBLE);
                searchView.setQuery("", false);
                searchView.clearFocus();  //可以收起键盘
                searchView.onActionViewCollapsed();    //可以收起SearchView视图
                MusicDaoImpl.orderListMusicByDefault(HomeActivity.localSongList);
                initOrderList();
                adapter=new MusicListAdapter(HomeActivity.localSongList.getMusics(),LocalSongActivity.this,LocalSongActivity.this::onClick);
                listView.setAdapter(adapter);
                numTv.setText("("+HomeActivity.localSongList.getMusics().size()+")");
                return false;
            }
        });
        //搜索图标按钮(打开搜索框的按钮)的点击事件
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTv.setVisibility(View.GONE);
                moreView.setVisibility(View.GONE);
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
                List<Music> musics=MusicDaoImpl.queryListMusic(HomeActivity.localSongList,searchView.getQuery().toString());
                HomeActivity.localSongList.getMusics().clear();
                HomeActivity.localSongList.getMusics().addAll(musics);
                adapter=new MusicListAdapter(HomeActivity.localSongList.getMusics(),LocalSongActivity.this,LocalSongActivity.this::onClick);
                listView.setAdapter(adapter);
                numTv.setText("("+HomeActivity.localSongList.getMusics().size()+")");
                return false;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (MusicService.mlastPlayer!=null
                        &&HomeActivity.musicControl.getMusic().getSId()==HomeActivity.localSongList.getId()){
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void doClick(View view) {
        switch (view.getId()){
            case R.id.btn_return:
                finish();
                break;
            case R.id.play_view:
                HomeActivity.musicControl.play();
                updateMusicView();
                break;
            case R.id.list_view:
                showListPopupMenu(LocalSongActivity.this);
                break;
            case R.id.show_music_view:
                Intent intent=new Intent(LocalSongActivity.this,DetailsActivity.class);
                intent.putExtra("continuePlay",true);
                startActivity(intent);
                break;
            case R.id.back_play:
                long playId=HomeActivity.musicControl.getMusic().getId();
                for (int i=0;i<HomeActivity.localSongList.getMusics().size();i++){
                    Music music=HomeActivity.localSongList.getMusics().get(i);
                    if (music.getId().equals(playId)){
                        listView.setSelection(i);
                    }
                }
                break;
            case R.id.add_local_list:
                startActivityForResult(new Intent(this,AddLocalMusicActivity.class),1);
                break;
            case R.id.play_all_view:
                Intent intent1=new Intent(LocalSongActivity.this,DetailsActivity.class);
                MusicService.initPlayMsg(0, (ArrayList<Music>) HomeActivity.localSongList.getMusics());
                adapter.notifyDataSetChanged();
                startActivity(intent1);
                break;
            case R.id.close_match_view:
                findViewById(R.id.match_view).setVisibility(View.GONE);
                break;
            case R.id.more:
                showPopup(view);
                break;
            case R.id.list_choice_view:
                Intent intent2=new Intent(LocalSongActivity.this,MultipleMusicActivity.class);
                intent2.putExtra("listPosition",0);
                startActivity(intent2);
                break;
        }
    }

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
                            Music music=HomeActivity.localSongList.getMusics().get(nowPosition);
                            int playIndex=MusicService.listMusic.indexOf(music);
                            music.setAlbumImg(coverString);
                            MusicDaoImpl.updateSongListMsg(HomeActivity.localSongList);
                            if (playIndex!=-1){ //音乐列表中有此歌曲信息
                                MusicService.listMusic.get(playIndex).setAlbumImg(coverString);
                                PlayMusicBean playMusicBean=MusicDaoImpl.getPlayListDao().load(MusicService.listMusic.get(playIndex).getId());
                                playMusicBean.setAlbumImg(coverString);
                                MusicDaoImpl.getPlayListDao().update(playMusicBean);
                            }
                        }
                        refreshView(LocalSongActivity.this,VAL_UPDATE_UI_PLAY);
                        nowPosition=-1;
                    } else if (resultCode == UCrop.RESULT_ERROR) {
                        final Throwable cropError = UCrop.getError(data);
                        cropError.printStackTrace();
                    }
                    break;
                default:
                    ArrayList<Music> musics=data.getParcelableArrayListExtra("listMusic");
                    MusicDaoImpl.addMusicListToSongList(HomeActivity.localSongList,musics);
                    HomeActivity.localSongList=MusicDaoImpl.getSongLists().get(0);
                    numTv.setText("("+HomeActivity.localSongList.getMusics().size()+")");
                    initOrderList();
                    adapter=new MusicListAdapter(HomeActivity.localSongList.getMusics(),LocalSongActivity.this,this::onClick);
                    listView.setAdapter(adapter);
                    updateMusicView();
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        updateMusicView();
        adapter=new MusicListAdapter(HomeActivity.localSongList.getMusics(),LocalSongActivity.this,LocalSongActivity.this::onClick);
        listView.setAdapter(adapter);
        numTv.setText("("+HomeActivity.localSongList.getMusics().size()+")");
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int position=(int)v.getTag();
        switch (v.getId()){
            case R.id.music_play_view:
                Intent intent=new Intent(LocalSongActivity.this,DetailsActivity.class);
                if (MusicService.mlastPlayer!=null
                        &&HomeActivity.musicControl.getMusic().getSId()==HomeActivity.localSongList.getId()
                        &&HomeActivity.musicControl.getMusic().getId()==HomeActivity.localSongList.getMusics().get(position).getId())
                    intent.putExtra("continuePlay",true);
                else
                    MusicService.initPlayMsg(position, (ArrayList<Music>) HomeActivity.localSongList.getMusics());
                adapter.notifyDataSetChanged();
                startActivity(intent);
                break;
            case R.id.music_more_view:
                showMusicMorePopup(LocalSongActivity.this,HomeActivity.localSongList,position);
                break;
        }
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
                    adapter=new MusicListAdapter(HomeActivity.localSongList.getMusics(),LocalSongActivity.this,LocalSongActivity.this::onClick);
                    listView.setAdapter(adapter);
                    numTv.setText("("+HomeActivity.localSongList.getMusics().size()+")");
                }
            });
        }
    }
    private static final int PICTURE = 10086; //requestcode
    public static void changeCoverImg(Context context,int position){
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {//因为Android SDK在4.4版本后图片action变化了 所以在这里先判断一下
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        ((Activity)context).startActivityForResult(intent, PICTURE);
    }

    private void initOrderList(){
        int order=HomeActivity.shared.getInt("orderBy",0);
        switch (order){
            case 0:
                MusicDaoImpl.orderListMusicByDefault(HomeActivity.localSongList);
                break;
            case 1:
                MusicDaoImpl.orderListMusicByName(HomeActivity.localSongList);
                break;
            case 2:
                MusicDaoImpl.orderListMusicBySinger(HomeActivity.localSongList);
                break;
            case 3:
                MusicDaoImpl.orderListMusicByAlbum(HomeActivity.localSongList);
                break;
        }
    }
    private void matchListLrc(){
        nowMatchPosition=0;
        ArrayList<Music> musics=new ArrayList<>();
        musics.addAll(MusicDaoImpl.getMusicList(HomeActivity.localSongList.getId()));
        matchNum.setText(nowMatchPosition+"/"+musics.size());
        for (final Music m :
                musics) {
            if (m.getMId()==-1){
                new AsyncTask<Void,Void,Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {

                            ArrayList<MyMusicSearchBean.SongsBean> songs= HttpRequest.getYunFormatSearchSimple(m.getName(),m.getSinger(),1,0);
                            if (songs!=null&&songs.size()>0){
                                song=songs.get(0);
                                try {
                                    String file = DownloadLrcUtils.downloadCover(song.getPic());
                                    if (file!=null&&!file.isEmpty())
                                        song.setPic(file);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (song!=null){
                            m.setAlbumImg(song.getPic()); //设置封面图url
                            m.setMId(Long.valueOf(song.getId()));
                            String id= String.valueOf(song.getId());    //获取歌曲id用于获取歌词信息
                            MusicDaoImpl.getMusicDao().update(m);
                            nowMatchPosition++;
                            if (song.getName().equals(m.getName())){
                                matchMusicLrc(id,m.getName(),m.getSinger(),m.getId(),musics.size(),nowMatchPosition);
                                if (nowMatchPosition==musics.size()){
                                    MusicDaoImpl.emptySongList(HomeActivity.localSongList);
                                    MusicDaoImpl.addMusicListToSongList(HomeActivity.localSongList,musics);
                                    initOrderList();
                                    adapter=new MusicListAdapter(HomeActivity.localSongList.getMusics(),LocalSongActivity.this,LocalSongActivity.this::onClick);
                                    listView.setAdapter(adapter);
                                }
                            }
                        }
                    }
                }.execute();
            }else {
                new AsyncTask<Void,Void,Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Music music=HttpRequest.getMusicDetail(Integer.parseInt(m.getMId().toString()));
                        m.setAlbumImg(music.getAlbumImg()); //设置封面图url
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        matchMusicLrc(m.getMId().toString(),m.getName(),m.getSinger(),m.getId(),musics.size(),nowMatchPosition);
                        if (nowMatchPosition==musics.size()){
                            MusicDaoImpl.emptySongList(HomeActivity.localSongList);
                            MusicDaoImpl.addMusicListToSongList(HomeActivity.localSongList,musics);
                            initOrderList();
                            adapter=new MusicListAdapter(HomeActivity.localSongList.getMusics(),LocalSongActivity.this,LocalSongActivity.this::onClick);
                            listView.setAdapter(adapter);
                        }
                    }
                }.execute();
            }
        }
    }
    private void matchMusicLrc(String id,String name,String singer,long songId,int maxSize,int position){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                HttpRequest.getYunSongLrc(id,name,singer,songId,false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                double progress=(double)position/maxSize;
                matchProgress.setProgress((int)(progress*100));
                matchNum.setText(position+"/"+maxSize);

                if (position==maxSize){
                    matchNum.setText(position+"/"+maxSize);
                    matchState.setText("完成匹配");
                    findViewById(R.id.match_view).setVisibility(View.VISIBLE);
                    nowMatchPosition=0;
                }
            }
        }.execute();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                public void run() {
                    //execute the task
                    if (!HomeActivity.musicControl.isPlaying()) {
                        playImg.setImageDrawable(LocalSongActivity.this.getDrawable(R.drawable.play));
                        songImg.clearAnimation();
                    } else {
                        playImg.setImageDrawable(LocalSongActivity.this.getDrawable(R.drawable.pause));
                        songImg.startAnimation(animation);
                    }
                }
            }, 600);
        }
        if (HomeActivity.localSongList.getMusics().size()==0){
            addLocalView.setVisibility(View.VISIBLE);
        }else {
            addLocalView.setVisibility(View.GONE);
        }
        if (playAdapter!=null) playAdapter.notifyDataSetChanged();
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
                refreshView(context,VAL_UPDATE_UI_PLAY);
                WindowManager.LayoutParams lp = LocalSongActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                LocalSongActivity.this.getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = LocalSongActivity.this.getWindow().getAttributes();
        lp.alpha = 0.5f;
        LocalSongActivity.this.getWindow().setAttributes(lp);

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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocalSongActivity.this);
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
                addMusicToSongList(context,MusicService.listMusic);
            }
        });

        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    private void showOrderPopup(){
        int order=HomeActivity.shared.getInt("orderBy",0);
        final View popView = View.inflate(LocalSongActivity.this,R.layout.select_order_menu,null);
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
                refreshView(LocalSongActivity.this,VAL_UPDATE_UI_PLAY);
                WindowManager.LayoutParams lp = LocalSongActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                LocalSongActivity.this.getWindow().setAttributes(lp);
            }
        });
        defaultOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicDaoImpl.orderListMusicByDefault(HomeActivity.localSongList);
                HomeActivity.shared.putInt("orderBy",0);
                popupWindow.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        nameOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicDaoImpl.orderListMusicByName(HomeActivity.localSongList);
                HomeActivity.shared.putInt("orderBy",1);
                popupWindow.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        singerOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicDaoImpl.orderListMusicBySinger(HomeActivity.localSongList);
                HomeActivity.shared.putInt("orderBy",2);
                popupWindow.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        albumOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicDaoImpl.orderListMusicByAlbum(HomeActivity.localSongList);
                HomeActivity.shared.putInt("orderBy",3);
                popupWindow.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = LocalSongActivity.this.getWindow().getAttributes();
        lp.alpha = 0.5f;
        LocalSongActivity.this.getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,(getResources().getDisplayMetrics().heightPixels-height)/2);

    }
    public static void refreshView(Context context, int state){
        Intent actionIntent = new Intent(MusicService.MAIN_UPDATE_UI);
        actionIntent.putExtra(MusicService.KEY_MAIN_ACTIVITY_UI_BTN,state);
        actionIntent.putExtra(MusicService.KEY_MAIN_ACTIVITY_UI_TEXT, MusicService.position);
//        updateNotification();
        context.sendBroadcast(actionIntent);
    }

    private void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.local_list_menu, popup.getMenu());
        //绑定菜单项的点击事件
        popup.getMenu().removeItem(R.id.edit_song_list);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_local_music:
                        startActivityForResult(new Intent(LocalSongActivity.this,AddLocalMusicActivity.class),1);
                        break;
                    case R.id.select_order:
                        showOrderPopup();
                        break;
                    case R.id.match_lrc:
                        findViewById(R.id.match_view).setVisibility(View.VISIBLE);
                        if (nowMatchPosition==0)
                        matchListLrc();
                        break;
                }
                return false;
            }
        });

        popup.show();
    }
    public static void showMusicMorePopup(Context context,SongList songList,int position){
        Music music=songList.getMusics().get(position);
        final View popView = View.inflate(context,R.layout.music_more_popup,null);
        View insertPlayView=popView.findViewById(R.id.insert_play_list);
        View addListView=popView.findViewById(R.id.add_list);
        View setCoverView=popView.findViewById(R.id.set_cover);
        TextView singerTv,nameTv,albumTv;
        View showSongView=popView.findViewById(R.id.show_song_more);
        showSongView.setVisibility(View.VISIBLE);
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
                refreshView(context,VAL_UPDATE_UI_PLAY);
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
                addMusicToSongList(context,musics);
                popupWindow.dismiss();
            }
        });
        setCoverView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowPosition=position;
                changeCoverImg(context,position);
                popupWindow.dismiss();
            }
        });
        showSongView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                dialog.setTitle("歌曲:"+music.getName())
                        .setMessage("播放时长:"+toTime((int)music.getTime())
                                +"\n文件大小:"+music.getSize()/(1024*1024)+"M"
                                +"\n文件路径:"+music.getUrl())
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .create().show();
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
                        refreshView(context,VAL_UPDATE_UI_PLAY);
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
    public static void addMusicToSongList(Context context, List<Music> musics){
        final View popView = View.inflate(context,R.layout.add_music_to_list_popup,null);
        View createSongList=popView.findViewById(R.id.create_song_list);
        ListView listView=popView.findViewById(R.id.select_song_list_view);
        SimpleSongListAdapter adapter=new SimpleSongListAdapter(HomeActivity.songLists,context);
        listView.setAdapter(adapter);
        FragmentMine.setListViewHeightBasedOnChildren(listView);
        //获取屏幕宽高
        int weight = ((Activity)context).getResources().getDisplayMetrics().widthPixels*9/10;
        int height=1200;

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
                refreshView(context,VAL_UPDATE_UI_PLAY);
                WindowManager.LayoutParams lp =((Activity)context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicDaoImpl.addMusicListToSongList(HomeActivity.songLists.get(position),musics);
                Toast.makeText(context,"加入歌单"+HomeActivity.songLists.get(position).getName()+"成功!"
                        ,Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
        createSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog show;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                View view=View.inflate(context, R.layout.create_song_list_popup, null);
                View sure,cancel;
                EditText editText;
                sure=view.findViewById(R.id.create_btn);
                cancel=view.findViewById(R.id.cancel_btn);
                editText=view.findViewById(R.id.list_name);
                alertDialog.setView(view);
                show=alertDialog.create();
                show.show();
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String listName=editText.getText().toString();
                        if (!listName.isEmpty()){
                            SongList songList=new SongList();
                            songList.setIntroduce("暂无简介");
                            songList.setName(listName);
                            songList.setTime(new Date());
                            songList.setCoverImg("");
                            MusicDaoImpl.createMusicsSongList(songList,musics);
                            HomeActivity.songLists=MusicDaoImpl.getSongLists();
                            for (int i=0;i<4;i++){
                                HomeActivity.songLists.remove(0);
                            }
                            show.dismiss();
                            popupWindow.dismiss();
                            Toast.makeText(context,"加入新建歌单成功!",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,"请输入歌单名称",Toast.LENGTH_SHORT).show();
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
        });
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,(((Activity)context).getResources().getDisplayMetrics().heightPixels-height)/2);
    }
    public static String toTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }
}
