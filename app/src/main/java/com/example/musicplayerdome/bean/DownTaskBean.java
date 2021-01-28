package com.example.musicplayerdome.bean;

public class DownTaskBean {
    private long taskId=-1;
    private String taskName;
    private int progress;
    private String downSpeed;
    private boolean downing;
    private String downUrl;
    private boolean pause;
    private Music music;
    public DownTaskBean() {
    }

    public DownTaskBean(long taskId, String taskName, int progress, String downSpeed, boolean downing, String downUrl, boolean pause, Music music) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.progress = progress;
        this.downSpeed = downSpeed;
        this.downing = downing;
        this.downUrl = downUrl;
        this.pause = pause;
        this.music = music;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getDownSpeed() {
        return downSpeed;
    }

    public void setDownSpeed(String downSpeed) {
        this.downSpeed = downSpeed;
    }

    public boolean isDowning() {
        return downing;
    }

    public void setDowning(boolean downing) {
        this.downing = downing;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }
}
