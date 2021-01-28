package com.example.musicplayerdome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.utils.DataCleanManager;
import com.example.musicplayerdome.utils.HttpRequest;
import com.example.musicplayerdome.utils.SharedPreferencesUtil;

public class SettingActivity extends BaseActivity implements Switch.OnCheckedChangeListener{
    Switch networkPlaySwitch,networkDownSwitch,simulcastSwitch,tableLrcSwitch,showLockSwitch,showBarSwitch,notificationSwitch;
    TextView cacheCapacityTv,cacheSizeTv,versionTv;
    private SharedPreferencesUtil share;
    boolean hadUpdate=false;
    @Override
    protected int getResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onConfigView() {
        cacheCapacityTv=findViewById(R.id.cache_capacity_text);
        versionTv=findViewById(R.id.version_text);
        cacheSizeTv=findViewById(R.id.cache_size_text);
        networkPlaySwitch=findViewById(R.id.mobile_network_play_switch);
        networkDownSwitch=findViewById(R.id.mobile_network_down_switch);
        simulcastSwitch=findViewById(R.id.simulcast_switch);
        tableLrcSwitch=findViewById(R.id.table_lrc_switch);
        showLockSwitch=findViewById(R.id.show_lock_switch);
        showBarSwitch=findViewById(R.id.show_bar_switch);
        notificationSwitch=findViewById(R.id.notification_switch);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        share=App.getShared();
        long size=share.getLong("cacheCapacity",0b10000000000000000000000000000000l);
        try {
            cacheCapacityTv.setText(DataCleanManager.getFormatSize(size));
            versionTv.setText("v"+getAppVersion(this.getPackageName(),SettingActivity.this));
            cacheSizeTv.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (share.getBoolean("mobileNetworkPlay",true)){
            networkPlaySwitch.setChecked(true);
        }else {
            networkPlaySwitch.setChecked(false);
        }
        if (share.getBoolean("mobileNetworkDown",true)){
            networkDownSwitch.setChecked(true);
        }else {
            networkDownSwitch.setChecked(false);
        }
        if (share.getBoolean("simulcastPlay",true)){
            simulcastSwitch.setChecked(true);
        }else {
            simulcastSwitch.setChecked(false);
        }
        tableLrcSwitch.setChecked(false);
        if (share.getBoolean("showLockLrc",true)){
            showLockSwitch.setChecked(true);
        }else {
            showLockSwitch.setChecked(false);
        }
        if (share.getBoolean("showBar",true)){
            showBarSwitch.setChecked(true);
        }else {
            showBarSwitch.setChecked(false);
        }
        if (share.getBoolean("allowNotification",true)){
            notificationSwitch.setChecked(true);
        }else {
            notificationSwitch.setChecked(false);
        }
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (HttpRequest.checkUpdate(getAppVersion(SettingActivity.this.getPackageName(),SettingActivity.this))!=null){
                        hadUpdate=true;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                if (hadUpdate){
                    findViewById(R.id.had_update_img).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.had_update_img).setVisibility(View.GONE);
                }
            }
        }.execute();
        initListener();
    }
    private void initListener(){
        networkPlaySwitch.setOnCheckedChangeListener(this::onCheckedChanged);
        networkDownSwitch.setOnCheckedChangeListener(this::onCheckedChanged);
        tableLrcSwitch.setOnCheckedChangeListener(this::onCheckedChanged);
        showLockSwitch.setOnCheckedChangeListener(this::onCheckedChanged);
        showBarSwitch.setOnCheckedChangeListener(this::onCheckedChanged);
        notificationSwitch.setOnCheckedChangeListener(this::onCheckedChanged);
        simulcastSwitch.setOnCheckedChangeListener(this::onCheckedChanged);
    }
    public void doClick(View view) {
        switch (view.getId()){
            case R.id.btn_return:
                finish();
                break;
            case R.id.mobile_network_play_view:
                networkPlaySwitch.setChecked(!networkPlaySwitch.isChecked());
                break;
            case R.id.mobile_network_down_view:
                networkDownSwitch.setChecked(!networkDownSwitch.isChecked());
                break;
            case R.id.table_lrc_view:
                tableLrcSwitch.setChecked(!tableLrcSwitch.isChecked());
                break;
            case R.id.simulcast_view:
                simulcastSwitch.setChecked(!simulcastSwitch.isChecked());
                break;
            case R.id.show_lock_View:
                showLockSwitch.setChecked(!showLockSwitch.isChecked());
                break;
            case R.id.show_bar_view:
                showBarSwitch.setChecked(!showBarSwitch.isChecked());
                break;
            case R.id.receipt_notification_view:
                notificationSwitch.setChecked(!notificationSwitch.isChecked());
                break;
            case R.id.cache_capacity_view:
                showSetCacheDialog();
                break;
            case R.id.clean_cache_view:
                showCleanDialog();
                break;
            case R.id.privacy_view:
                Intent intent= new Intent(SettingActivity.this,WebActivity.class);
                intent.putExtra("URLString","http://www.mob.com/about/policy");
                startActivity(intent);
                break;
            case R.id.about_view:
                AlertDialog.Builder dialog=new AlertDialog.Builder(SettingActivity.this);
                try {
                    dialog.setTitle("MusicPlayer")
                        .setIcon(R.drawable.music)
                        .setMessage("软件名:MusicPlayer(Foxluo音乐播放器)" +
                        "\n简介:基于网易云api开发的音乐播放器"+
                        "\n当前版本:v"+getAppVersion(this.getPackageName(),SettingActivity.this)+
                        "\n@powerBy:罗福林")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();//取消弹出框
                            }
                        })
                        .setNegativeButton("联系我", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (checkApkExist(SettingActivity.this, "com.tencent.mobileqq")){
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=1061297065&version=1")));
                                }else{
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:17628648573"));
                                    startActivity(intent);
//                                    Toast.makeText(SettingActivity.this,"本机未安装QQ应用",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .create().show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.version_view:
                try {
                    HomeActivity.checkUpdate(this,SettingActivity.getAppVersion(this.getPackageName(),this),true);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public static String getAppVersion(String packageName,Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        String appVersion = packageInfo.versionName;
        return appVersion;
    }
    private void showCleanDialog(){
        final AlertDialog show;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View view=View.inflate(this, R.layout.sure_del_list, null);
        View sure,cancel;
        TextView issueTv=view.findViewById(R.id.issue_text);
        issueTv.setText("确认清除缓存数据?\n其中包括:您缓存的音乐，封面图片等信息，清除后不可恢复");
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);
        alertDialog.setView(view);
        show=alertDialog.create();
        show.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCleanManager.clearAllCache(SettingActivity.this);
                try {
                    cacheSizeTv.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                show.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }
    private void showSetCacheDialog(){
        final AlertDialog show;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View view=View.inflate(this, R.layout.set_cache_size_dialog, null);
        View sure,cancel;
        EditText size=view.findViewById(R.id.cache_size);
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);
        alertDialog.setView(view);
        show=alertDialog.create();
        show.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (size.getText().toString().isEmpty()){
                    Toast.makeText(SettingActivity.this,"请设置缓存上限",Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(size.getText().toString())<100){
                    Toast.makeText(SettingActivity.this,"最小缓存上限不能低于100M",Toast.LENGTH_SHORT).show();
                }else {
                    share.putLong("cacheCapacity",Long.parseLong(size.getText().toString())*1024*1024);
                    cacheCapacityTv.setText(DataCleanManager.getFormatSize(share.getLong("cacheCapacity")));
                    show.dismiss();
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
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.mobile_network_play_switch:
                share.putBoolean("mobileNetworkPlay",isChecked);
                App.getShared().putBoolean("neverAskNetworkPlay",false);
                break;
            case R.id.mobile_network_down_switch:
                share.putBoolean("mobileNetworkDown",isChecked);
                App.getShared().putBoolean("neverAskNetworkDown",false);
                break;
            case R.id.simulcast_switch:
                share.putBoolean("simulcastPlay",isChecked);
                break;
            case R.id.table_lrc_switch:
                Toast.makeText(SettingActivity.this,"暂不支持桌面歌词",Toast.LENGTH_SHORT).show();
                tableLrcSwitch.setChecked(false);
                break;
            case R.id.show_lock_switch:
                if (!HomeActivity.checkShowLockPermission(this)||!HomeActivity.canShowLockView(this)){
                    HomeActivity.requestLockPermission(this);
                    if (showLockSwitch.isChecked()){
                        showLockSwitch.setChecked(false);
                    }
                }else {
                    share.putBoolean("showLockLrc",isChecked);
                }
                break;
            case R.id.show_bar_switch:
                if (!isChecked&&MusicService.isNotifying){
                    Intent intent=new Intent(MusicService.ACTION);
                    intent.putExtra(MusicService.KEY_USR_ACTION,MusicService.ACTION_DEL);
                    intent.putExtra("stopPlay",false);
                    sendBroadcast(intent);
                }else if (isChecked&&MusicService.notification!=null){
                    Intent intent=new Intent(MusicService.ACTION);
                    intent.putExtra(MusicService.KEY_USR_ACTION,MusicService.ACTION_UPDATE);
                    sendBroadcast(intent);
                }
                share.putBoolean("showBar",isChecked);
                break;
            case R.id.notification_switch:
                share.putBoolean("allowNotification",isChecked);
                break;
        }
    }
}
