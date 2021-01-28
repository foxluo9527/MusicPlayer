package com.example.musicplayerdome.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.MusicListAdapter;
import com.example.musicplayerdome.adapter.PlayListAdapter;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.activity.DetailsActivity;
import com.example.musicplayerdome.ui.activity.HomeActivity;
import com.example.musicplayerdome.ui.activity.LikeMusicActivity;
import com.example.musicplayerdome.ui.activity.LocalSongActivity;
import com.example.musicplayerdome.ui.activity.SearchResultActivity;
import com.example.musicplayerdome.ui.base.BaseFragment;
import com.example.musicplayerdome.ui.component.CircleImageView;

import static com.example.musicplayerdome.service.MusicService.VAL_UPDATE_UI_PLAY;
import static com.example.musicplayerdome.service.MusicService.position;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.changeRuleImg;
import static com.example.musicplayerdome.ui.activity.DetailsActivity.setRuleText;

public class FramentMusicbar extends BaseFragment implements View.OnClickListener{
    MyReceiver myReceiver;
    private View mainView,playView,listView;
    ImageView playImg;
    Animation animation;
    CircleImageView songImg;
    TextView songNameTv,singerTv;
    private PlayListAdapter playAdapter;
    Context context;
    @Override
    protected int getResId() {
        return R.layout.music_play_bar;
    }

    @Override
    protected void onConfigView() {
        mainView=getActivity().findViewById(R.id.show_music_view);
        playImg=getActivity().findViewById(R.id.home_song_play);
        songImg=getActivity().findViewById(R.id.home_song_img);
        songNameTv=getActivity().findViewById(R.id.home_song_name);
        singerTv=getActivity().findViewById(R.id.home_song_lrc);
        playView=getActivity().findViewById(R.id.play_view);
        listView=getActivity().findViewById(R.id.list_view);
    }

    @Override
    protected void initData() {
        context=getContext();
        if (myReceiver==null){
            myReceiver = new MyReceiver(new Handler());
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(MusicService.MAIN_UPDATE_UI);
            getActivity().registerReceiver(myReceiver, itFilter);
        }
        animation= AnimationUtils.loadAnimation(context,R.anim.rotate);
        songImg.setAnimation(animation);
        updateMusicView();
        mainView.setOnClickListener(this::onClick);
        playView.setOnClickListener(this::onClick);
        listView.setOnClickListener(this::onClick);
        if (MusicService.listMusic==null||MusicService.listMusic.size()==0){
            mainView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myReceiver!=null){
            getActivity().unregisterReceiver(myReceiver);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_music_view:
                Intent intent=new Intent(context,DetailsActivity.class);
                intent.putExtra("continuePlay",true);
                startActivity(intent);
                break;
            case R.id.list_view:
                showListPopupMenu(getContext());
                break;
            case R.id.play_view:
                HomeActivity.musicControl.play();
                updateMusicView();
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
                }
            });
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
                    if (HomeActivity.musicControl==null||!HomeActivity.musicControl.isPlaying()) {
                        playImg.setImageDrawable(context.getDrawable(R.drawable.play));
                        songImg.clearAnimation();
                    } else {
                        playImg.setImageDrawable(context.getDrawable(R.drawable.pause));
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
                WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp =  ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);

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
                        context.sendBroadcast(intent);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
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
