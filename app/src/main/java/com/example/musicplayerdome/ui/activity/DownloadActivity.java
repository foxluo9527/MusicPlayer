package com.example.musicplayerdome.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.DownLoadListAdapter;
import com.example.musicplayerdome.adapter.MusicListAdapter;
import com.example.musicplayerdome.adapter.PlayListAdapter;
import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.DownTaskBean;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicDownloadService;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;
import com.example.musicplayerdome.ui.component.CircleImageView;
import com.example.musicplayerdome.ui.fragment.FragmentMine;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.musicplayerdome.service.MusicService.VAL_UPDATE_UI_PLAY;
import static com.example.musicplayerdome.service.MusicService.position;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.changeRuleImg;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.setRuleText;

public class DownloadActivity extends BaseActivity implements View.OnClickListener{
    TextView downNum;
    ImageView downControlImg;
    TextView downControlText;
    private PlayListAdapter playAdapter;
    private View mainView;
    ImageView playImg;
    Animation animation;
    CircleImageView songImg;
    TextView songNameTv,singerTv;
    private MyReceiver myReceiver;
    private MusicDownloadDoneReceiver downloadDoneReceiver;
    ArrayList<DownTaskBean> downTaskBeans;
    DownLoadListAdapter adapter;
    ListView downListView;
    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void handleMessage(Message msg) {
            if (adapter!=null){
                adapter.notifyDataSetChanged();
                downNum.setText("("+downTaskBeans.size()+")");
            }else {
                initData();
            }
            handler.sendEmptyMessageDelayed(1, 500);
        }
    };
    @Override
    protected int getResId() {
        return R.layout.activity_download;
    }

    @Override
    protected void onConfigView() {
        downListView=findViewById(R.id.down_list_view);
        downNum=findViewById(R.id.down_num);
        downControlImg=findViewById(R.id.down_control_img);
        downControlText=findViewById(R.id.down_control_text);
        mainView=findViewById(R.id.show_music_view);
        playImg=findViewById(R.id.home_song_play);
        songImg=findViewById(R.id.home_song_img);
        songNameTv=findViewById(R.id.home_song_name);
        singerTv=findViewById(R.id.home_song_lrc);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void initData() {
        downControlImg.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.play_src_btn));
        downControlText.setText("全部开始");
        downTaskBeans=new ArrayList<>();
        if (MusicDownloadService.downTaskBeans.size()==0||MusicDownloadService.downTaskBeans.size()!=HomeActivity.downSongList.getMusics().size()){
            for (int i=0;i<HomeActivity.downSongList.getMusics().size();i++) {
                Music m=HomeActivity.downSongList.getMusics().get(i);
                String taskName=m.getName()+"-"+m.getSinger();
                Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
                Matcher matcher = pattern.matcher(taskName);
                taskName= matcher.replaceAll("");
                if (m.getUrl().contains("http")){
                    downTaskBeans.add(new DownTaskBean(-1,taskName,m.getProgress(),"0.0b/s",false,m.getUrl(),true,m));
                }else {
                    MusicDaoImpl.deleteMusicFromSonglist(HomeActivity.downSongList,m);
                }
                MusicDownloadService.nowDowningPosition=-1;
            }
            HomeActivity.downloadControl.initTasks(downTaskBeans);
        }else {
            downTaskBeans.addAll(MusicDownloadService.downTaskBeans);
        }
        adapter=new DownLoadListAdapter(downTaskBeans,this,this::onClick);
        downListView.setAdapter(adapter);

        animation= AnimationUtils.loadAnimation(this,R.anim.rotate);
        songImg.setAnimation(animation);
        updateMusicView();
        if (myReceiver==null){
            myReceiver = new MyReceiver(new Handler());
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(MusicService.MAIN_UPDATE_UI);
            registerReceiver(myReceiver, itFilter);
        }
        if (downloadDoneReceiver==null){
            downloadDoneReceiver=new MusicDownloadDoneReceiver();
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(MusicDownloadService.DOWNLOAD_DONE_ACTION);
            registerReceiver(downloadDoneReceiver, itFilter);
        }
        if (HomeActivity.downSongList.getMusics().size()==0){
            findViewById(R.id.down_view).setVisibility(View.GONE);
        }
        downListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (downTaskBeans.get(position).isDowning()){
                    downTaskBeans.get(position).setDowning(false);
                    HomeActivity.downloadControl.stopTask(position);
                    if (MusicDownloadService.nowDowningPosition==position){
                        for (int i=0;i<downTaskBeans.size();i++){
                            if (!downTaskBeans.get(i).isPause()){
                                HomeActivity.downloadControl.continueTask(i);
                                break;
                            }
                        }
                    }
                }else {
                    if (downTaskBeans.get(position).isPause()) {
                        downTaskBeans.get(position).setPause(false);
                        int i;
                        for (i=0;i<downTaskBeans.size();i++){
                            if (downTaskBeans.get(i).isDowning()){
                                break;
                            }
                        }
                        if (i==downTaskBeans.size()){
                            HomeActivity.downloadControl.continueTask(position);
                        }
                    } else {
                        downTaskBeans.get(position).setPause(true);
                        HomeActivity.downloadControl.stopTask(position);
                        if (MusicDownloadService.nowDowningPosition==position){
                            MusicDownloadService.nowDowningPosition=0;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        handler.sendEmptyMessage(1);
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
                showListPopupMenu(DownloadActivity.this);
                break;
            case R.id.show_music_view:
                Intent intent=new Intent(DownloadActivity.this,DetailsActivity.class);
                intent.putExtra("continuePlay",true);
                startActivity(intent);
                break;
            case R.id.down_control_view:
                if (MusicDownloadService.downTaskBeans.size()==0){
                    return;
                }
                if (MusicDownloadService.nowDowningPosition!=-1) {
                    HomeActivity.downloadControl.stopAllTask();
                    downControlImg.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.play_src_btn));
                    downControlText.setText("全部开始");
                } else {
                    HomeActivity.downloadControl.startAllTask();
                    downControlImg.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pause));
                    downControlText.setText("全部暂停");
                    HomeActivity.downloadControl.downTask(0);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.down_clean:
                final AlertDialog show;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DownloadActivity.this);
                View v=View.inflate(DownloadActivity.this, R.layout.sure_del_list, null);
                View sure,cancel;
                TextView issue_text;
                issue_text=v.findViewById(R.id.issue_text);
                issue_text.setText("确认清空下载列表?");
                sure=v.findViewById(R.id.sure);
                cancel=v.findViewById(R.id.cancel);
                alertDialog.setView(v);
                show=alertDialog.create();
                show.show();
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                        MusicDaoImpl.emptySongList(HomeActivity.downSongList);
                        HomeActivity.downloadControl.stopAllTask();
                        MusicDownloadService.downTaskBeans.clear();
                        downTaskBeans.clear();
                        adapter.notifyDataSetChanged();
                        findViewById(R.id.down_view).setVisibility(View.GONE);
                        return;
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int position= (int) v.getTag();
        final AlertDialog show;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DownloadActivity.this);
        View view=View.inflate(DownloadActivity.this, R.layout.sure_del_list, null);
        View sure,cancel;
        TextView issue_text;
        issue_text=view.findViewById(R.id.issue_text);
        issue_text.setText("确认删除此下载任务?");
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);
        alertDialog.setView(view);
        show=alertDialog.create();
        show.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                HomeActivity.downloadControl.stopTask(position);
                MusicDaoImpl.deleteMusicFromSonglist(HomeActivity.downSongList,downTaskBeans.get(position).getMusic());
                downTaskBeans.remove(position);
                adapter.notifyDataSetChanged();
                if (downTaskBeans.size()==0){
                    findViewById(R.id.down_view).setVisibility(View.GONE);
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        unregisterReceiver(downloadDoneReceiver);
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
                }
            });
        }
    }
    private class MusicDownloadDoneReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            int position=intent.getIntExtra("downPosition",-1);
            if (intent.getIntExtra("DOWNLOAD_ACTION_VAL",-1)==1){
                System.out.println("下载页面收到：下载失败,请求打开移动网络下载");
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                View view=View.inflate(context, R.layout.allow_mobile_net_play_dialog, null);
                View sure,cancel;
                sure=view.findViewById(R.id.sure);
                cancel=view.findViewById(R.id.cancel);
                TextView issueText=view.findViewById(R.id.issue_text);
                CheckBox noMoreAgain=view.findViewById(R.id.no_more_again);
                issueText.setText("是否使用移动网络下载?");
                alertDialog.setView(view);
                AlertDialog dialog=alertDialog.create();
                if (!App.getShared().getBoolean("neverAskNetworkDown",false))
                    dialog.show();
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        App.getShared().putBoolean("mobileNetworkDown",true);
                        dialog.dismiss();
                        HomeActivity.downloadControl.continueTask(position);
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (noMoreAgain.isChecked()){
                            App.getShared().putBoolean("neverAskNetworkDown",true);
                            Toast.makeText(context,"将不再使用移动网络下载，如需打开移动网络下载，请前往设置",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else {
                System.out.println("下载页面收到：下载完成");
                if (position!=-1){
                    if (downTaskBeans.size()>0)
                        downTaskBeans.remove(position);
                    adapter.notifyDataSetChanged();
                }else {
                    initData();
                }
            }
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
                        playImg.setImageDrawable(DownloadActivity.this.getDrawable(R.drawable.play));
                        songImg.clearAnimation();
                    } else {
                        playImg.setImageDrawable(DownloadActivity.this.getDrawable(R.drawable.pause));
                        songImg.startAnimation(animation);
                    }
                }
            }, 600);
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
                LocalSongActivity.refreshView(context,VAL_UPDATE_UI_PLAY);
                WindowManager.LayoutParams lp = DownloadActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                DownloadActivity.this.getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = DownloadActivity.this.getWindow().getAttributes();
        lp.alpha = 0.5f;
        DownloadActivity.this.getWindow().setAttributes(lp);

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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DownloadActivity.this);
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
