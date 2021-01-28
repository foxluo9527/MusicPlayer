package com.example.musicplayerdome.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.ui.activity.DetailsActivity;
import com.example.musicplayerdome.ui.activity.HomeActivity;
import com.example.musicplayerdome.ui.activity.LockActivity;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.widget.JalMusicWidget;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.ImgHelper;
import com.example.musicplayerdome.utils.NetBroadcastReceiver;
import com.example.musicplayerdome.utils.SharedPreferencesUtil;
import com.example.musicplayerdome.utils.Strings;
import com.lauzy.freedom.library.Lrc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.security.auth.callback.Callback;

public class MusicService extends Service implements NetBroadcastReceiver.NetEvevt, Callback {
    public static MediaPlayer mlastPlayer;
    public static int mPosition=-1;      //在播放歌曲列表中的位置
    public static int position;             //在播放歌曲列表中的位置
    private static int nowPosition;          //在播放顺序列表中的位置
    private String path = "";
    private String TAG = "MusicServiceLog";
    private MediaPlayer player;
    private static Music music;
    public static ArrayList<Music> listMusic;
    public static ArrayList<Integer> playList;//播放歌曲顺序的列表
    private Context context;
    private RemoteViews remoteView,remoteSmallView;
    public static Notification notification;
    public static HttpProxyCacheServer proxy = null;
    private String notificationChannelID = "1";
    public static String ACTION = "to_service";
    public static String KEY_USR_ACTION = "key_usr_action";
    public static final int ACTION_PRE = 0, ACTION_PLAY_PAUSE = 1, ACTION_NEXT = 2,ACTION_LIKE=3,ACTION_DEL=4,ACTION_UPDATE=5;
    public static String MAIN_UPDATE_UI = "main_activity_update_ui";  //Action
    public static String KEY_MAIN_ACTIVITY_UI_BTN = "main_activity_ui_btn_key";
    public static String KEY_MAIN_ACTIVITY_UI_TEXT = "main_activity_ui_text_key";
    public static final int  VAL_UPDATE_UI_PLAY = 1,VAL_UPDATE_UI_PAUSE =2;
    public static final int CIRCULATE_MODEL=3,CIRCULATE_ONE=4,RANDOM_MODEL=5;
    public static int NOW_PlAY_MODEL=CIRCULATE_MODEL;
    public static int notifyId = 1;
    public static boolean isNotifying=false;
    public static int CacheCurrent=0;   //歌曲缓冲率
    private static int lastNetModel=0;
    private static SharedPreferencesUtil shared;
    public static ArrayList<Lrc> nowLrcs;//当前播放的歌词信息
    public static ArrayList<Lrc> nowTransLrcs;//当前播放的翻译歌词信息
    public static ArrayList<Lrc> tempLrcs=new ArrayList<>();
    public static ArrayList<Lrc> tempTransLrcs=new ArrayList<>();
    public static boolean showTrans=false;
    public static NotificationManager manager;
    public static boolean isAskingAllowMobileNetPlay=false;
    @Override
    public IBinder onBind(Intent intent) {
        //When onCreate() is executed, onBind() will be executed to return the method of operating the music.
        return new MyMusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(receiver, intentFilter);
        if (proxy==null){
            proxy=getProxy();
        }
        initPlayModel();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == Intent.ACTION_SCREEN_OFF&&mlastPlayer!=null&&mlastPlayer.isPlaying()) {
                    System.out.println("收到锁屏广播");
                    if (!shared.getBoolean("showLockLrc",true)){
                        return;
                    }
                    Intent lockscreen = new Intent(MusicService.this, LockActivity.class);
                    lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lockscreen);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
    }
    private HttpProxyCacheServer getProxy() {
        shared=App.getShared();
        long size=shared.getLong("cacheCapacity",0b10000000000000000000000000000000l);
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(size)       // 2 Gb for cache
                .build();
    }
    private void initPlayModel(){
        NOW_PlAY_MODEL=shared.getInt(Strings.PLAY_MODE_KEY,CIRCULATE_MODEL);   //获取储存的播放模式，默认为循环播放
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    private void postState(Context context, int state, int songid) {
        Intent actionIntent = new Intent(MusicService.MAIN_UPDATE_UI);
        actionIntent.putExtra(MusicService.KEY_MAIN_ACTIVITY_UI_BTN,state);
        actionIntent.putExtra(MusicService.KEY_MAIN_ACTIVITY_UI_TEXT, songid);
        context.sendBroadcast(actionIntent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateNotification();
            }
        },300);
    }
    public static void initPlayMsg(int nowPosition,ArrayList<Music> musics){
        if (nowPosition>=musics.size()){
            nowPosition=0;
        }
        ArrayList<PlayMusicBean> playMusics=new ArrayList<>();
        for (Music m:
             musics) {
            playMusics.add(new PlayMusicBean(m.getId(),m.getSId(),m.getMId(),m.getTitle(),m.getSinger(),m.getAlbum(),m.getUrl()
                    ,m.getSize(),m.getTime(),m.getName(),m.getAlbumImg(),m.getProgress()));
        }
        MusicDaoImpl.addPlayMusicList(playMusics);
        mPosition=nowPosition;
        position = nowPosition;
        listMusic=new ArrayList<>();
        listMusic.addAll(musics);
        playList=new ArrayList<Integer>();
        for(int i=0;i<listMusic.size();i++){
            playList.add(i);
        }
        if (NOW_PlAY_MODEL==RANDOM_MODEL){       //随机模式，打乱歌曲播放顺序
            Collections.shuffle(playList);
        }
        int j;
        for (j=0;j<listMusic.size();j++){
            if (playList.get(j)==position){
                break;
            }
        }
        nowPosition=j;
        if (listMusic.size()==0){
            listMusic=null;
            music=null;
            mlastPlayer=null;
            mPosition=-1;
            return;
        }
        lastPosition=-1;
        music=listMusic.get(position);
    }
    private void initNotification(){
        manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notification channel";
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel mChannel = new NotificationChannel(notificationChannelID, name, importance);
            mChannel.setDescription(description);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            manager.createNotificationChannel(mChannel);
        }
        Intent intent = new Intent(this, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("continuePlay",true);
        intent.putExtras(bundle);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, 0);

        Intent actionIntent=new Intent(ACTION);
        remoteView=new RemoteViews(getPackageName(),R.layout.notification_view);
        actionIntent.putExtra(KEY_USR_ACTION,ACTION_PRE);
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.left,intent_prev);

        actionIntent.putExtra(KEY_USR_ACTION,ACTION_PLAY_PAUSE);
        PendingIntent intent_play = PendingIntent.getBroadcast(this, 2, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.play,intent_play);

        actionIntent.putExtra(KEY_USR_ACTION,ACTION_NEXT);
        PendingIntent intent_right = PendingIntent.getBroadcast(this, 3, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.right,intent_right);

        actionIntent.putExtra(KEY_USR_ACTION,ACTION_LIKE);
        PendingIntent intent_like = PendingIntent.getBroadcast(this, 4, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.like,intent_like);

        actionIntent.putExtra(KEY_USR_ACTION,ACTION_DEL);
        PendingIntent intent_del = PendingIntent.getBroadcast(this, 5, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.exit,intent_del);

        remoteSmallView=new RemoteViews(getPackageName(),R.layout.notification_small_view);
        remoteSmallView.setOnClickPendingIntent(R.id.play,intent_play);
        remoteSmallView.setOnClickPendingIntent(R.id.right,intent_right);
        remoteSmallView.setOnClickPendingIntent(R.id.exit,intent_del);

        notification =new NotificationCompat.Builder(context,notificationChannelID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.music)
                .setTicker("音乐播放器")
                .setOngoing(true)
                .setCustomBigContentView(remoteView)//设置显示bigView的notification视图
                .setContent(remoteSmallView)//设置普通notification视图
                .setPriority(NotificationCompat.PRIORITY_MAX)//设置最大优先级
                .setTimeoutAfter(0)
                .build();
        if (shared.getBoolean("showBar",true)){
            manager.notify(notifyId,notification);
            startForeground(notifyId, notification);
            updateNotification();
            isNotifying=true;
        }
    }

    static int lastPosition=-1;
    Bitmap srcBitmap;
    public void updateNotification() {
        if (!shared.getBoolean("showBar",true)){
            return;
        }
        if (manager==null){
            return;
        }
        isNotifying=true;
        if (notification==null||manager==null){
            return;
        }
        boolean isLike=false;
        for (Music m:
                HomeActivity.likeSongList.getMusics()) {
            if (m.getUrl().equals(music.getUrl())){
                isLike=true;
                break;
            }
        }
        if (isLike){
            remoteView.setImageViewResource(R.id.like,R.drawable.like_c);
        }else {
            remoteView.setImageViewResource(R.id.like,R.drawable.like);
        }
        if(MusicService.mlastPlayer!=null &&MusicService.mlastPlayer.isPlaying()){
            remoteSmallView.setImageViewResource(R.id.play,R.drawable.pause);
            remoteView.setImageViewResource(R.id.play,R.drawable.pause);
        }else{
            remoteSmallView.setImageViewResource(R.id.play,R.drawable.play);
            remoteView.setImageViewResource(R.id.play,R.drawable.play);
        }
        if (lastPosition!=position||lastPosition==-1){//新的一曲
            String title = listMusic.get(MusicService.mPosition).getName();
            Log.i(TAG, "updateNotification title = " + title);
            remoteSmallView.setTextViewText(R.id.textView_songName, title);
            remoteSmallView.setTextViewText(R.id.textView_songSinger, listMusic.get(MusicService.mPosition).getSinger());
            remoteView.setTextViewText(R.id.textView_songName, title);
            remoteView.setTextViewText(R.id.textView_songSinger, listMusic.get(MusicService.mPosition).getSinger());
            String picUrl=listMusic.get(MusicService.mPosition).getAlbumImg();
            if (picUrl.contains("http")){
                picUrl=proxy.getProxyUrl(picUrl);
            }
            Glide.with(context)
                    .load(picUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(100,100)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(
                                @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            srcBitmap = ImgHelper.drawableToBitmap(resource);
                            if (srcBitmap.getWidth()>srcBitmap.getHeight()){
                                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getHeight(), srcBitmap.getHeight());
                            }else{
                                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getWidth());
                            }
                            srcBitmap=ImgHelper.getRoundedCornerBitmap(srcBitmap,10f);
                            remoteView.setImageViewBitmap(R.id.cover_img,srcBitmap);
                            remoteSmallView.setImageViewBitmap(R.id.cover_img,srcBitmap);
                            notification.contentView = remoteSmallView;
                            notification.bigContentView=remoteView;
                            manager.notify(notifyId,notification);
                        }
                    });
        }else {//暂停继续
            notification.contentView = remoteSmallView;
            notification.bigContentView=remoteView;
            manager.notify(notifyId,notification);
        }
        lastPosition=position;
    }

    void prepare(){
        if (listMusic==null||listMusic.size()==0){
            return;
        }
        if (mlastPlayer==null||manager==null){
            initNotification();
            getFocus();
        }
        shared=SharedPreferencesUtil.getInstance();
        shared.putInt("playIndex",position);
        showTrans=false;
        CacheCurrent=0;
        music = listMusic.get(position);
        path = music.getUrl();
        String albumImgUri=music.getAlbumImg();
        if ((albumImgUri==null||albumImgUri.isEmpty())&&music.getMId()!=-1){
            new Thread(){
                @Override
                public void run() {
                    int id=Integer.parseInt(music.getMId().toString());
                    if (id<0){
                        id=0-id;
                    }
                    Music tempMusic= HttpRequest.getMusicDetail(id);
                    if (tempMusic!=null){
                        music.setAlbumImg(tempMusic.getAlbumImg());
                    }
                }
            }.start();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG,"path:"+path);
        player = new MediaPlayer();//This is only done once, used to prepare the player.
        if (mlastPlayer !=null){
            mlastPlayer.stop();
            mlastPlayer.release();
        }
        mlastPlayer = player;
        mPosition = position;
        HomeActivity.nowPlayIndex=position;
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.i(TAG,path);
            if(path.startsWith("http")){//加载网络资源，使用代理服务缓存策略
                if (!isNetworkConnected(context)){  //检查网络是否连接
                    if (!App.getProxy(context).isCached(path)){  //若未连接互联网，且未缓存此歌曲
                        Toast.makeText(context,"该歌曲为网络歌曲，请连接网络",Toast.LENGTH_SHORT).show();
                        next(1);
                        return;
                    }else {
                        Toast.makeText(context,"该歌曲已缓存，可播放已缓存的部分",Toast.LENGTH_SHORT).show();
                    }
                }else { //判断是否为移动网络
                    if (GetNetype(context)!=1&&!App.getProxy(context).isCached(path)){
                        if (!shared.getBoolean("mobileNetworkPlay",true)){
                            if(!shared.getBoolean("neverAskNetworkPlay",false)){
                                isAskingAllowMobileNetPlay=true;
                                Intent actionIntent = new Intent(BaseActivity.ASK_ALLOW_MOBILE_NET_PLAY_ACTION);
                                context.sendBroadcast(actionIntent);
                                player=null;
                                mlastPlayer=null;
                                postState(getApplicationContext(), VAL_UPDATE_UI_PAUSE,position);
                                return;
                            }else {
                                nowPosition+=1;
                                nowPosition = (nowPosition + listMusic.size())%listMusic.size();
                                position=playList.get(nowPosition);
                                music = listMusic.get(position);
                                prepare();
                                return;
                            }
                        }
                    }
                }
                path=proxy.getProxyUrl(path);
            }
            player.reset();
            player.setDataSource(path);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer iMediaPlayer) {
                    am.requestAudioFocus(afChangeListener,
                            AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN);
                    postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
                    MusicDaoImpl.addMusicToSongList(HomeActivity.recordSongList,music);
                    iMediaPlayer.start();
                    iMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer iMediaPlayer) {
                            if (NOW_PlAY_MODEL!=CIRCULATE_ONE){     //若非单曲循环，则按照播放顺序列表播放下一首/上一首
                                nowPosition+=1;
                                nowPosition = (nowPosition + listMusic.size())%listMusic.size();
                                position=playList.get(nowPosition);
                            }
                            music = listMusic.get(position);
                            Toast.makeText(context, "自动为您切换下一首:"+music.getName(), Toast.LENGTH_SHORT).show();
                            prepare();
                        }
                    });
                }
            });
            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer iMediaPlayer, int percent) {
                    CacheCurrent=percent;
                    System.out.println("缓冲==>"+percent+"%");
                }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (extra==MediaPlayer.MEDIA_ERROR_TIMED_OUT||(what==1&&extra==-2147483648)) {
                        Toast.makeText(context, "播放失败:连接超时,网络状态不好或者:(要会员/要付费/没版权)", Toast.LENGTH_LONG).show();
                        if (NOW_PlAY_MODEL!=CIRCULATE_ONE){     //若非单曲循环，则按照播放顺序列表播放下一首/上一首
                            nowPosition+=1;
                            nowPosition = (nowPosition + listMusic.size())%listMusic.size();
                            position=playList.get(nowPosition);
                        }
                        music = listMusic.get(position);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (listMusic.size()>1)
                            prepare();

                    }
                    return true;
                }
            });
            nowLrcs=null;
            Log.i(TAG, "Ready to play music");
            updateNotification();
        } catch (IOException e) {
            Log.i(TAG,"ERROR");
            e.printStackTrace();
        }
    }
    AudioManager am;
    AudioManager.OnAudioFocusChangeListener afChangeListener;
    private void getFocus(){
        //获取AudioManager对象
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //注册OnAudioFocusChangeListener监听
        afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    if (player.isPlaying()&&!shared.getBoolean("simulcastPlay",true)){
                        play();
                    }
                }
            }
        };
    }
    @Override
    public void onNetChange(int netMobile) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player!=null)
            player.reset();
        player=null;
        if (mlastPlayer!=null)
            mlastPlayer=null;
        unregisterReceiver(receiver);
        stopForeground(true);
    }

    //This method contains operations on music
    public class MyMusicBinder extends Binder {

        public boolean isPlaying(){
            if (player!=null)
            return player.isPlaying();
            else return false;
        }

        public void play() {
            if (listMusic==null||listMusic.size()==0){
                return;
            }
            if (player==null){
                prepare();
                return;
            }
            if (player.isPlaying()) {
                player.pause();
                am.abandonAudioFocus(afChangeListener);
                Log.i(TAG, "Play stop");
            } else {
                player.start();
                am.requestAudioFocus(afChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
                Log.i(TAG, "Play start");
            }
            updateNotification();
        }
        public void next(int type){
            if (listMusic==null||listMusic.size()==0){
                return;
            }
            if (NOW_PlAY_MODEL!=CIRCULATE_ONE){     //若非单曲循环，则按照播放顺序列表播放下一首/上一首
                nowPosition+=type;
                nowPosition = (nowPosition + listMusic.size())%listMusic.size();
                position=playList.get(nowPosition);
            }
            music = listMusic.get(position);
            prepare();
        }
        //播放指定位置歌曲
        public void playPosition(int position){
            if (listMusic==null||listMusic.size()==0){
                return;
            }
            MusicService.position=position;
            MusicService.mPosition=position;
            for(int i=0;i<listMusic.size();i++){
                if (playList.get(i)==position){
                    nowPosition=i;
                }
            }
            prepare();
        }
        //获取播放器播放模式
        public int getPlayModel(){
            return NOW_PlAY_MODEL;
        }

        //设置播放器播放模式
        public void setPlayModel(int playModel){
            NOW_PlAY_MODEL=playModel;
            shared.putInt(Strings.PLAY_MODE_KEY,NOW_PlAY_MODEL);//将设置好的播放模式存入shared内
            playList=new ArrayList<Integer>();
            for(int i=0;i<listMusic.size();i++){
                playList.add(i);
            }
            if (playModel==RANDOM_MODEL){       //随机模式，打乱歌曲播放顺序
                Collections.shuffle(playList);
            }else if (playModel==CIRCULATE_MODEL){
                nowPosition=position;
            }
        }

        public boolean getIsLocal(){
            return (!music.getUrl().contains("http"));
        }

        public int getPosition(){
            return mPosition;
        }
        //Returns the length of the music in milliseconds
        public long getDuration(){
            return player.getDuration();
        }

        //Returns the current progress of the music in milliseconds
        public long getCurrenPostion(){
            return player.getCurrentPosition();
        }
        public int getCacheCurrent(){
            double cacheLv=(double) CacheCurrent/100;
            int secondCurrent=(int)(getDuration()*cacheLv);
            return secondCurrent;
        }
        //Set the progress of music playback in milliseconds
        public void seekTo(int mesc){
            player.seekTo(mesc);
        }

        public Music getMusic(){ return music;}
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
            if (ACTION.equals(action)) {
                int widget_action = intent.getIntExtra(KEY_USR_ACTION, -1);
                switch (widget_action) {
                    case ACTION_PRE:
                        next(-1);
                        Log.d(TAG,"action_prev");
                        break;
                    case ACTION_PLAY_PAUSE:
                        play();
                        Log.d(TAG,"action_play");
                        break;
                    case ACTION_NEXT:
                        next(1);
                        Log.d(TAG,"action_next");
                        break;
                    case ACTION_DEL:
                        stopForeground(true);
                        manager.cancel(notifyId);
                        boolean stop=intent.getBooleanExtra("stopPlay",true);
                        if (stop) {
                            manager=null;
                            if (mlastPlayer!=null&&mlastPlayer.isPlaying()){
                                play();
                            }
                        }
                        isNotifying=false;
                        Log.d(TAG,"action_del");
                        break;
                    case ACTION_LIKE:
                        boolean isLike=false;
                        for (Music m:
                                HomeActivity.likeSongList.getMusics()) {
                            if (m.getUrl().equals(music.getUrl())){
                                MusicDaoImpl.deleteMusicFromSonglist( HomeActivity.likeSongList,m);
                                isLike=true;
                                break;
                            }
                        }
                        if (!isLike){
                            MusicDaoImpl.addMusicToSongList(HomeActivity.likeSongList,HomeActivity.musicControl.getMusic());
                            remoteView.setImageViewResource(R.id.like,R.drawable.like_c);
                        }else {
                            remoteView.setImageViewResource(R.id.like,R.drawable.like);
                        }
                        notification.contentView = remoteView;
                        notification.bigContentView=remoteView;
                        manager.notify(notifyId,notification);
                        postState(context,VAL_UPDATE_UI_PLAY,position);
                        Log.d(TAG,"action_like");
                        break;
                    case ACTION_UPDATE:
                        lastPosition=-1;
                        updateNotification();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    public void play() {
        if (listMusic==null||listMusic.size()==0){
            return;
        }
        if (player==null){
            prepare();
            return;
        }
        if (player.isPlaying()) {
            player.pause();
            am.abandonAudioFocus(afChangeListener);
            postState(getApplicationContext(), VAL_UPDATE_UI_PAUSE,position);
            Log.i(TAG, "Play stop");
        } else {
            player.start();
            am.requestAudioFocus(afChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
            Log.i(TAG, "Play start");
        }
        updateNotification();
    }

    //Play the next music
    public void next(int type){
        if (listMusic==null||listMusic.size()==0){
            stopSelf();
            return;
        }
        if (NOW_PlAY_MODEL!=CIRCULATE_ONE){     //若非单曲循环，则按照播放顺序列表播放下一首/上一首
            nowPosition+=type;
            nowPosition = (nowPosition + listMusic.size())%listMusic.size();
            position=playList.get(nowPosition);
        }
        music = listMusic.get(position);
        prepare();
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    //返回值 -1：没有网络  1：WIFI网络2：wap网络3：net网络
    public static int GetNetype(Context context)
    {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo==null)
        {
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType==ConnectivityManager.TYPE_MOBILE)
        {
            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet"))
            {
                netType = 3;
            }
            else
            {
                netType = 2;
            }
        }
        else if(nType==ConnectivityManager.TYPE_WIFI)
        {
            netType = 1;
        }
        return netType;
    }

}