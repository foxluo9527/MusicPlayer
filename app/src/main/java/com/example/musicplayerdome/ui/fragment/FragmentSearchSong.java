package com.example.musicplayerdome.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.SearchMusicListAdapter;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.MyMusicSearchBean;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.activity.DetailsActivity;
import com.example.musicplayerdome.ui.activity.LocalSongActivity;
import com.example.musicplayerdome.ui.activity.MultipleMusicActivity;
import com.example.musicplayerdome.ui.base.BaseFragment;
import com.example.musicplayerdome.utils.HttpRequest;

import java.util.ArrayList;

import static com.example.musicplayerdome.service.MusicService.VAL_UPDATE_UI_PLAY;

public class FragmentSearchSong extends BaseFragment implements View.OnClickListener{
    String queryString;
    View choiceView;
    ArrayList< MyMusicSearchBean.SongsBean> songs;
    ArrayList<Music> musics;
    ListView listView;
    TextView numTv;
    int nowPage=0;
    SearchMusicListAdapter adapter;
    boolean searchDone=false;
    Context context;
    View playAllView;
    public FragmentSearchSong(String searchString) {
        super();
        this.queryString=searchString;
    }

    @Override
    protected int getResId() {
        return R.layout.song_list;
    }

    @Override
    protected void onConfigView() {
        numTv=getActivity().findViewById(R.id.list_num);
        listView=getActivity().findViewById(R.id.song_list);
        choiceView=getActivity().findViewById(R.id.list_choice_view);
        choiceView.setOnClickListener(this::onClick);
        playAllView=getActivity().findViewById(R.id.play_all_view);
    }

    @Override
    protected void initData() {
        context=getContext();
        musics=new ArrayList<>();
        adapter=new SearchMusicListAdapter(context,FragmentSearchSong.this::onClick,musics);
        listView.setAdapter(adapter);
        getSearchMusic(queryString);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = listView.getChildAt(listView.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == listView.getHeight()) {
                        getSearchMusic(queryString);
                    }
                }
            }
        });
    }
    boolean onLoading=false;
    private void getSearchMusic(String searchString){
        if (onLoading){
            return;
        }
        if (searchDone){
            Toast.makeText(context,"人家也是有底线的",Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingDialog();
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                onLoading=true;
                songs= HttpRequest.getYunFormatSearchSimple(searchString,"",20,nowPage*20);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                onLoading=false;
                dismissLoadingDialog();
                if (songs!=null){
                    if (songs.size()==20){
                        nowPage++;
                    }else {
                        searchDone=true;
                    }
                    for (MyMusicSearchBean.SongsBean song:
                            songs) {
                        Music music=new Music();
                        music.setSId((long)-1);
                        music.setMId(Long.valueOf(song.getId()));
                        music.setId(Long.valueOf(song.getId()));
                        music.setUrl("https://music.163.com/song/media/outer/url?&id="+song.getId());
                        music.setSinger(song.getSinger());
                        music.setProgress(0);
                        music.setTime(song.getTime());
                        music.setName(song.getName());
                        music.setTitle(song.getSinger()+"-"+song.getName());
                        music.setAlbum(song.getAlbum());
                        music.setAlbumImg(song.getPic());
                        music.setSize(0);
                        musics.add(music);
                    }
                    numTv.setText("("+musics.size()+")");
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(context,"网络好像开小差了...",Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    void showMusicMorePopup(Context context, Music music){
        final View popView = View.inflate(context,R.layout.music_more_popup,null);
        View insertPlayView=popView.findViewById(R.id.insert_play_list);
        View addListView=popView.findViewById(R.id.add_list);
        View setCoverView=popView.findViewById(R.id.set_cover);
        setCoverView.setVisibility(View.GONE);
        TextView singerTv,nameTv,albumTv;
        View showSongView=popView.findViewById(R.id.show_song_more);
        showSongView.setVisibility(View.GONE);
        View deleteSongView=popView.findViewById(R.id.delete_song);
        deleteSongView.setVisibility(View.GONE);
        singerTv=popView.findViewById(R.id.song_singer);
        nameTv=popView.findViewById(R.id.song_name);
        albumTv=popView.findViewById(R.id.song_album);
        if (music!=null){
            singerTv.setText(music.getSinger());
            nameTv.setText(music.getName());
            albumTv.setText(music.getAlbum());
        }
        //获取屏幕宽高
        int weight = ((Activity)context).getResources().getDisplayMetrics().widthPixels;
        int height = ((Activity)context).getResources().getDisplayMetrics().heightPixels*2/3;

//        int height = getResources().getDisplayMetrics().heightPixels/2;
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
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);

        insertPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextPlayPosition= MusicService.playList.indexOf(MusicService.position+1);//获取下一首在播放顺序的位置
                if (nextPlayPosition==-1)
                    nextPlayPosition=0;
                if (MusicService.listMusic.contains(music)){//播放列表已有此歌曲
                    int position=MusicService.listMusic.indexOf(music);//获取在播放列表的位置
                    for (int i=0;i<MusicService.playList.size();i++){
                        if(MusicService.playList.get(i)==position){
                            //与下一首交换播放顺序的位置
                            int nextMusicPosition=MusicService.playList.get(nextPlayPosition);
                            MusicService.playList.set(nextPlayPosition, MusicService.playList.get(i));
                            MusicService.playList.set(i, nextMusicPosition);
                            break;
                        }
                    }
                }else {     //播放列表中没有
                    //插入在最后一个
                    int index=MusicService.listMusic.size();
                    MusicService.playList.add(nextPlayPosition,index);
                    MusicDaoImpl.addPlayMusic(new PlayMusicBean(music.getId(),music.getSId(),music.getMId(),music.getTitle(),music.getSinger(),music.getAlbum(),music.getUrl(),
                            music.getSize(),music.getTime(),music.getName(),music.getAlbumImg(),music.getProgress()));
                    MusicService.listMusic.add(index,music);
                }
                Toast.makeText(context,"添至下一曲播放成功",Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
        addListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Music> musics=new ArrayList<>();
                musics.add(music);
                LocalSongActivity.addMusicToSongList(context,musics);
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_all_view:
                Intent intent1=new Intent(context,DetailsActivity.class);
                MusicService.initPlayMsg(0, musics);
                adapter.notifyDataSetChanged();
                startActivity(intent1);
                break;
            case R.id.list_choice_view:
                Intent intent2=new Intent(context, MultipleMusicActivity.class);
                Bundle bundle=new Bundle();
                intent2.putExtra("musicModel",MultipleMusicActivity.ONLINE_MUSIC_MODEL);
                bundle.putParcelableArrayList("choiceMusics",musics);
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            case R.id.music_play_view:
                int position=(int)v.getTag();
                Intent intent=new Intent(context, DetailsActivity.class);
                MusicService.initPlayMsg(position, musics);
                adapter.notifyDataSetChanged();
                startActivity(intent);
                break;
            case R.id.music_more_view:
                int position1=(int)v.getTag();
                showMusicMorePopup(context,musics.get(position1));
                break;
        }
    }
}
