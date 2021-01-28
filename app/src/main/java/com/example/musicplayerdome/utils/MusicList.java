package com.example.musicplayerdome.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.musicplayerdome.bean.Music;
import com.example.musicplayerdome.bean.SongList;
import com.example.musicplayerdome.dao.MusicDaoImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicList {
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
    public static ArrayList<Music> musicList = new ArrayList<Music>();
    public static ArrayList<Music> getMusicData(Context context) {
        List<SongList> songLists=MusicDaoImpl.getSongLists();
        musicList.clear();
        getLocalList(context);
//        List<Music> musics=(List<Music>)musicList;
//        if (songLists.size()==0){
//            SongList songList=new SongList();
//            songList.setIntroduce("暂无简介");
//            songList.setName("我的喜欢");
//            songList.setTime(new Date());
//            songList.setCoverImg("");
//            MusicDaoImpl.createSongList(songList);
//            MusicDaoImpl.addMusicToSongList(songList,musics);
//        }else {
//            SongList songList=songLists.get(0);
//            MusicDaoImpl.emptySongList(songList);
//            MusicDaoImpl.addMusicToSongList(songList,musics);
//            songList.setIntroduce("暂无简介");
//            songList.setName("我的喜欢");
//            songList.setTime(new Date());
//            songList.setCoverImg("");
//            MusicDaoImpl.updateSongListMsg(songList);
//        }
        return musicList;
    }
    public static ArrayList<Music> getOnlineList(){
        Music music1=new Music();
        music1.setId((long) 1);
        music1.setSId((long)2);
        music1.setAlbumImg("http://www.foxluo.cn/myapp/img/aliyPay.jpg");
        music1.setAlbum("没有发生的爱情");
        music1.setName("无人之岛");
        music1.setSize(30355624 );
        music1.setSinger("任然");
        music1.setTime(285000);
        music1.setUrl("http://www.foxluo.cn/myapp/music/music.flac");
        music1.setProgress(0);
        Music music2=new Music();
        music2.setId((long) 1);
        music2.setSId((long)2);
        music2.setAlbumImg("http://www.foxluo.cn/myapp/img/aliyPay.jpg");
        music2.setAlbum("无人之岛");
        music2.setName("无人之岛 （Cover：任然）");
        music2.setSize(0);
        music2.setSinger("是你的垚");
        music2.setTime(143*1000);
        music2.setUrl("http://www.foxluo.cn/myapp/music/music2.mp3");
        music2.setProgress(0);
        Music music3=new Music();
        music3.setId((long) 1);
        music3.setSId((long)2);
        music3.setAlbumImg("http://www.foxluo.cn/myapp/img/aliyPay.jpg");
        music3.setAlbum("黄老板");
        music3.setName(" Ritmo");
        music3.setSize(0);
        music3.setSinger("十月忘");
        music3.setTime(41000);
        music3.setUrl("http://www.foxluo.cn/myapp/music/music3.mp3");
        music3.setProgress(0);
        musicList.clear();
        musicList.add(music1);
        musicList.add(music2);
        musicList.add(music3);
        return musicList;
    }
    public static ArrayList<Music> getLocalList(Context context){
        musicList.clear();
        ContentResolver cr = context.getContentResolver();
        if (cr != null) {
            // Get all the music
            Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (null == cursor) {
                return null;
            }
            if (cursor.moveToFirst()) {
                do {
                    Music m = new Music();
                    Long id=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    if ("<unknown>".equals(singer)) {
                        singer = "<unknown>";
                    }
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                    long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    if (name.contains("-")){
                        name=name.split("-")[1];
                    }
                    if (name.contains(".mp3")){
                        name=name.split("\\.mp3")[0];
                    }else if (name.contains(".flac")){
                        name=name.split("\\.flac")[0];
                    }else if (name.contains(".wav")){
                        name=name.split("\\.wav")[0];
                    }else if (name.contains(".wma")){
                        name=name.split("\\.wma")[0];
                    }
                    name.trim();
                    Long albumId=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    //文件是否为音乐
                    int isMusicInt = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
                    Uri uri = ContentUris.withAppendedId(albumArtUri, albumId);
                    //对歌曲进行筛选
                    if ((isMusicInt != 0) && (size > 0)) {
                        m.setId(id);
                        m.setTitle(title);
                        m.setSinger(singer);
                        m.setAlbum(album);
                        m.setSize(size);
                        m.setTime(time);
                        m.setUrl(url);
                        m.setName(name);
                        m.setAlbumImg(uri.toString());
                        m.setProgress(100);
                        m.setSId((long)1);
                        m.setMId((long) -1);
                        musicList.add(m);
                    }
                } while (cursor.moveToNext());
            }
        }
        return musicList;
    }
}

