package com.example.musicplayerdome.bean;

import java.util.ArrayList;

public class MyPlayListBean {
    private long id;
    private String name;
    private String creater;
    private String introduce;
    private String cover;
    private int listenCount;
    private ArrayList<Music> musics;

    public MyPlayListBean() {
    }

    public MyPlayListBean(String name, String creater, String introduce, String cover, int listenCount, ArrayList<Music> musics) {
        this.name = name;
        this.creater = creater;
        this.introduce = introduce;
        this.cover = cover;
        this.listenCount = listenCount;
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

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
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

    public int getListenCount() {
        return listenCount;
    }

    public void setListenCount(int listenCount) {
        this.listenCount = listenCount;
    }

    public ArrayList<Music> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<Music> musics) {
        this.musics = musics;
    }
}
