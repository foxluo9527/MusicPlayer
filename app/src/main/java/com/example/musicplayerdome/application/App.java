package com.example.musicplayerdome.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.musicplayerdome.bean.DaoMaster;
import com.example.musicplayerdome.bean.DaoSession;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.utils.SharedPreferencesUtil;
import com.mob.MobSDK;
import com.mob.OperationCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yalantis.ucrop.UCropFragment.TAG;

public class App extends MultiDexApplication {
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public static App instances;    //单例
    private static SharedPreferencesUtil shared;
    @Override
    public void onCreate() {
        super.onCreate();
        submitPrivacyGrantResult(true);
        instances = this;
        setDatabase();
        SharedPreferencesUtil.init(this,"Music", Context.MODE_PRIVATE);
        shared=SharedPreferencesUtil.getInstance();
    }

    public static SharedPreferencesUtil getShared() {
        return shared;
    }

    public static App getInstances(){
        return instances;
    }
    private void submitPrivacyGrantResult(boolean granted) {
        MobSDK.submitPolicyGrantResult(granted, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void data) {
                Log.d(TAG, "隐私协议授权结果提交：成功");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "隐私协议授权结果提交：失败");
            }
        });
    }
    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "music-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    public SQLiteDatabase getDb() {
        return db;
    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

    /**
     * 初始化设置
     * 添加本地,最近播放,下载,我的喜欢歌单
     */
    public static void initDefaultSet(){
        ArrayList<SongList> defaultSongLists=new ArrayList<SongList>();
        defaultSongLists.add(new SongList((long)1,"本地音乐","",new Date(),"本地音乐"));
        defaultSongLists.add(new SongList((long)2,"最近播放","",new Date(),"最近播放"));
        defaultSongLists.add(new SongList((long)3,"下载管理","",new Date(),"下载管理"));
        defaultSongLists.add(new SongList((long)4,"我的喜欢","",new Date(),"我的喜欢"));
        for (SongList songList:defaultSongLists) {
            MusicDaoImpl.createSongList(songList);
            songList.resetMusics();
        }
    }
}