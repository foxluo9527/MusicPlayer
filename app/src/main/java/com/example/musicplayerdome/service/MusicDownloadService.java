package com.example.musicplayerdome.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.DownTaskBean;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.ui.activity.DetailsActivity;
import com.example.musicplayerdome.ui.activity.DownloadActivity;
import com.example.musicplayerdome.ui.activity.HomeActivity;

import java.util.ArrayList;

public class MusicDownloadService extends Service {
    public static final String MusicDownloadPath= Environment.getExternalStorageDirectory()+"/MusicPlayer/music/";
    private static final int SUMMARY_ID = 10;
    public static ArrayList<DownTaskBean> downTaskBeans=new ArrayList<>();
    public static final String DOWNLOAD_DONE_ACTION="music_download_done";
    private static final String GROUP_KEY_WORK_EMAIL = "com.android.example.DOWNLOAD_DONE";
    public static int nowDowningPosition=-1;
    private String notificationChannelID = "10";
    private static int notifyId = 20;
    @Override
    public void onCreate() {
        super.onCreate();
        Aria.download(this).register();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyDownloadBinder();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    //This method contains operations on music
    public class MyDownloadBinder extends Binder {
        public void initTasks(ArrayList<DownTaskBean> taskBeans){
            downTaskBeans=taskBeans;
        }
        public void startAllTask(){
            int position=0;
            for (DownTaskBean taskBean:
                 downTaskBeans) {
                taskBean.setPause(false);
                if (taskBean.isDowning()){
                    continueTask(position);
                }
                position++;
            }
        }
        public void stopAllTask(){
            int position=0;
            for (DownTaskBean taskBean:
                    downTaskBeans) {
                taskBean.setPause(true);
                taskBean.setDowning(false);
                stopTask(position);
                position++;
            }
            nowDowningPosition=-1;
        }
        public void downTask(int position){
            if (MusicService.GetNetype(getApplicationContext())!=1&&!App.getShared().getBoolean("mobileNetworkDown",true)){    //移动网络下载判断
                Intent actionIntent = new Intent(DOWNLOAD_DONE_ACTION);
                actionIntent.putExtra("DOWNLOAD_ACTION_VAL",1);
                actionIntent.putExtra("downPosition",position);
                sendBroadcast(actionIntent);
                return ;
            }
            DownTaskBean taskBean=downTaskBeans.get(position);
            taskBean.setDowning(true);
            taskBean.setPause(false);
            long taskId = Aria.download(this)
                    .load(taskBean.getDownUrl())     //读取下载地址
                    .ignoreCheckPermissions()
                    .ignoreFilePathOccupy()
                    .setFilePath(MusicDownloadPath+taskBean.getTaskName()+".mp3") //设置文件保存的完整路径
                    .create();
            taskBean.setTaskId(taskId);
            nowDowningPosition=position;
        }
        public boolean stopTask(int position){
            DownTaskBean taskBean=downTaskBeans.get(position);
            taskBean.setPause(true);
            if (taskBean.getTaskId()==-1){
                return false;
            }
            Aria.download(this)
                    .load(taskBean.getTaskId())     //读取任务id
                    .stop();       // 停止任务
            return true;
        }
        public boolean continueTask(int position){
            if (MusicService.GetNetype(getApplicationContext())!=1&&!App.getShared().getBoolean("mobileNetworkDown",true)){    //移动网络下载判断
                Intent actionIntent = new Intent(DOWNLOAD_DONE_ACTION);
                actionIntent.putExtra("DOWNLOAD_ACTION_VAL",1);
                actionIntent.putExtra("downPosition",position);
                sendBroadcast(actionIntent);
                return false;
            }
            DownTaskBean taskBean=downTaskBeans.get(position);
            if (taskBean.getTaskId()==-1){
                downTask(position);
                return false;
            }
            taskBean.setDowning(true);
            Aria.download(this)
                    .load(taskBean.getTaskId())     //读取任务id
                    .resume();    // 恢复任务
            return true;
        }
    }
    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning protected void running(DownloadTask task) {
        int p = task.getPercent();	//任务进度百分比
        String speed =task.getConvertSpeed();//转换单位后的下载速度，单位转换需要在配置文件中打开
        for (DownTaskBean taskBean:
                downTaskBeans) {
            if(task.getKey().equals(taskBean.getDownUrl())){
                taskBean.setDowning(true);
                taskBean.setProgress(p);
                taskBean.setDownSpeed(speed);
                taskBean.getMusic().setProgress(p);
                MusicDaoImpl.getMusicDao().update(taskBean.getMusic());
                break;
            }
        }
    }
    @Download.onTaskFail void taskFail(DownloadTask task, Exception e) {
        if (downTaskBeans.size()>0){
            for (DownTaskBean taskBean:
                    downTaskBeans) {
                if(task.getKey().equals(taskBean.getDownUrl())){
                    taskBean.setPause(true);
                    taskBean.setDowning(false);
                    break;
                }
            }
            for (int i=0;i<downTaskBeans.size();i++){
                if (!downTaskBeans.get(i).isPause()){
                    HomeActivity.downloadControl.continueTask(i);
                    break;
                }
            }
        }
    }
    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        //在这里处理任务完成的状态
        Intent actionIntent = new Intent(DOWNLOAD_DONE_ACTION);
        int downPosition=0;
        for (DownTaskBean taskBean:
                downTaskBeans) {
            if(task.getKey().equals(taskBean.getDownUrl())){
                actionIntent.putExtra("downPosition",downPosition);
                taskBean.getMusic().setUrl(MusicDownloadPath+taskBean.getTaskName()+".mp3");
                taskBean.getMusic().setProgress(100);
                taskBean.getMusic().setSize(task.getFileSize());
                MusicDaoImpl.deleteMusicFromSonglist(HomeActivity.downSongList,taskBean.getMusic());
                MusicDaoImpl.addMusicToSongList(HomeActivity.localSongList,taskBean.getMusic());
                MusicDaoImpl.updateMusicDownloadDone(taskBean.getMusic());
                downTaskBeans.remove(taskBean);
                sendNotification("歌曲下载完成","任务("+taskBean.getMusic().getName()+"-"+taskBean.getMusic().getSinger()+")下载完成");
                break;
            }
            downPosition++;
        }
        for (int i=0;i<downTaskBeans.size();i++){
            if (!downTaskBeans.get(i).isPause()){
                downTaskBeans.get(i).setDowning(true);
                HomeActivity.downloadControl.continueTask(i);
                break;
            }
        }
        sendBroadcast(actionIntent);
    }
    private void sendNotification(String title,String value){
        if (!App.getShared().getBoolean("allowNotification",true)){
            return;
        }
        NotificationManager manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
        Intent intent = new Intent(this, DownloadActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);
        Notification notification =new NotificationCompat.Builder(getApplicationContext(),notificationChannelID)
                .setSmallIcon(R.drawable.music)
                .setTicker("下载完成")
                .setContentIntent(pi)
                .setContentTitle(title)
                //设置通知栏中的标题
                .setContentText(value)
                .setOngoing(false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.music))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .build();
        Notification summaryNotification =
                new NotificationCompat.Builder(getApplicationContext(), notificationChannelID)
                        .setContentTitle("MusicPlayer下载歌曲完成")
                        //set content text to support devices running API level < 24
                        .setContentText("歌曲下载完成")
                        .setSmallIcon(R.drawable.music)
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .setBigContentTitle("下载完成")
                                .setSummaryText("歌曲下载完成"))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(notifyId,notification);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(SUMMARY_ID, summaryNotification);
        notifyId++;
    }
}
