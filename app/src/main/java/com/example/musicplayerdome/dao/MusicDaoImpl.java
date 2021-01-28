package com.example.musicplayerdome.dao;

import android.util.Log;

import com.example.musicplayerdome.application.App;
import com.example.musicplayerdome.bean.DaoSession;
import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.MusicDao;
import com.example.musicplayerdome.bean.PlayMusicBean;
import com.example.musicplayerdome.bean.PlayMusicBeanDao;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.bean.SongListDao;
import com.example.musicplayerdome.service.MusicService;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MusicDaoImpl {
    private static DaoSession daoSession=App.getInstances().getDaoSession();
    private static MusicDao musicDao=daoSession.getMusicDao();
    private static SongListDao songListDao=daoSession.getSongListDao();
    private static PlayMusicBeanDao playListDao=daoSession.getPlayMusicBeanDao();

    public MusicDaoImpl(){ }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static void setDaoSession(DaoSession daoSession) {
        MusicDaoImpl.daoSession = daoSession;
    }

    public static MusicDao getMusicDao() {
        return musicDao;
    }

    public static void setMusicDao(MusicDao musicDao) {
        MusicDaoImpl.musicDao = musicDao;
    }

    public static SongListDao getSongListDao() {
        return songListDao;
    }

    public static void setSongListDao(SongListDao songListDao) {
        MusicDaoImpl.songListDao = songListDao;
    }

    public static PlayMusicBeanDao getPlayListDao() {
        return playListDao;
    }

    public static void setPlayListDao(PlayMusicBeanDao playListDao) {
        MusicDaoImpl.playListDao = playListDao;
    }

    /**
     * 获取所有歌单信息
     * @return
     */
    public static ArrayList<SongList> getSongLists(){
        daoSession.clear();
        return (ArrayList<SongList>) songListDao.loadAll();
    }
    public static SongList getSongList(long songId){
        return songListDao.load(songId);
    }
    /**
     * 获取指定歌单内全部歌曲信息
     * @return
     */
    public static ArrayList<Music> getMusicList(Long songListId){ return (ArrayList<Music>) songListDao.load(songListId).getMusics(); }

    /**
     * 向指定歌单添加多首歌曲信息
     */
    public static int addMusicListToSongList(SongList songList,List<Music> musics){
        int addSucCount=0;
        songList.resetMusics();
        for (Music music:musics) {
            List<Music> temp = musicDao.queryBuilder()
                    .where(MusicDao.Properties.Name.eq(music.getName()))
                    .where(MusicDao.Properties.Singer.eq(music.getSinger()))
                    .where(MusicDao.Properties.Album.eq(music.getAlbum()))
                    .where(MusicDao.Properties.SId.eq(songList.getId()))
                    .list();
            if (temp.size()==0){  //列表中音乐不存在则存入
                Music tempMusic=new Music();
                tempMusic.setId(null);
                tempMusic.setSId(songList.getId());
                tempMusic.setAlbumImg(music.getAlbumImg());
                tempMusic.setAlbum(music.getAlbum());
                tempMusic.setName(music.getName());
                tempMusic.setSinger(music.getSinger());
                tempMusic.setSize(music.getSize());
                tempMusic.setTime(music.getTime());
                tempMusic.setTitle(music.getTitle());
                tempMusic.setUrl(music.getUrl());
                tempMusic.setProgress(music.getProgress());
                if (music.getMId()!=null)
                tempMusic.setMId(music.getMId());
                musicDao.insertOrReplace(tempMusic);
                addSucCount+=1;
            }
        }
        Log.i(TAG,songList.getName()+":存入歌曲数量==>"+addSucCount);
        songListDao.update(songList);
        return addSucCount;
    }
    /**
     * 向指定歌单添加歌曲信息
     */
    public static void addMusicToSongList(SongList songList,Music music){
        songList.resetMusics();
        List<Music> temp = musicDao.queryBuilder()
                .where(MusicDao.Properties.Name.eq(music.getName()))
                .where(MusicDao.Properties.Singer.eq(music.getSinger()))
                .where(MusicDao.Properties.Album.eq(music.getAlbum()))
                .where(MusicDao.Properties.SId.eq(songList.getId()))
                .list();
        if (temp.size()==0){  //列表中音乐不存在则存入
            Music tempMusic=new Music();
            tempMusic.setId(null);
            tempMusic.setSId(songList.getId());
            tempMusic.setAlbumImg(music.getAlbumImg());
            tempMusic.setAlbum(music.getAlbum());
            tempMusic.setName(music.getName());
            tempMusic.setSinger(music.getSinger());
            tempMusic.setSize(music.getSize());
            tempMusic.setTime(music.getTime());
            tempMusic.setTitle(music.getTitle());
            tempMusic.setUrl(music.getUrl());
            tempMusic.setProgress(music.getProgress());
            if (music.getMId()!=null)
            tempMusic.setMId(music.getMId());
            musicDao.insertOrReplace(tempMusic);
            Log.i(TAG,songList.getName()+":存入歌曲==>"+music.getName()+"-"+music.getSinger());
        }else {//已经存在，更新
            musicDao.update(music);
        }
        songListDao.update(songList);
    }
    public static void updateMusicDownloadDone(Music music){
        List<Music> temp = musicDao.queryBuilder()
                .where(MusicDao.Properties.Name.eq(music.getName()))
                .where(MusicDao.Properties.Singer.eq(music.getSinger()))
                .where(MusicDao.Properties.Album.eq(music.getAlbum()))
                .list();
        for (Music m:
             temp) {
            m.setProgress(100);
            m.setUrl(music.getUrl());
            musicDao.update(m);
        }
        int playPosition=0;
        for (Music m:
                MusicService.listMusic) {
            if (m.getName().equals(music.getName())&&m.getSinger().equals(music.getSinger())&&m.getAlbum().equals(music.getAlbum())){
                m.setProgress(100);
                m.setUrl(music.getUrl());
                m.setMId(music.getMId());
                List<PlayMusicBean> playMusicBeans=getPlayList();
                playMusicBeans.get(playPosition).setProgress(100);
                playMusicBeans.get(playPosition).setUrl(music.getUrl());
                playListDao.update(playMusicBeans.get(playPosition));
                break;
            }
            playPosition++;
        }
    }
    public static void updateMusicList(ArrayList<Music> musics){
        for (Music music:
             musics) {
            List<Music> temp = musicDao.queryBuilder()
                    .where(MusicDao.Properties.Name.eq(music.getName()))
                    .where(MusicDao.Properties.Singer.eq(music.getSinger()))
                    .where(MusicDao.Properties.Album.eq(music.getAlbum()))
                    .list();
            for (Music m:
                    temp) {
                m.setProgress(100);
                m.setUrl(music.getUrl());
                musicDao.update(m);
            }
            int playPosition=0;
            for (Music m:
                    MusicService.listMusic) {
                if (m.getName().equals(music.getName())&&m.getSinger().equals(music.getSinger())&&m.getAlbum().equals(music.getAlbum())){
                    m.setProgress(100);
                    m.setUrl(music.getUrl());
                    m.setMId(music.getMId());
                    List<PlayMusicBean> playMusicBeans=getPlayList();
                    playMusicBeans.get(playPosition).setProgress(100);
                    playMusicBeans.get(playPosition).setUrl(music.getUrl());
                    playListDao.update(playMusicBeans.get(playPosition));
                    break;
                }
                playPosition++;
            }
        }
    }
    /**
     * 新建歌单信息
     * @return
     */
    public static void createSongList(SongList songList){ songListDao.insertOrReplace(songList); }

    /**
     * 直接创建带有多个音乐信息的歌单
     * @param songList
     * @param musics
     */
    public static void createMusicsSongList(SongList songList,List<Music> musics){
        createSongList(songList);
        if (musics!=null){
            songList.resetMusics();
            for (Music music:musics) {
                songList.getMusics().add(music);
                music.setId(null);
                music.setSId(songList.getId());
                Log.i(TAG,songList.getName()+":存入歌曲==>"+music.getName()+"-"+music.getSinger());
                musicDao.insertOrReplace(music);
            }
        }
        songList.update();
    }
    public static boolean checkLocalMusic(Music music){
        ArrayList<Music> musics=getMusicList((long)1);
        for (Music m:
             musics) {
            if (m.getId().equals(music.getId())||(m.getMId()!=-1&&m.getMId().equals(music.getMId()))){
                return true;
            }
        }
        return false;
    }
    /**
     * 修改songList信息
     * @param songList
     */
    public static void updateSongListMsg(SongList songList){
        songListDao.update(songList);
        for (Music m:
             songList.getMusics()) {
            musicDao.update(m);
        }
    }
    /**
     * 删除指定歌单内的指定歌曲
     */
    public static boolean deleteMusicFromSonglist(SongList songList,Music music){
        boolean isDeled=false;
        List<Music> musics=songList.getMusics();
        for (Music m:musics) {
            if (m.getId()==music.getId()||(m.getMId()!=-1&&music.getMId().equals(m.getMId()))){
                Log.i(TAG,songList.getName()+":删除歌曲==>"+music.getName()+"-"+music.getSinger());
                musics.remove(music);
                musicDao.delete(music);
                isDeled=true;
                break;
            }
        }
        songList.update();
        return isDeled;
    }

    /**
     * 删除歌单信息，包括其内所有歌曲信息
     */
    public static void deleteSongList(SongList songList){
        emptySongList(songList);
        songListDao.delete(songList);
    }

    /**
     * 清空指定歌单内所有歌曲
     * @param songList
     */
    public static void emptySongList(SongList songList){
        int delCount=0;
        List<Music> musics=songList.getMusics();
        for (Music music:musics) {
            if (music.getSId()==songList.getId()){
                musicDao.delete(music);
                delCount++;
            }
        }
        Log.i(TAG,songList.getName()+":清空歌曲数量==>"+delCount);
        songList.resetMusics();
        songList.update();
    }

    /**
     * 获取所有播放列表
     * @return
     */
    public static List<PlayMusicBean> getPlayList(){
        List<PlayMusicBean> musicBeans=playListDao.loadAll();
        return musicBeans;
    }
    /**
     * 添加音乐列表至播放列表
     * @param musicList
     */
    public static void addPlayMusicList(List<PlayMusicBean> musicList){
        playListDao.deleteAll();
        for (PlayMusicBean playMusic:musicList) {
            playListDao.insertOrReplace(playMusic);
        }
    }
    public static List<Music> queryListMusic(SongList songList,String queryString){
        return musicDao.queryBuilder().where(MusicDao.Properties.SId.eq(songList.getId()))
                .whereOr(MusicDao.Properties.Name.like("%"+queryString+"%"),MusicDao.Properties.Singer.like("%"+queryString+"%"),MusicDao.Properties.Album.like("%"+queryString+"%"))
                .list();
    }
    public static void orderListMusicByDefault(SongList songList){
        songList.getMusics().clear();
        List<Music> musics=musicDao.queryBuilder().where(MusicDao.Properties.SId.eq(songList.getId()))
                .list();
        songList.getMusics().addAll(musics);
    }
    public static void orderListMusicByName(SongList songList){
        ArrayList<Music> musics=new ArrayList<Music>();
        musics.addAll(songList.getMusics());
        songList.getMusics().clear();
        Collections.sort(musics, new Comparator<Music>() {
            @Override
            public int compare(Music o1, Music o2) {
                //这里俩个是对属性判null处理，为null的都放到列表最下面
                if (null==o1.getName()){
                    return 1;
                }
                if (null==o2.getName()){
                    return -1;
                }
                return Collator.getInstance(Locale.CHINESE).compare(o1.getName(),o2.getName());
            }
        });
        songList.getMusics().addAll(musics);
    }
    public static void orderListMusicBySinger(SongList songList){
        ArrayList<Music> musics=new ArrayList<Music>();
        musics.addAll(songList.getMusics());
        songList.getMusics().clear();
        Collections.sort(musics, new Comparator<Music>() {
            @Override
            public int compare(Music o1, Music o2) {
                //这里俩个是对属性判null处理，为null的都放到列表最下面
                if (null==o1.getSinger()){
                    return 1;
                }
                if (null==o2.getSinger()){
                    return -1;
                }
                return Collator.getInstance(Locale.CHINESE).compare(o1.getSinger(),o2.getSinger());
            }
        });
        songList.getMusics().addAll(musics);
    }
    public static void orderListMusicByAlbum(SongList songList){
        ArrayList<Music> musics=new ArrayList<Music>();
        musics.addAll(songList.getMusics());
        songList.getMusics().clear();
        Collections.sort(musics, new Comparator<Music>() {
            @Override
            public int compare(Music o1, Music o2) {
                //这里俩个是对属性判null处理，为null的都放到列表最下面
                if (null==o1.getAlbum()){
                    return 1;
                }
                if (null==o2.getAlbum()){
                    return -1;
                }
                return Collator.getInstance(Locale.CHINESE).compare(o1.getAlbum(),o2.getAlbum());
            }
        });
        songList.getMusics().addAll(musics);
    }
    /**
     * 添加音乐至播放列表
     * @param playMusicBean
     */
    public static void addPlayMusic(PlayMusicBean playMusicBean){ playListDao.insertOrReplace(playMusicBean);}

    /**
     * 将音乐从播放列表移走
     * @param playMusicBean
     */
    public static void removePlayMusic(PlayMusicBean playMusicBean){
        playListDao.delete(playMusicBean);
    }

    /**
     * 清空播放列表
     */
    public static void emptyPlayMusicList(){ playListDao.deleteAll(); }
}
