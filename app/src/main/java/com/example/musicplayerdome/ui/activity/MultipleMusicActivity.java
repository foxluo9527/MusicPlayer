package com.example.musicplayerdome.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayerdome.R;
import com.example.musicplayerdome.adapter.MusicMultipleAdapter;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.dao.MusicDaoImpl;
import com.example.musicplayerdome.service.MusicService;
import com.example.musicplayerdome.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayerdome.service.MusicService.VAL_UPDATE_UI_PLAY;

public class MultipleMusicActivity extends BaseActivity implements View.OnClickListener{
    public static boolean[] choices;
    public static boolean allChoice=false;
    private ListView listView;
    private TextView choiceNum;
    private TextView allChoiceText;
    private Context context;
    MusicMultipleAdapter adapter;
    ArrayList<Music> musics;
    SongList songList;
    View addToPlay,addToList,downList,deleteList;
    int musicModel;
    public static final int LOCAL_MUSIC_MODEL=1,ONLINE_MUSIC_MODEL=2;
    @Override
    protected int getResId() {
        return R.layout.activity_multiple_music;
    }

    @Override
    protected void onConfigView() {
        listView=findViewById(R.id.music_choice_list);
        choiceNum=findViewById(R.id.choice_song_num);
        allChoiceText=findViewById(R.id.all_choice_text);
        addToPlay=findViewById(R.id.add_to_play);
        addToList=findViewById(R.id.add_to_list);
        downList=findViewById(R.id.down_list);
        deleteList=findViewById(R.id.delete_list);
    }

    @Override
    protected void setLocalClassName() {
        this.lockAppName+=this.getLocalClassName();
    }
    @Override
    protected void initData() {
        musicModel=getIntent().getIntExtra("musicModel",LOCAL_MUSIC_MODEL);
        if (musicModel==LOCAL_MUSIC_MODEL){
            int listPosition=getIntent().getIntExtra("listPosition",-1);
            if (listPosition<0){
                return;
            }
            songList=MusicDaoImpl.getSongLists().get(listPosition);
            musics= (ArrayList<Music>) songList.getMusics();
        }else {
            Bundle bundle=getIntent().getExtras();
            musics=bundle.getParcelableArrayList("choiceMusics");
            TextView delTv=deleteList.findViewById(R.id.del_text);
            delTv.setText("删除下载");
        }

        context=MultipleMusicActivity.this;
        choices= new boolean[musics.size()];
        for(int i=0;i<choices.length;i++){
            choices[i]=false;
        }
        adapter=new MusicMultipleAdapter(musics,context,this::onClick);
        listView.setAdapter(adapter);
        choiceNum.setText("已选中(0)");
        allChoiceText.setText("全选");
    }

    public void doClick(View view) {
        int choiceCount=0;
        for(int i=0;i<choices.length;i++){
            if (choices[i])
                choiceCount++;
        }
        switch (view.getId()){
            case R.id.btn_return:
                finish();
                break;
            case R.id.all_choice:
                if (choices.length==0){
                    return;
                }
                if (!allChoice){
                    allChoiceText.setText("取消全选");
                    choiceNum.setText("已选中("+choices.length+")");
                }else {
                    allChoiceText.setText("全选");
                    choiceNum.setText("已选中(0)");
                }
                allChoice=!allChoice;
                for(int i=0;i<choices.length;i++){
                    choices[i]=allChoice;
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.add_to_list:
                if (choiceCount==0){
                    Toast.makeText(context,"还未选择任何歌曲",Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<Music> newMusics=new ArrayList<>();
                for (int i=0;i<choices.length;i++){
                    if (choices[i]){
                        newMusics.add(musics.get(i));
                    }
                }
                LocalSongActivity.addMusicToSongList(context,newMusics);
                break;
            case R.id.add_to_play:
                if (choiceCount==0){
                    Toast.makeText(context,"还未选择任何歌曲",Toast.LENGTH_SHORT).show();
                    return;
                }
                addToPlayList();
                break;
            case R.id.delete_list:
                if (choiceCount==0){
                    Toast.makeText(context,"还未选择任何歌曲",Toast.LENGTH_SHORT).show();
                    return;
                }
                delMusics();
                break;
            case R.id.down_list:
                if (choiceCount==0){
                    Toast.makeText(context,"还未选择任何歌曲",Toast.LENGTH_SHORT).show();
                    return;
                }
                downMusics();
                break;
        }
    }
    private void delMusics(){
        final AlertDialog show;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        View view=View.inflate(context, R.layout.sure_del_list, null);
        View sure,cancel;
        TextView issueText=view.findViewById(R.id.issue_text);
        issueText.setText("确认删除所选歌曲?");
        final CheckBox checkBox;
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);

        alertDialog.setView(view);
        show=alertDialog.create();
        show.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicModel==LOCAL_MUSIC_MODEL){
                    int delCount=0;
                    if (allChoice){
                        MusicDaoImpl.emptySongList(songList);
                        delCount=songList.getMusics().size();
                    }else {
                        ArrayList<Music> tempMusics=new ArrayList<>();
                        tempMusics.addAll(musics);
                        for (int i=0;i<choices.length;i++){
                            if (choices[i]){
                                Music m=tempMusics.get(i);
                                if(MusicDaoImpl.deleteMusicFromSonglist(songList,m))
                                    delCount++;
                            }
                        }
                    }
                    Toast.makeText(context,"成功删除"+delCount+"首歌曲",Toast.LENGTH_SHORT).show();
                    initData();
                    LocalSongActivity.refreshView(context,VAL_UPDATE_UI_PLAY);
                }else {
                    ArrayList<Music> tempMusics=new ArrayList<>();
                    tempMusics.addAll(musics);
                    int delCount=0;
                    for (int i=0;i<choices.length;i++){
                        if (choices[i]){
                            Music m=tempMusics.get(i);
                            if(MusicDaoImpl.deleteMusicFromSonglist(HomeActivity.localSongList,m))
                                delCount++;
                        }
                    }
                    Toast.makeText(context,"成功删除"+delCount+"首下载歌曲",Toast.LENGTH_SHORT).show();
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
    private void downMusics(){
        ArrayList<Music> tempMusics=new ArrayList<>();
        tempMusics.addAll(musics);
        final AlertDialog show;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        View view=View.inflate(context, R.layout.sure_del_list, null);
        View sure,cancel;
        TextView issueText=view.findViewById(R.id.issue_text);
        issueText.setText("确认下载所选歌曲?");
        sure=view.findViewById(R.id.sure);
        cancel=view.findViewById(R.id.cancel);
        alertDialog.setView(view);
        show=alertDialog.create();
        show.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int downCount=0;
                for (int i=0;i<choices.length;i++){
                    if (choices[i]){
                        Music m=tempMusics.get(i);
                        if (m.getUrl().contains("http")){
                            boolean hadDown=false;
                            for (Music music:
                                    HomeActivity.downSongList.getMusics()) {
                                if (m.getName().equals(music.getName())&&m.getSinger().equals(music.getSinger())&&m.getAlbum().equals(music.getAlbum())){
                                    hadDown=true;
                                    break;
                                }
                            }
                            if (!hadDown)
                                hadDown=MusicDaoImpl.checkLocalMusic(m);
                            if (!hadDown){
                                MusicDaoImpl.addMusicToSongList(HomeActivity.downSongList,m);
                                downCount++;
                            }
                        }
                    }
                }
                Toast.makeText(context,downCount+"首歌加入下载列表",Toast.LENGTH_SHORT).show();
                show.dismiss();
                if (downCount>0){
                    Intent intent=new Intent(MultipleMusicActivity.this, DownloadActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                    finish();
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
    private void addToPlayList() {
        int addNum=0;
        if (MusicService.listMusic==null||MusicService.listMusic.size()==0){
            MusicService.listMusic=new ArrayList<>();
            for (int i=musics.size()-1;i>=0;i--){
                if (choices[i]){
                    Music m=musics.get(i);
                    MusicService.listMusic.add(m);
                    MusicDaoImpl.addPlayMusic(new PlayMusicBean(m.getId(),m.getSId(),m.getMId(),m.getTitle(),m.getSinger(),m.getAlbum(),m.getUrl(),m.getSize(),
                            m.getTime(),m.getName(),m.getAlbumImg(),m.getProgress()));
                    addNum++;
                }
            }
            MusicService.initPlayMsg(0,MusicService.listMusic);
        }else {
            for (int i=musics.size()-1;i>=0;i--){
                if (choices[i]){
                    Music m=musics.get(i);
                    boolean hadMusic=false;
                    for (int j=0;j<MusicService.listMusic.size();j++){
                        if (MusicService.listMusic.get(j).getUrl().equals(m.getUrl())){
                            hadMusic=true;
                            break;
                        }
                    }
                    if (!hadMusic){
                        MusicService.listMusic.add(m);
                        MusicDaoImpl.addPlayMusic(new PlayMusicBean(m.getId(),m.getSId(),m.getMId(),m.getTitle(),m.getSinger(),m.getAlbum(),m.getUrl(),m.getSize(),
                                m.getTime(),m.getName(),m.getAlbumImg(),m.getProgress()));
                        addNum++;
                    }
                }
            }
            MusicService.initPlayMsg(MusicService.position,MusicService.listMusic);
        }
        Toast.makeText(context,addNum+"首歌添至播放列表成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int position= (int) v.getTag();
        switch (v.getId()){
            case R.id.music_item_main_view:
                v=v.findViewById(R.id.choice);
                ((CheckBox)v).setChecked(!((CheckBox)v).isChecked());
            case R.id.choice:
                choices[position]=((CheckBox)v).isChecked();
                int choiceCount=0;
                for(int i=0;i<choices.length;i++){
                    if (choices[i])
                        choiceCount++;
                }
                allChoice=(choiceCount==choices.length);
                if (!allChoice) allChoiceText.setText("全选");
                else allChoiceText.setText("取消全选");
                choiceNum.setText("已选中("+choiceCount+")");
                break;
        }
    }
}
