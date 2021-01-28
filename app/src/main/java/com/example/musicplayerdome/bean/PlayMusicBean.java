package com.example.musicplayerdome.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PlayMusicBean{
    @Id(autoincrement = true)
    @NotNull
    private Long id;
    private Long sId;
    private Long mId;
    private String title;
    private String singer;
    private String album;
    private String url;
    private long size;
    private long time;
    private String name;
    private String albumImg;
    private int progress;
    @Generated(hash = 1066245200)
    public PlayMusicBean(@NotNull Long id, Long sId, Long mId, String title,
            String singer, String album, String url, long size, long time,
            String name, String albumImg, int progress) {
        this.id = id;
        this.sId = sId;
        this.mId = mId;
        this.title = title;
        this.singer = singer;
        this.album = album;
        this.url = url;
        this.size = size;
        this.time = time;
        this.name = name;
        this.albumImg = albumImg;
        this.progress = progress;
    }
    @Generated(hash = 1345112530)
    public PlayMusicBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSinger() {
        return this.singer;
    }
    public void setSinger(String singer) {
        this.singer = singer;
    }
    public String getAlbum() {
        return this.album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public long getSize() {
        return this.size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAlbumImg() {
        return this.albumImg;
    }
    public void setAlbumImg(String albumImg) {
        this.albumImg = albumImg;
    }
    public int getProgress() {
        return this.progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
    public Long getSId() {
        return this.sId;
    }
    public void setSId(Long sId) {
        this.sId = sId;
    }
    public Long getMId() {
        return this.mId;
    }
    public void setMId(Long mId) {
        this.mId = mId;
    }
}
