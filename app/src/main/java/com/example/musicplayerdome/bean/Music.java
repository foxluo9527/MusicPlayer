package com.example.musicplayerdome.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Objects;

//The Music class implements the Parcelable interface and can be serialized.
@Entity
public  class Music implements Parcelable {
    @Id(autoincrement = true)
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
    //Non-parametric construction
    public Music(){};


    //    Deserialization
    protected Music(Parcel in) {
        id=in.readLong();
        sId=in.readLong();
        mId=in.readLong();
        title = in.readString();
        singer = in.readString();
        album = in.readString();
        url = in.readString();
        size = in.readLong();
        time = in.readLong();
        name = in.readString();
        albumImg=in.readString();
        progress=in.readInt();
    }


    @Generated(hash = 904761564)
    public Music(Long id, Long sId, Long mId, String title, String singer, String album, String url,
            long size, long time, String name, String albumImg, int progress) {
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

    //    Serialization
    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAlbumImg() {
        return albumImg;
    }
    public void setAlbumImg(String albumImg) {
        this.albumImg = albumImg;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getSinger()
    {
        return singer;
    }
    public void setSinger(String singer)
    {
        this.singer = singer;
    }
    public String getAlbum(){ return album;}
    public void setAlbum(String album)
    {
        this.album = album;
    }
    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }
    public long getSize()
    {
        return size;
    }
    public void setSize(long size)
    {
        this.size =size;
    }
    public long getTime()
    {
        return time;
    }
    public void setTime(long time)
    {
        this.time = time;
    }


    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return size == music.size &&
                time == music.time &&
                progress == music.progress &&
                Objects.equals(id, music.id) &&
                Objects.equals(sId, music.sId) &&
                Objects.equals(mId, music.mId) &&
                Objects.equals(title, music.title) &&
                Objects.equals(singer, music.singer) &&
                Objects.equals(album, music.album) &&
                Objects.equals(url, music.url) &&
                Objects.equals(name, music.name) &&
                Objects.equals(albumImg, music.albumImg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sId, title, singer, album, url, size, time, name, albumImg, progress);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(sId);
        dest.writeLong(mId);
        dest.writeString(title);
        dest.writeString(singer);
        dest.writeString(album);
        dest.writeString(url);
        dest.writeLong(size);
        dest.writeLong(time);
        dest.writeString(name);
        dest.writeString(albumImg);
        dest.writeInt(progress);
    }


    public Long getMId() {
        return this.mId;
    }


    public void setMId(Long mId) {
        this.mId = mId;
    }
}
