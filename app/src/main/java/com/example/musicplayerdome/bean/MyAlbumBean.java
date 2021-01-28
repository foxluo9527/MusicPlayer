package com.example.musicplayerdome.bean;

import java.util.ArrayList;

public class MyAlbumBean {
    private long id;
    private String name;
    private String singer;
    private String createTime;
    private String introduce;
    private String cover;
    private ArrayList<Music> musics;

    public MyAlbumBean() {
    }

    public MyAlbumBean(String name, String singer, String createTime, String introduce, String cover, ArrayList<Music> musics) {
        this.name = name;
        this.singer = singer;
        this.createTime = createTime;
        this.introduce = introduce;
        this.cover = cover;
        this.musics = musics;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public ArrayList<Music> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<Music> musics) {
        this.musics = musics;
    }
}
