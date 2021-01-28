package com.example.musicplayerdome.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.PlayListAdapter;
import com.example.musicplayerdome.adapter.TabPageAdapter;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.AppVersionBean;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicDownloadService;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.service.UpdateService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.component.CircleImageView;
import com.example.musicplayerdome.ui.fragment.FragmentDynamic;
import com.example.musicplayerdome.ui.fragment.FragmentFriend;
import com.example.musicplayerdome.ui.fragment.FragmentMine;
import com.example.musicplayerdome.ui.fragment.FragmentRecommend;
import com.example.musicplayerdome.ui.fragment.FramentMusicbar;
import com.example.musicplayerdome.utils.ActivityManager;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.MusicList;
import com.example.musicplayerdome.utils.SharedPreferencesUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.mob.MobSDK;
import com.mob.OperationCallback;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.musicplayerdome.service.MusicService.VAL_UPDATE_UI_PLAY;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.changeRuleImg;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.setRuleText;

public class HomeActivity extends BaseActivity {
    private String TAG = "HomeActivity";
    private MyConnection conn;
    private MyDownloadConnection downloadConn;
    private MyUpdateConnection updateConn;
    public static MusicService.MyMusicBinder musicControl;
    public static MusicDownloadService.MyDownloadBinder downloadControl;
    public static UpdateService.MyUpdateBinder updateBinder;
    private DrawerLayout mDrawerLayout;
    FragmentMine fragmentMine;
    FragmentRecommend fragmentRecommend;
    FragmentFriend fragmentFriend;
    FragmentDynamic fragmentDynamic;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<Fragment> fragmentList;
    public static ArrayList<SongList> songLists;
    public static int nowListIndex;//当前播放歌单索引
    public static int nowPlayIndex;//当前播放歌曲位置
    public static Long nowMusicProgress;
    public static SongList localSongList,recordSongList,downSongList,likeSongList;
    public static SharedPreferencesUtil shared;
    FramentMusicbar musicBar;
    private FragmentManager manager;
    TextView countDownTv;
    public static int countDownMills=-1;
    @Override
    protected int getResId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onConfigView() {
        mDrawerLayout =findViewById(R.id.drawerLayout);
        viewPager=findViewById(R.id.main_view);
        tabLayout=findViewById(R.id.home_tab_view);
        countDownTv=findViewById(R.id.count_down_time);
        findViewById(R.id.count_down_view).setOnClickListener(this::doClick);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        SharedPreferencesUtil.init(this,"Music", Context.MODE_PRIVATE);
        shared=SharedPreferencesUtil.getInstance();
        bindService();
        if (shared.getBoolean("firstUse",true)){  //若为第一次使用，初始新建默认歌单信息
            App.initDefaultSet();
            shared.putBoolean("firstUse",false);
        }else { //获取上次播放状态:播放列表,播放歌曲索引,播放位置
            nowMusicProgress=shared.getLong("progress",0);
            nowListIndex=shared.getInt("listIndex",-1);
            nowPlayIndex=shared.getInt("playIndex",-1);
        }
        initFragment();
//        String[] mTabNames=new String[]{"我的","推荐","好友","动态"};
        String[] mTabNames=new String[]{"我的","推荐"};
        TabPageAdapter pageAdapter = new TabPageAdapter(getSupportFragmentManager(), fragmentList,mTabNames);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        requestPermission();
        if (nowPlayIndex!=-1){
            ArrayList<Music> musics=new ArrayList<Music>();
            ArrayList<PlayMusicBean> playMusics= (ArrayList<PlayMusicBean>) MusicDaoImpl.getPlayList();
            for (PlayMusicBean m:
                    playMusics) {
                musics.add(new Music(m.getId(),m.getSId(),m.getMId(),m.getTitle(),m.getSinger(),m.getAlbum(),m.getUrl()
                        ,m.getSize(),m.getTime(),m.getName(),m.getAlbumImg(),m.getProgress()));
            }
            MusicService.initPlayMsg(nowPlayIndex,musics);
        }
        musicBar=new FramentMusicbar();
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.music_bar,musicBar);
        transaction.show(musicBar).commitAllowingStateLoss();
        try {
            checkUpdate(this,SettingActivity.getAppVersion(this.getPackageName(),this),false);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void bindService(){
        if (downloadControl==null){
            Intent downloadIntent = new Intent(this, MusicDownloadService.class);
            downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            downloadConn = new MyDownloadConnection();
            startService(downloadIntent);
            bindService(downloadIntent, downloadConn, BIND_AUTO_CREATE);
        }
        if (musicControl==null){
            Intent intent = new Intent(this, MusicService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            conn = new MyConnection();
            startService(intent);
            bindService(intent, conn, BIND_AUTO_CREATE);
        }
        if (updateBinder==null){
            Intent updateIntent=new Intent(this,UpdateService.class);
            updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            updateConn=new MyUpdateConnection();
            startService(updateIntent);
            bindService(updateIntent,updateConn,BIND_AUTO_CREATE);
        }
    }
    private class MyConnection implements ServiceConnection {
        //This method will be entered after the service is started.
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "::MusicConnection::onServiceConnected");
            //Get MyBinder in service
            HomeActivity.musicControl = (MusicService.MyMusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "::MusicConnection::onServiceDisconnected");
        }
    }
    private class MyDownloadConnection implements ServiceConnection {
        //This method will be entered after the service is started.
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "::DownloadConnection::onServiceConnected");
            //Get MyBinder in service
            downloadControl = (MusicDownloadService.MyDownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "::DownloadConnection::onServiceDisconnected");
        }
    }
    private class MyUpdateConnection implements ServiceConnection {
        //This method will be entered after the service is started.
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "::updateConnection::onServiceConnected");
            //Get MyBinder in service
            updateBinder = (UpdateService.MyUpdateBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "::updateConnection::onServiceDisconnected");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
        if (musicBar==null){
            musicBar=new FramentMusicbar();
            manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.music_bar,musicBar);
            transaction.show(musicBar).commitAllowingStateLoss();
        }
    }
    Handler updateCountDownHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (countDownMills>0)
                countDownTv.setText(toTime(countDownMills));
            else if (countDownMills<0){
                isOnCountDown=false;
                countDownTv.setText("");
            }else {
                ActivityManager.finishAllActivity();
                System.exit(0);
            }
        }
    };
    boolean isOnCountDown=false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void doClick(View view) {
        switch (view.getId()){
            case R.id.show_drawer:
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.sheet_down:
                System.exit(0);
                break;
            case R.id.search_view:
                startActivity(new Intent(HomeActivity.this,SearchActivity.class));
                break;
            case R.id.count_down_view:
                showChoiceCountDownTimePopup();
                break;
            case R.id.style_choice_view:
                showSetStyleDialog();
                break;
            case R.id.setting:
                startActivity(new Intent(HomeActivity.this,SettingActivity.class));
                break;
        }
    }
    private void initFragment(){
        fragmentList=new ArrayList<Fragment>();
        fragmentMine=new FragmentMine();
        fragmentRecommend=new FragmentRecommend();
        fragmentFriend=new FragmentFriend();
        fragmentDynamic=new FragmentDynamic();
        fragmentList.add(fragmentMine);
        fragmentList.add(fragmentRecommend);
//        fragmentList.add(fragmentFriend);
//        fragmentList.add(fragmentDynamic);
    }
    private void setCountDown(int countDown){
        if (isOnCountDown){
            countDownMills=countDown;
        }else {
            isOnCountDown=true;
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    countDownMills=countDown;
                    while (countDownMills>0){
                        countDownMills--;
                        updateCountDownHandler.sendEmptyMessage(0);
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }
    private void showChoiceCountDownTimePopup(){
        final View popView = View.inflate(HomeActivity.this,R.layout.count_down_exit_popup,null);
        //获取屏幕宽高
        int weight = HomeActivity.this.getResources().getDisplayMetrics().widthPixels*4/5;
        int height=WebActivity.dip2px(470,HomeActivity.this);

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
                WindowManager.LayoutParams lp =HomeActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                HomeActivity.this.getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = HomeActivity.this.getWindow().getAttributes();
        lp.alpha = 0.5f;
        HomeActivity.this.getWindow().setAttributes(lp);
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.close_m:
                        countDownMills=-1;
                        updateCountDownHandler.sendEmptyMessage(0);
                        break;
                    case R.id.ten_m:
                        setCountDown(10*60);
                        break;
                    case R.id.twenty_m:
                        setCountDown(20*60);
                        break;
                    case R.id.thirty_m:
                        setCountDown(30*60);
                        break;
                    case R.id.fourty_m:
                        setCountDown(40*60);
                        break;
                    case R.id.fivety_m:
                        setCountDown(50*60);
                        break;
                    case R.id.sixty_m:
                        setCountDown(60*60);
                        break;
                    case R.id.any_m:
                        showSetCountDialog();
                        break;
                }
                popupWindow.dismiss();
            }
        };
        popView.findViewById(R.id.close_m).setOnClickListener(listener);
        popView.findViewById(R.id.ten_m).setOnClickListener(listener);
        popView.findViewById(R.id.twenty_m).setOnClickListener(listener);
        popView.findViewById(R.id.thirty_m).setOnClickListener(listener);
        popView.findViewById(R.id.fourty_m).setOnClickListener(listener);
        popView.findViewById(R.id.fivety_m).setOnClickListener(listener);
        popView.findViewById(R.id.sixty_m).setOnClickListener(listener);
        popView.findViewById(R.id.any_m).setOnClickListener(listener);

        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,(HomeActivity.this.getResources().getDisplayMetrics().heightPixels-height)/2);
    }
    private void showSetStyleDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        View view=View.inflate(this, R.layout.style_choice_dialog, null);
        RadioButton color,gray;
        color=view.findViewById(R.id.color_model);
        gray=view.findViewById(R.id.gray_model);
        boolean grayModel=App.getShared().getBoolean("grayModel",false);
        if (grayModel){
            gray.setChecked(true);
        }else {
            color.setChecked(true);
        }
        View sure,cancel;
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);
        alertDialog.setView(view);
        AlertDialog show=alertDialog.create();
        show.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                show.dismiss();
                if (color.isChecked()){
                    shared.putBoolean("grayModel",false);
                }else {
                    shared.putBoolean("grayModel",true);
                }
                if ((grayModel&&color.isChecked())||(!grayModel&&gray.isChecked())){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = getPackageManager()
                                    .getLaunchIntentForPackage(getApplication().getPackageName());
                            PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                            AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
                            System.exit(0);
                        }
                    }, 100);// 1秒钟后重启应用
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
    private void showSetCountDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        View view=View.inflate(this, R.layout.set_count_down_popup, null);
        View sure,cancel;
        TimePicker timePicker=view.findViewById(R.id.time_picker);
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);
        alertDialog.setView(view);
        AlertDialog show=alertDialog.create();
        show.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                show.dismiss();
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                int second=cal.get(Calendar.SECOND);
                int nowTime=hour*60*60+minute*60+second;
                int choiceTime=timePicker.getHour()*60*60+timePicker.getMinute()*60;
                if (choiceTime>=nowTime){    //选择今天往后的时间
                    setCountDown(choiceTime-nowTime);
                }else {     //选择第二天的时间
                    setCountDown(24*60*60-nowTime+choiceTime);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownMills=-1;
                updateCountDownHandler.sendEmptyMessage(0);
                show.dismiss();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn!=null){
            unbindService(conn);
        }
        if (updateConn!=null){
            unbindService(updateConn);
        }
        if (downloadConn!=null){
            unbindService(downloadConn);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void requestPermission(){
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED&&
//                !checkFloatPermission(this)) { // Android6.0及以后需要动态申请权限
//            boolean noMoreAgain=shared.getBoolean("notRequestFloatPermission",false);
//            if (floatShow==null&&!noMoreAgain)
//                requestFloatPermission(HomeActivity.this);
//        }

        if (!checkShowLockPermission(this)||!canShowLockView(this)){
            boolean noMoreAgain=shared.getBoolean("notRequestLockPermission",false);
            if (lockShow==null&&!noMoreAgain)
                requestLockPermission(this);
        }

        if (!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),1);
        }
    }
    public static AlertDialog lockShow=null;
    AlertDialog floatShow=null;
    public static void requestLockPermission(Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        View view=View.inflate(context, R.layout.request_permission_dialog, null);
        View sure,cancel,guide;
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);
        guide=view.findViewById(R.id.guide);
        TextView issueText=view.findViewById(R.id.issue_text);
        CheckBox noMoreAgain=view.findViewById(R.id.no_more_again);
        issueText.setText("请允许程序获取以下权限" +
                "\n1.后台弹出界面\n2.锁屏显示");
        alertDialog.setView(view);
        lockShow=alertDialog.create();
        lockShow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                lockShow=null;
            }
        });
        lockShow.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.fromParts("package",context.getPackageName(), null));
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    ((Activity)context).startActivity(intent);
                }
                lockShow.dismiss();
                return;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockShow.dismiss();
                if (noMoreAgain.isChecked()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    View view=View.inflate(context, R.layout.sure_del_list, null);
                    View sure,cancel;
                    sure=view.findViewById(R.id.sure);
                    cancel=view.findViewById(R.id.cancel);
                    TextView issueText=view.findViewById(R.id.issue_text);
                    CheckBox noMoreAgain=view.findViewById(R.id.no_more_again);
                    issueText.setText("确认不开启锁屏显示权限?\n这将使锁屏歌词显示异常");
                    alertDialog.setView(view);
                    AlertDialog showSureAgain=alertDialog.create();
                    showSureAgain.show();
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shared.putBoolean("notRequestLockPermission",true);
                            showSureAgain.dismiss();
                            return;
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSureAgain.dismiss();
                        }
                    });
                }
            }
        });
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockShow.dismiss();
                Intent intent=new Intent(context,GuidePermissionActivity.class);
                intent.putExtra("GUIDE_MODEL",GuidePermissionActivity.GUIDE_LOCK_PERMISSION);
                ((Activity)context).startActivity(intent);
                return;
            }
        });
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }

    private void requestFloatPermission(Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        View view=View.inflate(this, R.layout.request_permission_dialog, null);
        View sure,cancel,guide;
        CheckBox noMoreAgain=view.findViewById(R.id.no_more_again);
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);
        guide=view.findViewById(R.id.guide);
        TextView issueText=view.findViewById(R.id.issue_text);
        issueText.setText("请允许程序在其他应用上层显示");
        alertDialog.setView(view);
        floatShow=alertDialog.create();
        floatShow.show();
        floatShow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                floatShow=null;
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatShow.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1010);
                return;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatShow.dismiss();
                if (noMoreAgain.isChecked()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                    View view=View.inflate(HomeActivity.this, R.layout.sure_del_list, null);
                    View sure,cancel;
                    sure=view.findViewById(R.id.sure);
                    cancel=view.findViewById(R.id.cancel);
                    TextView issueText=view.findViewById(R.id.issue_text);
                    CheckBox noMoreAgain=view.findViewById(R.id.no_more_again);
                    issueText.setText("确认不开启其他应用上层显示权限?");
                    alertDialog.setView(view);
                    AlertDialog showSureAgain=alertDialog.create();
                    showSureAgain.show();
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shared.putBoolean("notRequestFloatPermission",true);
                            showSureAgain.dismiss();
                            return;
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSureAgain.dismiss();
                        }
                    });
                }
            }
        });
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatShow.dismiss();
                Intent intent=new Intent(HomeActivity.this,GuidePermissionActivity.class);
                intent.putExtra("GUIDE_MODEL",GuidePermissionActivity.GUIDE_FLOAT_PERMISSION);
                startActivity(intent);
                return;
            }
        });
    }


    public static boolean canShowLockView(Context context) {
        AppOpsManager ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            int op = 10020; // >= 23
// ops.checkOpNoThrow(op, uid, packageName)
            Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]
                    {int.class, int.class, String.class}
            );
            Integer result = (Integer) method.invoke(ops, op, Binder.getCallingUid(), context.getPackageName());
            return result == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean checkShowLockPermission(Context context) {
        AppOpsManager ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            int op = 10021;
            Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]{int.class, int.class, String.class});
            Integer result = (Integer) method.invoke(ops, op, Binder.getCallingUid(),context.getPackageName());
            return result == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            Log.e("musicPlayer", "not support");
        }
        return false;
    }
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", Binder.getCallingUid(), context
                        .getPackageName());
                return Settings.canDrawOverlays(context) || mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }
    static AppVersionBean versionBean=null;
    public static void checkUpdate(Context context,String version,boolean showToast) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                versionBean = HttpRequest.checkUpdate(version);
                return null;
            }
//SettingActivity.getAppVersion(context.getPackageName(),context)
            @Override
            protected void onPostExecute(Void aVoid) {
                if (versionBean != null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("检测到新版本")
                            .setIcon(R.drawable.music)
                            .setMessage("\n最新版本:v" + versionBean.getVersion()+
                                    "\n新版本信息:" +versionBean.getInfo()+
                                    "\n是否更新?")
                            .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();//取消弹出框
                                    if (!updateBinder.checkOnDownloading()) {
                                        updateBinder.startUpdate(versionBean.getUrl());
                                        Toast.makeText(context,"更新下载开始",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(context,"更新正在下载中!",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();//取消弹出框
                                    if (versionBean.getForce() == 1) {
                                        System.exit(0);
                                    }
                                }
                            })
                            .create().show();
                }else if (showToast) {
                    Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    //总秒数转小时:分:秒
    public String toTime(int time) {
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d:%02d",hour,minute, second);
    }
}
