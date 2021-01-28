package com.example.musicplayerdome.ui.base;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.activity.HomeActivity;
import com.example.musicplayerdome.ui.component.GrayFrameLayout;
import com.example.musicplayerdome.utils.ActivityManager;
import com.example.musicplayerdome.utils.NetBroadcastReceiver;
import com.example.musicplayerdome.utils.NetUtil;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetEvevt{
    private MaterialDialog mLoadingDialog;
    public static NetBroadcastReceiver.NetEvevt evevt;
    protected String lockAppName="com.example.musicplayerdome.";
    /**
     * 网络类型
     */
    private int netMobile;
    private allowMobileNetPlayReceiver receiver;
    public static String ASK_ALLOW_MOBILE_NET_PLAY_ACTION="com.example.musicplayer.mobilenetplayaction";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        setContentView(getResId());
        onConfigView();
        initData();
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        evevt = this;
        inspectNet();
        if (receiver==null){
            receiver = new allowMobileNetPlayReceiver(new Handler());
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(ASK_ALLOW_MOBILE_NET_PLAY_ACTION);
            registerReceiver(receiver, itFilter);
        }
        setLocalClassName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        boolean grayModel=App.getShared().getBoolean("grayModel",false);
        if(grayModel&&"FrameLayout".equals(name)){
            int count = attrs.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String attributeName = attrs.getAttributeName(i);
                String attributeValue = attrs.getAttributeValue(i);
                if (attributeName.equals("id")) {
                    int id = Integer.parseInt(attributeValue.substring(1));
                    String idVal = getResources().getResourceName(id);
                    if ("android:id/content".equals(idVal)) {
                        GrayFrameLayout grayFrameLayout = new GrayFrameLayout(context, attrs);
                        return grayFrameLayout;
                    }
                }
            }
        }
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
    }

    //记录用户首次点击返回键的时间
    private long firstTime=0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
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
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new MaterialDialog.Builder(this)
                    .widgetColorRes(R.color.black)
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
        }
        mLoadingDialog.setContent("玩命加载中...");
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 初始化时判断有没有网络
     */

    public boolean inspectNet() {
        this.netMobile = NetUtil.getNetWorkState(BaseActivity.this);
        return isNetConnect();

        // if (netMobile == 1) {
        // System.out.println("inspectNet：连接wifi");
        // } else if (netMobile == 0) {
        // System.out.println("inspectNet:连接移动数据");
        // } else if (netMobile == -1) {
        // System.out.println("inspectNet:当前没有网络");
        //
        // }
    }

    /**
     * 网络变化之后的类型
     */
    @Override
    public void onNetChange(int netMobile) {
        // TODO Auto-generated method stub
        this.netMobile = netMobile;
        isNetConnect();

    }

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netMobile == 1) {
            return true;
        } else if (netMobile == 0) {
            return true;
        } else if (netMobile == -1) {
            return false;
        }
        return false;
    }
    private class allowMobileNetPlayReceiver extends BroadcastReceiver {
        private final Handler handler;
        // Handler used to execute code on the UI thread
        public allowMobileNetPlayReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            handler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    if (!App.getShared().getBoolean("neverAskNetworkPlay",false)
                            &&MusicService.isAskingAllowMobileNetPlay
                            &&getTopApp(BaseActivity.this))
                        showAskAllowMobilePlay();
                }
            });
        }
    }
    private void showAskAllowMobilePlay(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View view=View.inflate(this, R.layout.allow_mobile_net_play_dialog, null);
        View sure,cancel,guide;
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);
        TextView issueText=view.findViewById(R.id.issue_text);
        CheckBox noMoreAgain=view.findViewById(R.id.no_more_again);
        issueText.setText("是否使用移动网络播放?");
        alertDialog.setView(view);
        AlertDialog dialog=alertDialog.create();
        if (!App.getShared().getBoolean("neverAskNetworkPlay",false)&&MusicService.isAskingAllowMobileNetPlay)
            dialog.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getShared().putBoolean("mobileNetworkPlay",true);
                dialog.dismiss();
                HomeActivity.musicControl.playPosition(MusicService.position);
                MusicService.isAskingAllowMobileNetPlay=false;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (noMoreAgain.isChecked()){
                    App.getShared().putBoolean("neverAskNetworkPlay",true);
                    Toast.makeText(BaseActivity.this,"将只播放缓存歌曲与本地歌曲，如需打开移动网络播放，请前往设置",Toast.LENGTH_LONG).show();
                }
                MusicService.isAskingAllowMobileNetPlay=false;
            }
        });
    }
    private boolean getTopApp(Context mContext) {
        String topActivityName = "";
        android.app.ActivityManager am = (android.app.ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningTaskInfo> runningTasks = am
                .getRunningTasks(1);
        if (runningTasks != null && !runningTasks.isEmpty()) {
            android.app.ActivityManager.RunningTaskInfo taskInfo = runningTasks.get(0);
            topActivityName = taskInfo.topActivity.getClassName();
        }
        if (lockAppName.equals(topActivityName)) {
            return true;
        }
        return false;
    }
    protected abstract int getResId();//加载布局
    protected abstract void onConfigView();//初始化View
    protected abstract void initData();//加载数据
    protected abstract void setLocalClassName();
}
