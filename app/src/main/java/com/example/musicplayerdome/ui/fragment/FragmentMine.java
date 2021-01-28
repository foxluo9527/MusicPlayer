package com.example.musicplayerdome.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.PlayListAdapter;
import com.example.musicplayerdome.adapter.SongListAdapter;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicDownloadService;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.activity.DetailsActivity;
import com.example.musicplayerdome.ui.activity.DownloadActivity;
import com.example.musicplayerdome.ui.activity.EditSongListActivity;
import com.example.musicplayerdome.ui.activity.HomeActivity;
import com.example.musicplayerdome.ui.activity.LikeMusicActivity;
import com.example.musicplayerdome.ui.activity.LocalSongActivity;
import com.example.musicplayerdome.ui.activity.ManageSongListActivity;
import com.example.musicplayerdome.ui.activity.RecordMusicActivity;
import com.example.musicplayerdome.ui.activity.SongListActivity;
import com.example.musicplayerdome.ui.base.BaseFragment;
import com.example.musicplayerdome.utils.MusicList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class FragmentMine extends BaseFragment implements View.OnClickListener{
    Context context;
    TextView localListNum,recordListNum,downListNum,likeListNum;
    ImageView localOnPlayImg,recordOnPlayImg,downOnPlayImg,likeOnPlayImg;
    ListView listView;
    TextView myListMsg;
    SongListAdapter songListAdapter;
    ScrollView scrollView;
    SwipeRefreshLayout refreshLayout;
    private MyReceiver myReceiver;
    private MusicDownloadDoneReceiver downloadDoneReceiver;
    @Override
    protected int getResId() {
        new Thread(getSongListsRunnable).start();
        return R.layout.fragment_mine;
    }

    @Override
    protected void onConfigView() {
        localListNum=getActivity().findViewById(R.id.local_list_num);
        recordListNum=getActivity().findViewById(R.id.record_list_num);
        downListNum=getActivity().findViewById(R.id.down_list_num);
        likeListNum=getActivity().findViewById(R.id.like_list_num);
        listView=getActivity().findViewById(R.id.song_lists_view);
        localOnPlayImg=getActivity().findViewById(R.id.local_list_onPlay);
        recordOnPlayImg=getActivity().findViewById(R.id.record_list_onPlay);
        downOnPlayImg=getActivity().findViewById(R.id.down_list_onPlay);
        likeOnPlayImg=getActivity().findViewById(R.id.like_list_onPlay);
        myListMsg=getActivity().findViewById(R.id.my_list_num);
        scrollView=getActivity().findViewById(R.id.m_scrollView);
        refreshLayout=getActivity().findViewById(R.id.music_refresh);
    }
    Handler listLoadDoneHandler=new Handler(){ //歌单信息加载完毕
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            HomeActivity.songLists=MusicDaoImpl.getSongLists();
            HomeActivity.localSongList=HomeActivity.songLists.get(0);
            HomeActivity.songLists.remove(HomeActivity.localSongList);//取出预设歌单后将其从普通歌单列表中移除
            HomeActivity.recordSongList=HomeActivity.songLists.get(0);
            HomeActivity.songLists.remove(HomeActivity.recordSongList);
            HomeActivity.downSongList=HomeActivity.songLists.get(0);
            HomeActivity.songLists.remove(HomeActivity.downSongList);
            HomeActivity.likeSongList=HomeActivity.songLists.get(0);
            HomeActivity.songLists.remove(HomeActivity.likeSongList);
//            MusicDaoImpl.addMusicListToSongList(HomeActivity.likeSongList,MusicList.getOnlineList());
            localListNum.setText("("+HomeActivity.localSongList.getMusics().size()+")");
            recordListNum.setText("("+HomeActivity.recordSongList.getMusics().size()+")");
            downListNum.setText("("+HomeActivity.downSongList.getMusics().size()+")");
            likeListNum.setText("("+HomeActivity.likeSongList.getMusics().size()+")");
            songListAdapter=new SongListAdapter(HomeActivity.songLists,context,FragmentMine.this::onClick);
            listView.setAdapter(songListAdapter);
            setListViewHeightBasedOnChildren(listView);
            setListOnPlayImg();
            myListMsg.setText("("+HomeActivity.songLists.size()+")");
            if (HomeActivity.nowListIndex!=-1&&HomeActivity.songLists.size()>0){
                MusicService.initPlayMsg(HomeActivity.nowPlayIndex,(ArrayList<Music>) HomeActivity.songLists.get(HomeActivity.nowListIndex).getMusics());
            }
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_UP);
                }
            });
            refreshLayout.setRefreshing(false);
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&scrollView!=null){
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_UP);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setRefreshing(true);
        listLoadDoneHandler.sendEmptyMessage(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myReceiver!=null)
        getActivity().unregisterReceiver(myReceiver);
        getActivity().unregisterReceiver(downloadDoneReceiver);
    }

    Runnable getSongListsRunnable = new Runnable() {
        @Override
        public void run() {
            HomeActivity.songLists= MusicDaoImpl.getSongLists();
            listLoadDoneHandler.sendEmptyMessage(0);
        }
    };
    @Override
    protected void initData() {
        context=getActivity();
        getActivity().findViewById(R.id.list_more).setOnClickListener(this);
        getActivity().findViewById(R.id.local_list).setOnClickListener(this);
        getActivity().findViewById(R.id.record_list).setOnClickListener(this);
        getActivity().findViewById(R.id.down_list).setOnClickListener(this);
        getActivity().findViewById(R.id.like_list).setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),SongListActivity.class);
                intent.putExtra("songListId",HomeActivity.songLists.get(position).getId());
                intent.putExtra("listPosition",position+4);
                startActivity(intent);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(getSongListsRunnable).start();
                    }
                }, 300);
            }
        });
        if (myReceiver==null){
            myReceiver = new MyReceiver(new Handler());
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(MusicService.MAIN_UPDATE_UI);
            getActivity().registerReceiver(myReceiver, itFilter);
        }
        if (downloadDoneReceiver==null){
            downloadDoneReceiver=new MusicDownloadDoneReceiver();
            IntentFilter itFilter = new IntentFilter();
            itFilter.addAction(MusicDownloadService.DOWNLOAD_DONE_ACTION);
            getActivity().registerReceiver(downloadDoneReceiver, itFilter);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.local_list:
                startActivity(new Intent(getActivity(),LocalSongActivity.class));
                break;
            case R.id.record_list:
                startActivity(new Intent(getActivity(), RecordMusicActivity.class));
                break;
            case R.id.down_list:
                startActivity(new Intent(getActivity(), DownloadActivity.class));
                break;
            case R.id.like_list:
                startActivity(new Intent(getActivity(), LikeMusicActivity.class));
                break;
            case R.id.list_more:
                showEditSongListPopupView();
                break;
            case R.id.img_song_list_more:
                int position=(int)v.getTag();
                showSonglistMore(position);
                break;
        }
    }

    private void setListOnPlayImg(){
        if (HomeActivity.nowListIndex>0&&HomeActivity.nowListIndex<4){       //在默认四个歌单中播放，将播放图标显示
            switch (HomeActivity.nowListIndex){
                case 0:
                    localOnPlayImg.setVisibility(View.VISIBLE);
                    recordOnPlayImg.setVisibility(View.GONE);
                    recordOnPlayImg.setVisibility(View.GONE);
                    likeOnPlayImg.setVisibility(View.GONE);
                    break;
                case 1:
                    localOnPlayImg.setVisibility(View.GONE);
                    recordOnPlayImg.setVisibility(View.VISIBLE);
                    recordOnPlayImg.setVisibility(View.GONE);
                    likeOnPlayImg.setVisibility(View.GONE);
                    break;
                case 2:
                    localOnPlayImg.setVisibility(View.GONE);
                    recordOnPlayImg.setVisibility(View.GONE);
                    recordOnPlayImg.setVisibility(View.VISIBLE);
                    likeOnPlayImg.setVisibility(View.GONE);
                    break;
                case 3:
                    localOnPlayImg.setVisibility(View.GONE);
                    recordOnPlayImg.setVisibility(View.GONE);
                    recordOnPlayImg.setVisibility(View.GONE);
                    likeOnPlayImg.setVisibility(View.VISIBLE);
                    break;
            }
        }else {
            localOnPlayImg.setVisibility(View.GONE);
            recordOnPlayImg.setVisibility(View.GONE);
            recordOnPlayImg.setVisibility(View.GONE);
            likeOnPlayImg.setVisibility(View.GONE);
            listView.setSelection(HomeActivity.nowListIndex-4);
            songListAdapter.notifyDataSetChanged();
        }
    }
    /**
     * 动态设置ListView的高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
        // pre-condition 
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight()+30;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showSonglistMore(final int position) {
        final View popView = View.inflate(context,R.layout.song_list_show_more_popup,null);
        TextView listName=popView.findViewById(R.id.show_song_list_name);

        listName.setText("歌单:"+HomeActivity.songLists.get(position).getName());
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*1/3;

        final PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp =getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        popView.findViewById(R.id.down_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Music> musics=HomeActivity.songLists.get(position).getMusics();
                int downCount=0;//当前列表中已经下载的歌曲数
                if (musics.size()!=0){
                    for (Music m:
                            musics) {
                        if (m.getUrl().contains("http")){
                            boolean hadDown=false;
                            for (Music music:
                                    HomeActivity.downSongList.getMusics()) {
                                if (m.getName().equals(music.getName())&&m.getSinger().equals(music.getSinger())&&m.getAlbum().equals(music.getAlbum())){
                                    hadDown=true;
                                    break;
                                }
                            }
                            if (!hadDown){
                                downCount++;
                            }
                        }
                    }
                    if (downCount==0){
                        Toast.makeText(context,"没有歌曲可以下载",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(context,"没有歌曲可以下载",Toast.LENGTH_SHORT).show();
                    return;
                }
                final AlertDialog show;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                View view=View.inflate(context, R.layout.sure_del_list, null);
                View sure,cancel;
                TextView issueText=view.findViewById(R.id.issue_text);
                issueText.setText("确认下载歌单内歌曲("+downCount+"首)");
                sure=view.findViewById(R.id.sure);
                cancel=view.findViewById(R.id.cancel);
                alertDialog.setView(view);
                show=alertDialog.create();
                show.show();
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (Music m:
                                musics) {
                            if (m.getUrl().contains("http")){
                                boolean hadDown=false;
                                for (Music music:
                                        HomeActivity.downSongList.getMusics()) {
                                    if (m.getName().equals(music.getName())&&m.getSinger().equals(music.getSinger())&&m.getAlbum().equals(music.getAlbum())){
                                        hadDown=true;
                                        break;
                                    }
                                }
                                if (!hadDown){
                                    MusicDaoImpl.addMusicToSongList(HomeActivity.downSongList,m);
                                }
                            }
                        }
                        show.dismiss();
                        popupWindow.dismiss();
                        startActivity(new Intent(getActivity(), DownloadActivity.class));
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                        popupWindow.dismiss();
                    }
                });
            }
        });
        popView.findViewById(R.id.edit_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), EditSongListActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        popView.findViewById(R.id.del_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog show;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                View view=View.inflate(context, R.layout.sure_del_list, null);
                View sure,cancel;
                TextView issueText=view.findViewById(R.id.issue_text);
                issueText.setText("确认删除此歌单及其内所有歌曲(不会删除本地下载的歌曲文件)");
                sure=view.findViewById(R.id.sure);
                cancel=view.findViewById(R.id.cancel);

                alertDialog.setView(view);
                show=alertDialog.create();
                show.show();
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MusicDaoImpl.deleteSongList(HomeActivity.songLists.get(position));
                        show.dismiss();
                        popupWindow.dismiss();
                        new Thread(getSongListsRunnable).start();
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
        getActivity().getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    private void showEditSongListPopupView(){
        final View popView = View.inflate(context,R.layout.song_list_edit_popup,null);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*1/4;

        final PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp =getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        popView.findViewById(R.id.edit_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ManageSongListActivity.class));
                popupWindow.dismiss();
            }
        });
        popView.findViewById(R.id.create_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog show;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                View view=View.inflate(context, R.layout.create_song_list_popup, null);
                View sure,cancel;
                EditText editText;
                sure=view.findViewById(R.id.create_btn);
                cancel=view.findViewById(R.id.cancel_btn);
                editText=view.findViewById(R.id.list_name);
                alertDialog.setView(view);
                show=alertDialog.create();
                show.show();
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String listName=editText.getText().toString();
                        if (!listName.isEmpty()){
                            SongList songList=new SongList();
                            songList.setIntroduce("暂无简介");
                            songList.setName(listName);
                            songList.setTime(new Date());
                            songList.setCoverImg("");
                            MusicDaoImpl.createSongList(songList);
                            HomeActivity.songLists=MusicDaoImpl.getSongLists();
                            for (int i=0;i<4;i++){
                                HomeActivity.songLists.remove(0);
                            }
                            new Thread(getSongListsRunnable).start();
                            show.dismiss();
                            popupWindow.dismiss();
                        }else {
                            Toast.makeText(context,"请输入歌单名称",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                        popupWindow.dismiss();
                    }
                });
                popupWindow.dismiss();
            }
        });
        getActivity().getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    private class MusicDownloadDoneReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            new Thread(getSongListsRunnable).start();
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
                    new Thread(getSongListsRunnable).start();
                }
            });
        }
    }
}
