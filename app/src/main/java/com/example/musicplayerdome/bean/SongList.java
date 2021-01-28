package com.example.musicplayerdome.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.DaoException;

/**
 * 歌单实体类
 * 歌单id
 * 歌单名称
 * 封面图片
 * 创建时间
 * 歌单简介
 * 歌曲列表
 */
@Entity
public class SongList {
    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String coverImg;
    private Date time;
    private String introduce;
    @ToMany(referencedJoinProperty = "sId")
    private List<Music> musics;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 741189411)
    private transient SongListDao myDao;

    public SongList() {
    }
    @Generated(hash = 1513493221)
    public SongList(Long id, String name, String coverImg, Date time, String introduce) {
        this.id = id;
        this.name = name;
        this.coverImg = coverImg;
        this.time = time;
        this.introduce = introduce;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCoverImg() {
        return this.coverImg;
    }
    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }
    public Date getTime() {
        return this.time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public String getIntroduce() { return introduce; }
    public void setIntroduce(String introduce) { this.introduce = introduce; }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 473653134)
    public List<Music> getMusics() {
        if (musics == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MusicDao targetDao = daoSession.getMusicDao();
            List<Music> musicsNew = targetDao._querySongList_Musics(id);
            synchronized (this) {
                if (musics == null) {
                    musics = musicsNew;
                }
            }
        }
        return musics;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1369131536)
    public synchronized void resetMusics() {
        musics = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1874485990)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSongListDao() : null;
    }
}
