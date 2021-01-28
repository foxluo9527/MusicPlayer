package com.example.musicplayerdome.bean;

import java.util.List;

public class TopListBean {
    private String picUrl;
    private long id;
    private String name;
    private String updateString;
    private int playCount;
    private List<TopBean> taskBeans;

    public TopListBean() {
    }

    public TopListBean(String picUrl, int id, String name, String updateString, int playCount, List<TopBean> taskBeans) {
        this.picUrl = picUrl;
        this.id = id;
        this.name = name;
        this.updateString = updateString;
        this.playCount = playCount;
        this.taskBeans = taskBeans;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
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

    public String getUpdateString() {
        return updateString;
    }

    public void setUpdateString(String updateString) {
        this.updateString = updateString;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public List<TopBean> getTaskBeans() {
        return taskBeans;
    }

    public void setTaskBeans(List<TopBean> taskBeans) {
        this.taskBeans = taskBeans;
    }

    public static class TopBean{
        private String singer;
        private String name;

        public TopBean(String singer, String name) {
            this.singer = singer;
            this.name = name;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
