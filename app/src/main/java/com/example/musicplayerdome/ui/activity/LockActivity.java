package com.example.musicplayerdome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.MyMusicSearchBean;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.component.SlideRightViewDragHelper;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.ImgHelper;
import com.example.musicplayerdome.utils.Strings;
import com.lauzy.freedom.library.Lrc;
import com.lauzy.freedom.library.LrcHelper;
import com.lauzy.freedom.library.LrcView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LockActivity extends AppCompatActivity {
    private ImageView btn_pre,btn_next,btn_play,btn_like;
    View mainLayout;
    TextView dateTv,timeTv,dayTv,nameTv,singerTv;
    Music music;
    LrcView lockLrc;
    private static final int UPDATE_UI = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setShowWhenLocked(true);//亮屏时显示
//        setTurnScreenOn(true);//息屏时显示
        setContentView(R.layout.activity_lock);
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        SlideRightViewDragHelper dragHelper;
        dragHelper = (SlideRightViewDragHelper) findViewById(R.id.lock_view);
        dragHelper.setOnReleasedListener(new SlideRightViewDragHelper.OnReleasedListener() {
            @Override
            public void onReleased() {
                //TODO
                finish();
            }
        });
        initView();
        initData();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.MAIN_UPDATE_UI);
        intentFilter.addAction(MusicService.ACTION);
        registerReceiver(receiver, intentFilter);
    }
    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    updateTime();
                    break;
            }
        }
    };

    private void initView(){
        dateTv=findViewById(R.id.date_text);
        timeTv=findViewById(R.id.time_text);
        dayTv=findViewById(R.id.day_text);
        nameTv=findViewById(R.id.music_name);
        singerTv=findViewById(R.id.singer_name);
        btn_pre=findViewById(R.id.lock_last);
        btn_next=findViewById(R.id.lock_next);
        btn_play=findViewById(R.id.lock_play);
        btn_like=findViewById(R.id.lock_like);
        lockLrc=findViewById(R.id.lock_lrc);
        mainLayout=findViewById(R.id.main_view);
    }
    private void initData(){
        lockLrc.setEmptyContent("暂无歌词");
        updateUI();
        updateTime();
        initLrc();
        lockLrc.setOnPlayIndicatorLineListener(new LrcView.OnPlayIndicatorLineListener() {
            @Override
            public void onPlay(long time, String content) {
                HomeActivity.musicControl.seekTo((int)time);
            }
        });
    }
    @Override
    public void onBackPressed() {
    }
    private String[] getFormatTime(){
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);//获取年份
        int month=ca.get(Calendar.MONTH);//获取月份
        int day=ca.get(Calendar.DATE);//获取日
        int minute=ca.get(Calendar.MINUTE);//分
        int hour=ca.get(Calendar.HOUR_OF_DAY);//小时
        int second=ca.get(Calendar.SECOND);//秒
        //一周第一天是否为星期天
        boolean isFirstSunday = (ca.getFirstDayOfWeek() == Calendar.SUNDAY);
        //获取周几
        int weekDay = ca.get(Calendar.DAY_OF_WEEK);
        //若一周第一天为星期天，则-1
        if(isFirstSunday) {
            weekDay = weekDay - 1;
            if (weekDay == 0) {
                weekDay = 7;
            }
        }
        String[] formatTimeStrings=new String[3];
        if (minute<10){
            formatTimeStrings[0]=hour+":0"+minute;//时间:HH:mm
        }else {
            formatTimeStrings[0]=hour+":"+minute;//时间:HH:mm
        }
        formatTimeStrings[1]=month+"月"+day+"日";
        switch (weekDay){
            case 1:
                formatTimeStrings[2]="星期一";
                break;
            case 2:
                formatTimeStrings[2]="星期二";
                break;
            case 3:
                formatTimeStrings[2]="星期三";
                break;
            case 4:
                formatTimeStrings[2]="星期四";
                break;
            case 5:
                formatTimeStrings[2]="星期五";
                break;
            case 6:
                formatTimeStrings[2]="星期六";
                break;
            case 7:
                formatTimeStrings[2]="星期日";
                break;
        }
        return formatTimeStrings;
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MusicService.ACTION.equals(action)) {
                int widget_action = intent.getIntExtra(MusicService.KEY_USR_ACTION, -1);
                if (widget_action==MusicService.ACTION_LIKE){
                    updateLike();
                }
            }else {
                if (MusicService.listMusic==null||MusicService.listMusic.size()==0){
                    finish();
                }
                updatePlayText();
                updateUI();
                initLrc();
            }
        }
    };
    MyMusicSearchBean.SongsBean song=null;
    private void getYunSongMsgByNS(String name, String singer,long songId) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList< MyMusicSearchBean.SongsBean> songs= HttpRequest.getYunFormatSearchSimple(name,singer,1,0);
                if (songs!=null&&songs.size()>0){
                    song=songs.get(0);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (song!=null){
//                  musicControl.getMusic().setAlbumImg(song.getAl().getPicUrl()); //设置封面图url
                    String id= String.valueOf(song.getId());    //获取歌曲id用于获取歌词信息
                    if (songId==HomeActivity.musicControl.getMusic().getId()&&name.equals(song.getName())){
                        getYunLrc(id,name,singer,songId);
                    }
                }
            }
        }.execute();
    }

    /**
     * 网易云获取歌词信息
     */
    public void  getYunLrc(String id,String name,String singer,long songId){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                HttpRequest.getYunSongLrc(id,name,singer,songId,true);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (MusicService.nowLrcs !=null&&songId==HomeActivity.musicControl.getMusic().getId()){
                    lockLrc.setLrcData(MusicService.nowLrcs);
                }
            }
        }.execute();
    }
    private void initLrc(){
        String fileName=music.getName()+"-"+music.getSinger()+".lrc";
        String transFileName=music.getName()+"-"+music.getSinger()+"-trans.lrc";
        Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(fileName);
        fileName= matcher.replaceAll("");
        Matcher matcher1 = pattern.matcher(transFileName);
        transFileName=matcher1.replaceAll("");
        MusicService.nowLrcs= (ArrayList<Lrc>) LrcHelper.parseLrcFromFile(new File(DetailsActivity.LrcPath+"/"+fileName));
        MusicService.nowTransLrcs=null;
        MusicService.nowTransLrcs= (ArrayList<Lrc>) LrcHelper.parseLrcFromFile(new File(DetailsActivity.LrcPath+"/"+transFileName));
        if (MusicService.nowLrcs!=null){
            lockLrc.setLrcData(MusicService.nowLrcs);
        }else {
            if (music.getMId()==-1) {
                getYunSongMsgByNS(music.getName(),music.getSinger(),music.getId());
            } else {
                getYunLrc(String.valueOf(music.getMId()),music.getName(),music.getSinger(),music.getId());
                MusicDaoImpl.getMusicDao().update(music);
            }
        }
    }
    private void updateUI(){
        music=HomeActivity.musicControl.getMusic();
        nameTv.setText(music.getName());
        singerTv.setText(music.getSinger());
        String picUrl=HomeActivity.musicControl.getMusic().getAlbumImg();
        if (picUrl.contains("http")){
            picUrl=MusicService.proxy.getProxyUrl(picUrl);
        }
        Glide.with(this)
                .load(picUrl)
                .into(new SimpleTarget<Drawable>() {
                    @Override public void onResourceReady(
                            @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap srcBitmap= ImgHelper.drawableToBitmap(resource);
                        int imageWidth = srcBitmap.getWidth();
                        int imageHeight = srcBitmap.getHeight();
                        int top=0;
                        int left=0;
                        WindowManager windowManager = (WindowManager) LockActivity.this.getSystemService(Context.WINDOW_SERVICE);
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                        int screenWidth=displayMetrics.widthPixels;
                        int screenHeight = displayMetrics.heightPixels;
                        if ((screenWidth/screenHeight)>(imageWidth/imageHeight)){   //纵向裁
                            int newHeight=imageWidth*screenHeight/screenWidth;
                            top=(imageHeight-newHeight)/2;
                            if (top>0){
                                top=0-top;
                            }
                            imageHeight=newHeight;
                        }else { //横向裁
                            int newWidth=imageHeight*screenWidth/screenHeight;
                            left=(imageWidth-newWidth)/2;
                            if (left>0){
                                left=0-left;
                            }
                            imageWidth=newWidth;
                        }
                        resizeBmp = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.RGB_565);
                        Canvas canvas = new Canvas(resizeBmp);
                        canvas.drawBitmap(srcBitmap,left,top, null);
                        srcBitmap.recycle();
                        mainLayout.setBackground(ImgHelper.getDrawbleFormBitmap(LockActivity.this,resizeBmp));
                    }
                });
        updateLike();
    }
    Bitmap resizeBmp;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (resizeBmp!=null&&!resizeBmp.isRecycled()){
            resizeBmp.recycle();
        }
    }

    //Update button text
    public void updatePlayText() {
        if(MusicService.mlastPlayer!=null &&MusicService.mlastPlayer.isPlaying()){
            btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pause));
        }else{
            btn_play.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.play));
        }
    }
    private void updateTime(){
        String[] formatTimeStrings=getFormatTime();
        timeTv.setText(formatTimeStrings[0]);
        dateTv.setText(formatTimeStrings[1]);
        dayTv.setText(formatTimeStrings[2]);
        if (MusicService.nowLrcs!=null){
            long currenPostion = HomeActivity.musicControl.getCurrenPostion();
            lockLrc.updateTime(currenPostion);
        }
        handler.sendEmptyMessageDelayed(UPDATE_UI, 500);
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

    public void doClick(View view) {
        switch (view.getId()){
            case R.id.lock_last:
                pre();
                break;
            case R.id.lock_play:
                play();
                break;
            case R.id.lock_next:
                next();
                break;
            case R.id.lock_like:
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
        }
    }
    private void play() {
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_PLAY_PAUSE);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private void next() {
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_NEXT);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private void pre() {
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_PRE);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
}
