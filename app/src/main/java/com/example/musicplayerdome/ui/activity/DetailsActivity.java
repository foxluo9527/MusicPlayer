package com.example.musicplayerdome.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.MusicListAdapter;
import com.example.musicplayerdome.adapter.PlayListAdapter;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.bean.MyMusicSearchBean;
import com.example.musicplayerdome.bean.YunSearchResult;
import com.example.musicplayerdome.bean.YunSongLrcBean;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.component.MarqueeTextView;
import com.example.musicplayerdome.ui.component.MyRadioRelativeLayout;
import com.example.musicplayerdome.utils.BitmapBlurUtil;
import com.example.musicplayerdome.utils.DownloadLrcUtils;
import com.example.musicplayerdome.utils.FileUtils;
import com.example.musicplayerdome.utils.GlideUtil;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.ImgHelper;
import com.example.musicplayerdome.utils.MusicList;
import com.example.musicplayerdome.utils.Strings;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;
import com.lauzy.freedom.library.Lrc;
import com.lauzy.freedom.library.LrcHelper;
import com.lauzy.freedom.library.LrcView;
import com.yalantis.ucrop.UCrop;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.musicplayerdome.service.MusicService.VAL_UPDATE_UI_PLAY;
import static com.example.musicplayerdome.service.MusicService.proxy;
import static com.example.musicplayerdome.utils.DownloadLrcUtils.downloadLrc;
import static com.example.musicplayerdome.utils.ImgHelper.getRoundedCornerBitmap;

public class DetailsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btn_return,btn_pre,btn_next,btn_play,btn_more,btn_down,btn_like;
    private ImageView albumImg;
    private SeekBar seekBar;
    private SeekBar amSeekBar;
    private MarqueeTextView tv_title,tv_album;
    private TextView tv_cur_time,tv_total_time;
    private static final int UPDATE_UI = 0;
    private LinearLayout mainLayout;
    private ImageView play_rule;
    MyReceiver myReceiver;
    private PlayListAdapter adapter;
    private TextView trans;
    public static final String LrcPath=Environment.getExternalStorageDirectory()+"/MusicPlayer/lyric";
    public static final String SharePath=Environment.getExternalStorageDirectory()+"/MusicPlayer/share/";
    AudioManager am;
    LrcView lrcView;
    VolumeReceiver amReceiver;
    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    updateUI();
                    if(adapter!=null){
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };
    private Handler lrcReadyHander = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            trans.setText("译");
            MusicService.showTrans=false;
            if(MusicService.nowLrcs!=null){
                if (MusicService.nowTransLrcs==null){
                    trans.setVisibility(View.GONE);
                }else {
                    trans.setVisibility(View.VISIBLE);
                }
                MusicService.tempLrcs.clear();
                MusicService.tempLrcs.addAll(MusicService.nowLrcs);
                lrcView.setLrcData(MusicService.nowLrcs);
                findViewById(R.id.noneLrc).setVisibility(View.GONE);
                findViewById(R.id.lrc_view).setVisibility(View.VISIBLE);
            }else {
                findViewById(R.id.noneLrc).setVisibility(View.VISIBLE);
                findViewById(R.id.lrc_view).setVisibility(View.GONE);
            }
        }
    };

    public DetailsActivity() {
    }
    @Override
    protected int getResId() {
        return R.layout.activity_details;
    }

    @Override
    protected void onConfigView() {
        bindViews();
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        nowPosition=-1;
        updatePlayText();
        myReceiver = new MyReceiver(new Handler());
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction(MusicService.MAIN_UPDATE_UI);
        itFilter.addAction(MusicService.ACTION);
        getApplicationContext().registerReceiver(myReceiver, itFilter);
        Boolean continuePlay=getIntent().getBooleanExtra("continuePlay",false);
        if (!continuePlay||MusicService.mlastPlayer==null)
        HomeActivity.musicControl.playPosition(MusicService.position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            if (HomeActivity.musicControl.getMusic().getId().equals(data.getLongExtra("musicId",-1))){
                initLrc(HomeActivity.musicControl.getMusic());
            }
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        private final Handler handler;
        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (intent.getAction().equals(MusicService.ACTION)){
                        if (intent.getIntExtra(MusicService.KEY_USR_ACTION,-1)==MusicService.ACTION_LIKE){
                           updateLike();
                        }
                    }else {
                        if (MusicService.listMusic==null||MusicService.listMusic.size()==0){
                            return;
                        }
                        int play_pause = intent.getIntExtra(MusicService.KEY_MAIN_ACTIVITY_UI_BTN, -1);
                        int songid = intent.getIntExtra(MusicService.KEY_MAIN_ACTIVITY_UI_TEXT, -1);
                        tv_title.setText(MusicService.listMusic.get(songid).getName());
                        switch (play_pause) {
                            case MusicService.VAL_UPDATE_UI_PLAY:
                                btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pause));
                                break;
                            case MusicService.VAL_UPDATE_UI_PAUSE:
                                btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.play));
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }
    }


    private void bindViews() {
        btn_pre = findViewById(R.id.btn_pre);
        btn_play = findViewById(R.id.btn_play);
        btn_next = findViewById(R.id.btn_next);
        btn_return = findViewById(R.id.btn_return);
        seekBar =  findViewById(R.id.sb);
        amSeekBar=findViewById(R.id.sb3);
        tv_title = findViewById(R.id.tv_title);
        play_rule=findViewById(R.id.btn_play_rule);
        tv_cur_time =findViewById(R.id.tv_cur_time);
        tv_total_time = findViewById(R.id.tv_total_time);
        albumImg=findViewById(R.id.albumImg);
        tv_album=findViewById(R.id.tv_album);
        mainLayout=findViewById(R.id.LinearLayout1);
        btn_down=findViewById(R.id.btn_down);
        btn_like=findViewById(R.id.btn_like);
        btn_more=findViewById(R.id.btn_more);
        trans=findViewById(R.id.trans_btn);
        findViewById(R.id.btn_share).setOnClickListener(this::onClick);
        btn_down.setOnClickListener(this::onClick);
        btn_like.setOnClickListener(this::onClick);
        btn_more.setOnClickListener(this::onClick);
        findViewById(R.id.btn_list).setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_return.setOnClickListener(this);
        play_rule.setOnClickListener(this);
        albumImg.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                String url=HomeActivity.musicControl.getMusic().getAlbumImg();
                if (url.contains("http")){
                    url=MusicService.proxy.getProxyUrl(url);
                }
                File downloadDir;
                Intent intent=new Intent(DetailsActivity.this,ShowImgActivity.class);
                downloadDir = new File(Environment.getExternalStorageDirectory(), "MusicPlayer/downloadImg");
                intent.putExtra("filePath",downloadDir.getPath());
                intent.putExtra("url",url);
                startActivity(intent);
                return true;
            }
        });
        albumImg.setOnClickListener(this);
        trans.setOnClickListener(this);
        lrcView=findViewById(R.id.lrc_view);
        lrcView.setOnClickListener(this);
        lrcView.setOnPlayIndicatorLineListener(new LrcView.OnPlayIndicatorLineListener() {
            @Override
            public void onPlay(long time, String content) {
                seekBar.setProgress((int)time);
                HomeActivity.musicControl.seekTo((int)time);
            }
        });
        ((MyRadioRelativeLayout)findViewById(R.id.showAlbum)).setmSetOnSlideListener(new MyRadioRelativeLayout.setOnSlideListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onRightToLeftSlide() {
                next();
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onLeftToRightSlide() {
                pre();
            }

            @Override
            public void onClick() {
                System.out.println("显示歌词");
                findViewById(R.id.showAlbum).setVisibility(View.GONE);
                findViewById(R.id.showLrc).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.showLrc).setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Progress bar change
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Start touching the progress bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Stop touching the progress bar
                //停止滑动后跳转
                int progress=seekBar.getProgress();
                //判断是否为本地歌曲，若未缓冲则最多快进至缓冲区内
                if (progress>HomeActivity.musicControl.getCacheCurrent()&&(!HomeActivity.musicControl.getIsLocal()&&!MusicService.proxy.isCached(HomeActivity.musicControl.getMusic().getUrl()))){
                    int subProgress=500;
                    while (progress<(subProgress-10)){
                        subProgress-=10;
                    }
                    progress=HomeActivity.musicControl.getCacheCurrent()-subProgress;
                    seekBar.setProgress(progress);
                }
                HomeActivity.musicControl.seekTo(progress);
                lrcView.updateTime(progress);
            }
        });
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //获取系统最大音量
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        amSeekBar.setMax(maxVolume);
        //获取当前音量
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        amSeekBar.setProgress(currentVolume);
        amSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    //设置系统音量
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                    seekBar.setProgress(currentVolume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        if (amReceiver==null){
            amReceiver = new VolumeReceiver();
            IntentFilter filter = new IntentFilter() ;
            filter.addAction("android.media.VOLUME_CHANGED_ACTION") ;
            registerReceiver(amReceiver, filter) ;
        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
        }else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
        }else if (event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return true;
    }
    private class VolumeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                amSeekBar.setProgress(currentVolume);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                play();
                break;
            case R.id.btn_next:
                next();
                break;
            case R.id.btn_pre:
                pre();
                break;
            case R.id.btn_return:
                finish();
                break;
            case R.id.btn_share:
                showShare(HomeActivity.musicControl.getMusic());
                break;
            case R.id.btn_discuss:
                Music music1=HomeActivity.musicControl.getMusic();
                if (music1.getMId()==-1){
                    Toast.makeText(DetailsActivity.this,"未找到歌曲信息",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent discussIntent=new Intent(DetailsActivity.this,SongCommentsActivity.class);
                Bundle discussBundle=new Bundle();
                discussBundle.putParcelable("music",music1);
                discussIntent.putExtras(discussBundle);
                startActivity(discussIntent);
                break;
            case R.id.btn_play_rule:
                changeRuleImg(DetailsActivity.this,v);
                break;
            case R.id.btn_list:
                showListPopupMenu();
                break;
            case R.id.albumImg:
                System.out.println("显示歌词");
                findViewById(R.id.showAlbum).setVisibility(View.GONE);
                findViewById(R.id.showLrc).setVisibility(View.VISIBLE);
                break;
            case R.id.showLrc:
            case R.id.lrc_view:
                System.out.println("隐藏歌词");
                findViewById(R.id.showAlbum).setVisibility(View.VISIBLE);
                findViewById(R.id.showLrc).setVisibility(View.GONE);
                break;
            case R.id.trans_btn:
                if(MusicService.nowLrcs!=null&&MusicService.nowTransLrcs!=null){
                    if (MusicService.nowLrcs.size()>0){
                        MusicService.tempLrcs.clear();
                        MusicService.tempLrcs.addAll(MusicService.nowLrcs);
                    } else {
                        MusicService.nowLrcs.addAll(MusicService.tempLrcs);
                    }
                    if (MusicService.nowTransLrcs.size()>0){
                        MusicService.tempTransLrcs.clear();
                        MusicService.tempTransLrcs.addAll(MusicService.nowTransLrcs);
                    } else {
                        MusicService.nowTransLrcs.addAll(MusicService.tempTransLrcs);
                    }
                    if (MusicService.showTrans){
                        MusicService.showTrans=false;
                        trans.setText("译");
                        lrcView.setLrcData(MusicService.nowLrcs);
                    }else {
                        MusicService.showTrans=true;
                        trans.setText("原");
                        lrcView.setLrcData(MusicService.nowTransLrcs);
                    }
                }
                break;
            case R.id.btn_more:
                showPlayMusicMore(DetailsActivity.this);
                break;
            case R.id.btn_like:
                boolean isLike=false;
                for (Music m:
                        HomeActivity.likeSongList.getMusics()) {
                    if (m.getUrl().equals(HomeActivity.musicControl.getMusic().getUrl())){
                        isLike=true;
                        MusicDaoImpl.deleteMusicFromSonglist( HomeActivity.likeSongList,m);
                        break;
                    }
                }
                if (isLike){
                    btn_like.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.like));
                }else {
                    MusicDaoImpl.addMusicToSongList(HomeActivity.likeSongList,HomeActivity.musicControl.getMusic());
                    btn_like.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.like_c));
                }
                Intent intent = new Intent(MusicService.ACTION);
                Bundle bundle = new Bundle();
                bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_UPDATE);
                intent.putExtras(bundle);
                sendBroadcast(intent);
                break;
            case R.id.btn_down:
                if (HomeActivity.musicControl.getMusic().getProgress()<100){
                    Music music=HomeActivity.musicControl.getMusic();
                    for (Music m:
                            HomeActivity.downSongList.getMusics()) {
                        if (m.getName().equals(music.getName())&&m.getSinger().equals(music.getSinger())&&m.getAlbum().equals(music.getAlbum())){
                            Toast.makeText(DetailsActivity.this,"此歌曲已在下载列表!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    MusicDaoImpl.addMusicToSongList(HomeActivity.downSongList,music);
                    Intent intent1=new Intent(DetailsActivity.this, DownloadActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent1);
                    finish();
                }
                break;
        }
    }
    String cover=null;
    boolean isShareing=false;
    private void showShare(Music music) {
        if (isShareing){
            return;
        }
        if (music.getMId()==null||music.getMId()==-1||music.getMId().equals(-1)){
            Toast.makeText(DetailsActivity.this,"未找到链接地址",Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingDialog();
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                isShareing=true;
                if (music.getAlbumImg().contains("http")){
                    try {
                        cover=DownloadLrcUtils.downloadCover(music.getAlbumImg());//保存图片到SD卡
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    cover=music.getAlbumImg();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismissLoadingDialog();
                OnekeyShare oks = new OnekeyShare();
                // title标题，微信、QQ和QQ空间等平台使用
                oks.setTitle(getString(R.string.share));
                // titleUrl QQ和QQ空间跳转链接
                oks.setTitleUrl(Strings.SIMPLE_PLAY_MUSIC_URL+music.getMId());
                // text是分享文本，所有平台都需要这个字段
                oks.setText("分享音乐:"+music.getName()+"-"+music.getSinger());
                // imagePath是图片的本地路径，确保SDcard下面存在此张图片
                oks.setImagePath(cover);
                // url在微信、Facebook等平台中使用
                oks.setUrl(Strings.SIMPLE_PLAY_MUSIC_URL+music.getMId());
                // 启动分享GUI
                oks.show(DetailsActivity.this);
                isShareing=false;
            }
        }.execute();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //Start the update UI bar after entering the interface
        if (HomeActivity.musicControl != null) {
            handler.sendEmptyMessage(UPDATE_UI);
        }
        updateLike();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unbind from the service after exiting
        getApplicationContext().unregisterReceiver(myReceiver);
        unregisterReceiver(amReceiver);
        if (srcBitmap!=null&&!srcBitmap.isRecycled()){
            srcBitmap.recycle();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the progress of the update progress bar
        handler.removeCallbacksAndMessages(null);
    }

    //Update progress bar
    private void updateProgress() {
        long currenPostion = HomeActivity.musicControl.getCurrenPostion();
        seekBar.setProgress((int)currenPostion);
        lrcView.updateTime(currenPostion);
    }

    private void updateSecondProgress(){
        int cachePosition=HomeActivity.musicControl.getCacheCurrent();
        seekBar.setSecondaryProgress(cachePosition);
    }
    //Update button text
    public void updatePlayText() {
        if(MusicService.mlastPlayer!=null &&MusicService.mlastPlayer.isPlaying()){
            btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pause));
        }else{
            btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.play));
        }
    }

    public void play() {
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_PLAY_PAUSE);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        updatePlayText();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void next() {
        trans.setVisibility(View.GONE);
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_NEXT);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        updatePlayText();
        updateUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void pre() {
        trans.setVisibility(View.GONE);
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_PRE);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        updatePlayText();
        updateUI();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void changeRuleImg(Context context,View view){
        if (MusicService.NOW_PlAY_MODEL==MusicService.CIRCULATE_MODEL){ //列表循环，变化为单曲循环
            ((ImageView)view).setImageDrawable(context.getDrawable(R.drawable.single));
            HomeActivity.musicControl.setPlayModel(MusicService.CIRCULATE_ONE);
        }else if (MusicService.NOW_PlAY_MODEL==MusicService.CIRCULATE_ONE){   //单曲循环，变化为随机播放
            ((ImageView)view).setImageDrawable(context.getDrawable(R.drawable.random));
            HomeActivity.musicControl.setPlayModel(MusicService.RANDOM_MODEL);
        }else if (MusicService.NOW_PlAY_MODEL==MusicService.RANDOM_MODEL){   //随机播放，变化为列表循环
            ((ImageView)view).setImageDrawable(context.getDrawable(R.drawable.circulate));
            HomeActivity.musicControl.setPlayModel(MusicService.CIRCULATE_MODEL);
        }
    }
    public static void setRuleText(Context context,View view){
        if (MusicService.NOW_PlAY_MODEL==MusicService.CIRCULATE_MODEL){ //列表循环
            ((TextView)view).setText("列表循环");
        }else if (MusicService.NOW_PlAY_MODEL==MusicService.CIRCULATE_ONE){   //单曲循环
            ((TextView)view).setText("单曲循环");
        }else if (MusicService.NOW_PlAY_MODEL==MusicService.RANDOM_MODEL){   //随机播放
            ((TextView)view).setText("随机播放");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setRuleImg(Context context,View view){
        if (MusicService.NOW_PlAY_MODEL==MusicService.CIRCULATE_MODEL){ //列表循环
            ((ImageView)view).setImageDrawable(context.getDrawable(R.drawable.circulate));
        }else if (MusicService.NOW_PlAY_MODEL==MusicService.CIRCULATE_ONE){   //单曲循环
            ((ImageView)view).setImageDrawable(context.getDrawable(R.drawable.single));
        }else if (MusicService.NOW_PlAY_MODEL==MusicService.RANDOM_MODEL){   //随机播放
            ((ImageView)view).setImageDrawable(context.getDrawable(R.drawable.random));
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showListPopupMenu() {
        final View popView = View.inflate(this,R.layout.music_list_popup,null);
        ListView playList=popView.findViewById(R.id.play_list_view);
        View ruleView=popView.findViewById(R.id.change_play_rule);
        final TextView numText=popView.findViewById(R.id.play_list_num);
        View addListView=popView.findViewById(R.id.add_list);
        View delAllView=popView.findViewById(R.id.del_all_list);

        setRuleImg(DetailsActivity.this,ruleView.findViewById(R.id.img_play_rule));
        setRuleText(DetailsActivity.this,ruleView.findViewById(R.id.text_play_rule));

        if (MusicService.listMusic==null){
            finish();
            return;
        }
        numText.setText("( "+MusicService.listMusic.size()+" )");
        View.OnClickListener listener=new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                int position= (int) v.getTag();
                if (v.getId()==R.id.song_list_item){//点击播放
                    HomeActivity.musicControl.playPosition(position);
                }else {//点击删除
                    //删除播放列表中的歌曲
                    if (MusicService.listMusic.size()==1){//若只有一首歌,退出播放界面
                        MusicService.mPosition=-1;
                        MusicService.position=-1;
                        MusicService.listMusic=null;
                        MusicService.playList=null;
                        MusicService.mlastPlayer.reset();
                        MusicService.mlastPlayer=null;
                        finish();
                        return;
                    }
                    if (position==MusicService.mPosition){//判断是否为当前歌曲
                        //位置向前移一首后,播放下一曲
                        MusicService.mPosition -= 1;
                        MusicService.position -= 1;
                        next();
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
                adapter.notifyDataSetChanged();
            }
        };
        adapter=new PlayListAdapter(DetailsActivity.this,MusicService.listMusic,listener);
        playList.setAdapter(adapter);
        playList.setSelection(MusicService.position);

        ruleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRuleImg(DetailsActivity.this,v.findViewById(R.id.img_play_rule));
                setRuleText(DetailsActivity.this,v.findViewById(R.id.text_play_rule));
            }
        });

        delAllView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog show;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailsActivity.this);
                View view=View.inflate(DetailsActivity.this, R.layout.sure_del_list, null);
                View sure,cancel;
                final CheckBox checkBox;
                sure=view.findViewById(R.id.sure);
                cancel=view.findViewById(R.id.cancel);

                alertDialog.setView(view);
                show=alertDialog.create();
                show.show();
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                        MusicService.mPosition=-1;
                        MusicService.position=-1;
                        MusicService.listMusic=null;
                        MusicService.playList=null;
                        if (MusicService.mlastPlayer!=null){
                            MusicService.mlastPlayer.reset();
                            MusicService.mlastPlayer=null;
                        }
                        MusicDaoImpl.emptyPlayMusicList();
                        finish();
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
                WindowManager.LayoutParams lp = DetailsActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                DetailsActivity.this.getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = DetailsActivity.this.getWindow().getAttributes();
        lp.alpha = 0.5f;
        DetailsActivity.this.getWindow().setAttributes(lp);
        addListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                LocalSongActivity.addMusicToSongList(DetailsActivity.this,MusicService.listMusic);
            }
        });
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    private void showPlayMusicMore(Context context){
        Music music=HomeActivity.musicControl.getMusic();
        if (!music.getId().equals(MusicService.listMusic.get(MusicService.position).getId())){
            return;
        }
        final View popView = View.inflate(context,R.layout.music_more_popup,null);
        View insertPlayView=popView.findViewById(R.id.insert_play_list);
        insertPlayView.setVisibility(View.GONE);
        View addListView=popView.findViewById(R.id.add_list);
        View setCoverView=popView.findViewById(R.id.set_cover);
        View sourceView=popView.findViewById(R.id.song_source);
        View searchLrcView=popView.findViewById(R.id.search_lrc);
        searchLrcView.setVisibility(View.VISIBLE);
        sourceView.setVisibility(View.VISIBLE);
        setCoverView.setVisibility(View.GONE);
        TextView singerTv,nameTv,albumTv;
        singerTv=popView.findViewById(R.id.song_singer);
        nameTv=popView.findViewById(R.id.song_name);
        albumTv=popView.findViewById(R.id.song_album);
        SongList songList=MusicDaoImpl.getSongList(MusicService.listMusic.get(MusicService.position).getSId());
        if (songList!=null){
            ((TextView)sourceView.findViewById(R.id.song_list_name)).setText(songList.getName());
        }else {
            ((TextView)sourceView.findViewById(R.id.song_list_name)).setText("未找到歌单信息");
            ((TextView)sourceView.findViewById(R.id.song_list_name)).setTextColor(Color.GRAY);
        }
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
                WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);

        addListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Music> musics=new ArrayList<>();
                musics.add(music);
                LocalSongActivity.addMusicToSongList(context,musics);
                popupWindow.dismiss();
            }
        });
        sourceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songList!=null){
                    Intent intent=null;
                    if (songList.getId()==1){
                        intent=new Intent(context,LocalSongActivity.class);
                    }else if (songList.getId()==2) {
                        intent=new Intent(context,RecordMusicActivity.class);
                    }else if (songList.getId()==3){
                    }else if (songList.getId()==4){
                        intent=new Intent(context,LikeMusicActivity.class);
                    }else {
                        intent=new Intent(context,SongListActivity.class);
                        intent.putExtra("songListId",songList.getId());
                        intent.putExtra("listPosition",MusicDaoImpl.getSongLists().indexOf(songList));
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            }
        });
        searchLrcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DetailsActivity.this,SearchLrcActivity.class);
                intent.putExtra("musicName",music.getName());
                intent.putExtra("musicSinger",music.getSinger());
                intent.putExtra("musicId",music.getId());
                startActivityForResult(intent,1);
            }
        });
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    public static int nowPosition=-1;
    Bitmap srcBitmap;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateUI(){
        if (MusicService.listMusic==null){
            finish();
            return;
        }
        Music music=HomeActivity.musicControl.getMusic();
        if (nowPosition!=HomeActivity.musicControl.getPosition()){
            trans.setVisibility(View.GONE);
            String albumImgUri=music.getAlbumImg();
            if (MusicService.proxy!=null&&albumImgUri.contains("http")){
                albumImgUri=MusicService.proxy.getProxyUrl(albumImgUri);
            }
            String str = music.getName();
            Long id=music.getId();
            String album=music.getSinger();
            tv_title.setText(str);
            tv_album.setText(album);
            if (MusicDaoImpl.checkLocalMusic(music)){
                btn_down.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.undone));
            }else {
                btn_down.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.down2));
            }
            updateLike();
            Glide.with(DetailsActivity.this)
                    .load(albumImgUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(500,500)
                    .listener(GlidePalette.with(albumImgUri)
                        .use(GlidePalette.Profile.MUTED_LIGHT)
                        .intoBackground(mainLayout)
                    )
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(
                                @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            srcBitmap = ImgHelper.drawableToBitmap(resource);
                            if (srcBitmap.getWidth()>srcBitmap.getHeight()){
                                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getHeight(), srcBitmap.getHeight());
                            }else if (srcBitmap.getWidth()<srcBitmap.getHeight()){
                                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getWidth());
                            }
                            srcBitmap=getRoundedCornerBitmap(srcBitmap,15f);
                            albumImg.setImageBitmap(srcBitmap);
                        }
                    });
            findViewById(R.id.noneLrc).setVisibility(View.VISIBLE);
            findViewById(R.id.lrc_view).setVisibility(View.GONE);
            initLrc(music);
            nowPosition=HomeActivity.musicControl.getPosition();
        }
        //Set the maximum value of the progress bar
        if (MusicService.mlastPlayer!=null) {
            long cur_time = HomeActivity.musicControl.getCurrenPostion(), total_time = HomeActivity.musicControl.getDuration();
            tv_cur_time.setText(timeToString(cur_time));
            tv_total_time.setText(timeToString(total_time));
            seekBar.setMax((int)total_time);
            //Set the progress of the progress bar
            seekBar.setProgress((int)cur_time);
            updateProgress();
            if (!HomeActivity.musicControl.getIsLocal()){
                updateSecondProgress();
            }else {
                seekBar.setSecondaryProgress(0);
            }
            setRuleImg(DetailsActivity.this,play_rule);
            //Update the UI bar every 500 milliseconds using Handler
            handler.sendEmptyMessageDelayed(UPDATE_UI, 500);
        }
    }
    private void updateLike(){
        boolean isLike=false;
        for (Music m:
                HomeActivity.likeSongList.getMusics()) {
            if (m.getUrl().equals(HomeActivity.musicControl.getMusic().getUrl())){
                isLike=true;
                break;
            }
        }
        if (isLike){
            btn_like.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.like_c));
        }else {
            btn_like.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.like));
        }
    }
    /**
     * 歌词迷api获取歌词信息
     * @param name
     * @param singer
     */
    private void getSongLrc(String name,String singer,long songId){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (songId==HomeActivity.musicControl.getMusic().getId())
                    HttpRequest.getMiSongLrc(DetailsActivity.this,name,singer,songId);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                lrcReadyHander.sendEmptyMessage(0);
            }
        }.execute();
    }
    /**
     * 歌曲名称
     * 歌手
     * 网易云获取单首歌曲信息
     */
    MyMusicSearchBean.SongsBean song=null;
    private void getYunSongMsgByNS(String name, String singer,long songId) {
        MusicService.nowLrcs=null;
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList< MyMusicSearchBean.SongsBean> songs=HttpRequest.getYunFormatSearchSimple(name,singer,1,0);
                if (songs!=null&&songs.size()>0){
                    song=songs.get(0);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (song!=null){
                    Music music=HomeActivity.musicControl.getMusic();
                    music.setAlbumImg(song.getPic()); //设置封面图url
                    String id= String.valueOf(song.getId());    //获取歌曲id用于获取歌词信息
                    if (songId==music.getId()){
                        getYunLrc(id,name,singer,songId);
                        MusicDaoImpl.getMusicDao().update(music);
                    }
                }
            }
        }.execute();
    }

    /**
     * 网易云获取歌词信息
     */
    public void  getYunLrc(String id,String name,String singer,long songId){
        MusicService.nowLrcs=null;
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                HttpRequest.getYunSongLrc(id,name,singer,songId,true);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (MusicService.nowLrcs==null){
                    getSongLrc(name,singer,songId);
                }else if (songId==HomeActivity.musicControl.getMusic().getId()){
                    lrcReadyHander.sendEmptyMessage(0);
                }
            }
        }.execute();
    }
    private void initLrc(Music music){
        MusicService.nowLrcs=null;
        String fileName=music.getName()+"-"+music.getSinger()+".lrc";
        String transFileName=music.getName()+"-"+music.getSinger()+"-trans.lrc";
        Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(fileName);
        fileName= matcher.replaceAll("");
        Matcher matcher1 = pattern.matcher(transFileName);
        transFileName=matcher1.replaceAll("");
        MusicService.nowLrcs= (ArrayList<Lrc>) LrcHelper.parseLrcFromFile(new File(LrcPath+"/"+fileName));
        MusicService.nowTransLrcs=null;
        MusicService.nowTransLrcs= (ArrayList<Lrc>) LrcHelper.parseLrcFromFile(new File(LrcPath+"/"+transFileName));
        if (MusicService.nowLrcs==null){
            setLrcView(music);
        }else {
            lrcReadyHander.sendEmptyMessage(0);
        }
    }
    private void setLrcView(Music music){
        if (music.getMId()==-1) {
            getYunSongMsgByNS(music.getName(),music.getSinger(),music.getId());
        } else {
            getYunLrc(String.valueOf(music.getMId()),music.getName(),music.getSinger(),music.getId());
            MusicDaoImpl.getMusicDao().update(music);
        }
    }
    private String timeToString(long time) {
        time /= 1000;
        return String.format("%02d:%02d",time/60,time%60);
    }

}
