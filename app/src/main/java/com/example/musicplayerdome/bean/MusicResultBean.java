package com.example.musicplayerdome.bean;

import java.util.List;

public class MusicResultBean {

    /**
     * songs : [{"name":"秘密","id":543656128,"position":14,"alias":[],"status":0,"fee":8,"copyrightId":684010,"disc":"1","no":14,"artists":[{"name":"张震岳","id":6453,"picId":0,"img1v1Id":0,"briefDesc":"","picUrl":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","img1v1Url":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","albumSize":0,"alias":[],"trans":"","musicSize":0,"topicPerson":0}],"album":{"name":"爱曲初体验","id":37873032,"type":"专辑","size":14,"picId":109951163175719996,"blurPicUrl":"http://p2.music.126.net/Jdlbgs636ICWm1MDZJaFsw==/109951163175719996.jpg","companyId":0,"pic":109951163175719996,"picUrl":"http://p2.music.126.net/Jdlbgs636ICWm1MDZJaFsw==/109951163175719996.jpg","publishTime":907171200000,"description":"","tags":"","company":"滚石唱片","briefDesc":"","artist":{"name":"","id":0,"picId":0,"img1v1Id":0,"briefDesc":"","picUrl":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","img1v1Url":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","albumSize":0,"alias":[],"trans":"","musicSize":0,"topicPerson":0},"songs":[],"alias":[],"status":-1,"copyrightId":684010,"commentThreadId":"R_AL_3_37873032","artists":[{"name":"群星","id":122455,"picId":0,"img1v1Id":0,"briefDesc":"","picUrl":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","img1v1Url":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","albumSize":0,"alias":[],"trans":"","musicSize":0,"topicPerson":0}],"subType":"录音室版","transName":null,"mark":0,"picId_str":"109951163175719996"},"starred":false,"popularity":100,"score":100,"starredNum":0,"duration":322573,"playedNum":0,"dayPlays":0,"hearTime":0,"ringtone":null,"crbt":null,"audition":null,"copyFrom":"","commentThreadId":"R_SO_4_543656128","rtUrl":null,"ftype":0,"rtUrls":[],"copyright":0,"transName":null,"sign":null,"mark":0,"hMusic":{"name":null,"id":3594870244,"size":12905578,"extension":"mp3","sr":44100,"dfsId":0,"bitrate":320000,"playTime":322573,"volumeDelta":0},"mMusic":{"name":null,"id":3594870245,"size":7743364,"extension":"mp3","sr":44100,"dfsId":0,"bitrate":192000,"playTime":322573,"volumeDelta":0},"lMusic":{"name":null,"id":3594870246,"size":5162257,"extension":"mp3","sr":44100,"dfsId":0,"bitrate":128000,"playTime":322573,"volumeDelta":0},"bMusic":{"name":null,"id":3594870246,"size":5162257,"extension":"mp3","sr":44100,"dfsId":0,"bitrate":128000,"playTime":322573,"volumeDelta":0},"mvid":0,"rtype":0,"rurl":null,"mp3Url":null}]
     * equalizers : {}
     * code : 200
     */

    private EqualizersBean equalizers;
    private int code;
    private List<SongsBean> songs;

    public EqualizersBean getEqualizers() {
        return equalizers;
    }

    public void setEqualizers(EqualizersBean equalizers) {
        this.equalizers = equalizers;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<SongsBean> getSongs() {
        return songs;
    }

    public void setSongs(List<SongsBean> songs) {
        this.songs = songs;
    }

    public static class EqualizersBean {
    }

    public static class SongsBean {
        /**
         * name : 秘密
         * id : 543656128
         * position : 14
         * alias : []
         * status : 0
         * fee : 8
         * copyrightId : 684010
         * disc : 1
         * no : 14
         * artists : [{"name":"张震岳","id":6453,"picId":0,"img1v1Id":0,"briefDesc":"","picUrl":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","img1v1Url":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","albumSize":0,"alias":[],"trans":"","musicSize":0,"topicPerson":0}]
         * album : {"name":"爱曲初体验","id":37873032,"type":"专辑","size":14,"picId":109951163175719996,"blurPicUrl":"http://p2.music.126.net/Jdlbgs636ICWm1MDZJaFsw==/109951163175719996.jpg","companyId":0,"pic":109951163175719996,"picUrl":"http://p2.music.126.net/Jdlbgs636ICWm1MDZJaFsw==/109951163175719996.jpg","publishTime":907171200000,"description":"","tags":"","company":"滚石唱片","briefDesc":"","artist":{"name":"","id":0,"picId":0,"img1v1Id":0,"briefDesc":"","picUrl":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","img1v1Url":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","albumSize":0,"alias":[],"trans":"","musicSize":0,"topicPerson":0},"songs":[],"alias":[],"status":-1,"copyrightId":684010,"commentThreadId":"R_AL_3_37873032","artists":[{"name":"群星","id":122455,"picId":0,"img1v1Id":0,"briefDesc":"","picUrl":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","img1v1Url":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","albumSize":0,"alias":[],"trans":"","musicSize":0,"topicPerson":0}],"subType":"录音室版","transName":null,"mark":0,"picId_str":"109951163175719996"}
         * starred : false
         * popularity : 100.0
         * score : 100
         * starredNum : 0
         * duration : 322573
         * playedNum : 0
         * dayPlays : 0
         * hearTime : 0
         * ringtone : null
         * crbt : null
         * audition : null
         * copyFrom :
         * commentThreadId : R_SO_4_543656128
         * rtUrl : null
         * ftype : 0
         * rtUrls : []
         * copyright : 0
         * transName : null
         * sign : null
         * mark : 0
         * hMusic : {"name":null,"id":3594870244,"size":12905578,"extension":"mp3","sr":44100,"dfsId":0,"bitrate":320000,"playTime":322573,"volumeDelta":0}
         * mMusic : {"name":null,"id":3594870245,"size":7743364,"extension":"mp3","sr":44100,"dfsId":0,"bitrate":192000,"playTime":322573,"volumeDelta":0}
         * lMusic : {"name":null,"id":3594870246,"size":5162257,"extension":"mp3","sr":44100,"dfsId":0,"bitrate":128000,"playTime":322573,"volumeDelta":0}
         * bMusic : {"name":null,"id":3594870246,"size":5162257,"extension":"mp3","sr":44100,"dfsId":0,"bitrate":128000,"playTime":322573,"volumeDelta":0}
         * mvid : 0
         * rtype : 0
         * rurl : null
         * mp3Url : null
         */

        private String name;
        private int id;
        private int position;
        private int status;
        private int fee;
        private int copyrightId;
        private String disc;
        private int no;
        private AlbumBean album;
        private boolean starred;
        private double popularity;
        private int score;
        private int starredNum;
        private int duration;
        private int playedNum;
        private int dayPlays;
        private int hearTime;
        private Object ringtone;
        private Object crbt;
        private Object audition;
        private String copyFrom;
        private String commentThreadId;
        private Object rtUrl;
        private int ftype;
        private int copyright;
        private Object transName;
        private Object sign;
        private int mark;
        private HMusicBean hMusic;
        private MMusicBean mMusic;
        private LMusicBean lMusic;
        private BMusicBean bMusic;
        private int mvid;
        private int rtype;
        private Object rurl;
        private Object mp3Url;
        private List<?> alias;
        private List<ArtistsBeanX> artists;
        private List<?> rtUrls;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getFee() {
            return fee;
        }

        public void setFee(int fee) {
            this.fee = fee;
        }

        public int getCopyrightId() {
            return copyrightId;
        }

        public void setCopyrightId(int copyrightId) {
            this.copyrightId = copyrightId;
        }

        public String getDisc() {
            return disc;
        }

        public void setDisc(String disc) {
            this.disc = disc;
        }

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }

        public AlbumBean getAlbum() {
            return album;
        }

        public void setAlbum(AlbumBean album) {
            this.album = album;
        }

        public boolean isStarred() {
            return starred;
        }

        public void setStarred(boolean starred) {
            this.starred = starred;
        }

        public double getPopularity() {
            return popularity;
        }

        public void setPopularity(double popularity) {
            this.popularity = popularity;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getStarredNum() {
            return starredNum;
        }

        public void setStarredNum(int starredNum) {
            this.starredNum = starredNum;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getPlayedNum() {
            return playedNum;
        }

        public void setPlayedNum(int playedNum) {
            this.playedNum = playedNum;
        }

        public int getDayPlays() {
            return dayPlays;
        }

        public void setDayPlays(int dayPlays) {
            this.dayPlays = dayPlays;
        }

        public int getHearTime() {
            return hearTime;
        }

        public void setHearTime(int hearTime) {
            this.hearTime = hearTime;
        }

        public Object getRingtone() {
            return ringtone;
        }

        public void setRingtone(Object ringtone) {
            this.ringtone = ringtone;
        }

        public Object getCrbt() {
            return crbt;
        }

        public void setCrbt(Object crbt) {
            this.crbt = crbt;
        }

        public Object getAudition() {
            return audition;
        }

        public void setAudition(Object audition) {
            this.audition = audition;
        }

        public String getCopyFrom() {
            return copyFrom;
        }

        public void setCopyFrom(String copyFrom) {
            this.copyFrom = copyFrom;
        }

        public String getCommentThreadId() {
            return commentThreadId;
        }

        public void setCommentThreadId(String commentThreadId) {
            this.commentThreadId = commentThreadId;
        }

        public Object getRtUrl() {
            return rtUrl;
        }

        public void setRtUrl(Object rtUrl) {
            this.rtUrl = rtUrl;
        }

        public int getFtype() {
            return ftype;
        }

        public void setFtype(int ftype) {
            this.ftype = ftype;
        }

        public int getCopyright() {
            return copyright;
        }

        public void setCopyright(int copyright) {
            this.copyright = copyright;
        }

        public Object getTransName() {
            return transName;
        }

        public void setTransName(Object transName) {
            this.transName = transName;
        }

        public Object getSign() {
            return sign;
        }

        public void setSign(Object sign) {
            this.sign = sign;
        }

        public int getMark() {
            return mark;
        }

        public void setMark(int mark) {
            this.mark = mark;
        }

        public HMusicBean getHMusic() {
            return hMusic;
        }

        public void setHMusic(HMusicBean hMusic) {
            this.hMusic = hMusic;
        }

        public MMusicBean getMMusic() {
            return mMusic;
        }

        public void setMMusic(MMusicBean mMusic) {
            this.mMusic = mMusic;
        }

        public LMusicBean getLMusic() {
            return lMusic;
        }

        public void setLMusic(LMusicBean lMusic) {
            this.lMusic = lMusic;
        }

        public BMusicBean getBMusic() {
            return bMusic;
        }

        public void setBMusic(BMusicBean bMusic) {
            this.bMusic = bMusic;
        }

        public int getMvid() {
            return mvid;
        }

        public void setMvid(int mvid) {
            this.mvid = mvid;
        }

        public int getRtype() {
            return rtype;
        }

        public void setRtype(int rtype) {
            this.rtype = rtype;
        }

        public Object getRurl() {
            return rurl;
        }

        public void setRurl(Object rurl) {
            this.rurl = rurl;
        }

        public Object getMp3Url() {
            return mp3Url;
        }

        public void setMp3Url(Object mp3Url) {
            this.mp3Url = mp3Url;
        }

        public List<?> getAlias() {
            return alias;
        }

        public void setAlias(List<?> alias) {
            this.alias = alias;
        }

        public List<ArtistsBeanX> getArtists() {
            return artists;
        }

        public void setArtists(List<ArtistsBeanX> artists) {
            this.artists = artists;
        }

        public List<?> getRtUrls() {
            return rtUrls;
        }

        public void setRtUrls(List<?> rtUrls) {
            this.rtUrls = rtUrls;
        }

        public static class AlbumBean {
            /**
             * name : 爱曲初体验
             * id : 37873032
             * type : 专辑
             * size : 14
             * picId : 109951163175719996
             * blurPicUrl : http://p2.music.126.net/Jdlbgs636ICWm1MDZJaFsw==/109951163175719996.jpg
             * companyId : 0
             * pic : 109951163175719996
             * picUrl : http://p2.music.126.net/Jdlbgs636ICWm1MDZJaFsw==/109951163175719996.jpg
             * publishTime : 907171200000
             * description :
             * tags :
             * company : 滚石唱片
             * briefDesc :
             * artist : {"name":"","id":0,"picId":0,"img1v1Id":0,"briefDesc":"","picUrl":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","img1v1Url":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","albumSize":0,"alias":[],"trans":"","musicSize":0,"topicPerson":0}
             * songs : []
             * alias : []
             * status : -1
             * copyrightId : 684010
             * commentThreadId : R_AL_3_37873032
             * artists : [{"name":"群星","id":122455,"picId":0,"img1v1Id":0,"briefDesc":"","picUrl":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","img1v1Url":"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg","albumSize":0,"alias":[],"trans":"","musicSize":0,"topicPerson":0}]
             * subType : 录音室版
             * transName : null
             * mark : 0
             * picId_str : 109951163175719996
             */

            private String name;
            private int id;
            private String type;
            private int size;
            private long picId;
            private String blurPicUrl;
            private int companyId;
            private long pic;
            private String picUrl;
            private long publishTime;
            private String description;
            private String tags;
            private String company;
            private String briefDesc;
            private ArtistBean artist;
            private int status;
            private int copyrightId;
            private String commentThreadId;
            private String subType;
            private Object transName;
            private int mark;
            private String picId_str;
            private List<?> songs;
            private List<?> alias;
            private List<ArtistsBean> artists;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public long getPicId() {
                return picId;
            }

            public void setPicId(long picId) {
                this.picId = picId;
            }

            public String getBlurPicUrl() {
                return blurPicUrl;
            }

            public void setBlurPicUrl(String blurPicUrl) {
                this.blurPicUrl = blurPicUrl;
            }

            public int getCompanyId() {
                return companyId;
            }

            public void setCompanyId(int companyId) {
                this.companyId = companyId;
            }

            public long getPic() {
                return pic;
            }

            public void setPic(long pic) {
                this.pic = pic;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public long getPublishTime() {
                return publishTime;
            }

            public void setPublishTime(long publishTime) {
                this.publishTime = publishTime;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getTags() {
                return tags;
            }

            public void setTags(String tags) {
                this.tags = tags;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public String getBriefDesc() {
                return briefDesc;
            }

            public void setBriefDesc(String briefDesc) {
                this.briefDesc = briefDesc;
            }

            public ArtistBean getArtist() {
                return artist;
            }

            public void setArtist(ArtistBean artist) {
                this.artist = artist;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getCopyrightId() {
                return copyrightId;
            }

            public void setCopyrightId(int copyrightId) {
                this.copyrightId = copyrightId;
            }

            public String getCommentThreadId() {
                return commentThreadId;
            }

            public void setCommentThreadId(String commentThreadId) {
                this.commentThreadId = commentThreadId;
            }

            public String getSubType() {
                return subType;
            }

            public void setSubType(String subType) {
                this.subType = subType;
            }

            public Object getTransName() {
                return transName;
            }

            public void setTransName(Object transName) {
                this.transName = transName;
            }

            public int getMark() {
                return mark;
            }

            public void setMark(int mark) {
                this.mark = mark;
            }

            public String getPicId_str() {
                return picId_str;
            }

            public void setPicId_str(String picId_str) {
                this.picId_str = picId_str;
            }

            public List<?> getSongs() {
                return songs;
            }

            public void setSongs(List<?> songs) {
                this.songs = songs;
            }

            public List<?> getAlias() {
                return alias;
            }

            public void setAlias(List<?> alias) {
                this.alias = alias;
            }

            public List<ArtistsBean> getArtists() {
                return artists;
            }

            public void setArtists(List<ArtistsBean> artists) {
                this.artists = artists;
            }

            public static class ArtistBean {
                /**
                 * name :
                 * id : 0
                 * picId : 0
                 * img1v1Id : 0
                 * briefDesc :
                 * picUrl : http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg
                 * img1v1Url : http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg
                 * albumSize : 0
                 * alias : []
                 * trans :
                 * musicSize : 0
                 * topicPerson : 0
                 */

                private String name;
                private int id;
                private int picId;
                private int img1v1Id;
                private String briefDesc;
                private String picUrl;
                private String img1v1Url;
                private int albumSize;
                private String trans;
                private int musicSize;
                private int topicPerson;
                private List<?> alias;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getPicId() {
                    return picId;
                }

                public void setPicId(int picId) {
                    this.picId = picId;
                }

                public int getImg1v1Id() {
                    return img1v1Id;
                }

                public void setImg1v1Id(int img1v1Id) {
                    this.img1v1Id = img1v1Id;
                }

                public String getBriefDesc() {
                    return briefDesc;
                }

                public void setBriefDesc(String briefDesc) {
                    this.briefDesc = briefDesc;
                }

                public String getPicUrl() {
                    return picUrl;
                }

                public void setPicUrl(String picUrl) {
                    this.picUrl = picUrl;
                }

                public String getImg1v1Url() {
                    return img1v1Url;
                }

                public void setImg1v1Url(String img1v1Url) {
                    this.img1v1Url = img1v1Url;
                }

                public int getAlbumSize() {
                    return albumSize;
                }

                public void setAlbumSize(int albumSize) {
                    this.albumSize = albumSize;
                }

                public String getTrans() {
                    return trans;
                }

                public void setTrans(String trans) {
                    this.trans = trans;
                }

                public int getMusicSize() {
                    return musicSize;
                }

                public void setMusicSize(int musicSize) {
                    this.musicSize = musicSize;
                }

                public int getTopicPerson() {
                    return topicPerson;
                }

                public void setTopicPerson(int topicPerson) {
                    this.topicPerson = topicPerson;
                }

                public List<?> getAlias() {
                    return alias;
                }

                public void setAlias(List<?> alias) {
                    this.alias = alias;
                }
            }

            public static class ArtistsBean {
                /**
                 * name : 群星
                 * id : 122455
                 * picId : 0
                 * img1v1Id : 0
                 * briefDesc :
                 * picUrl : http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg
                 * img1v1Url : http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg
                 * albumSize : 0
                 * alias : []
                 * trans :
                 * musicSize : 0
                 * topicPerson : 0
                 */

                private String name;
                private int id;
                private int picId;
                private int img1v1Id;
                private String briefDesc;
                private String picUrl;
                private String img1v1Url;
                private int albumSize;
                private String trans;
                private int musicSize;
                private int topicPerson;
                private List<?> alias;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getPicId() {
                    return picId;
                }

                public void setPicId(int picId) {
                    this.picId = picId;
                }

                public int getImg1v1Id() {
                    return img1v1Id;
                }

                public void setImg1v1Id(int img1v1Id) {
                    this.img1v1Id = img1v1Id;
                }

                public String getBriefDesc() {
                    return briefDesc;
                }

                public void setBriefDesc(String briefDesc) {
                    this.briefDesc = briefDesc;
                }

                public String getPicUrl() {
                    return picUrl;
                }

                public void setPicUrl(String picUrl) {
                    this.picUrl = picUrl;
                }

                public String getImg1v1Url() {
                    return img1v1Url;
                }

                public void setImg1v1Url(String img1v1Url) {
                    this.img1v1Url = img1v1Url;
                }

                public int getAlbumSize() {
                    return albumSize;
                }

                public void setAlbumSize(int albumSize) {
                    this.albumSize = albumSize;
                }

                public String getTrans() {
                    return trans;
                }

                public void setTrans(String trans) {
                    this.trans = trans;
                }

                public int getMusicSize() {
                    return musicSize;
                }

                public void setMusicSize(int musicSize) {
                    this.musicSize = musicSize;
                }

                public int getTopicPerson() {
                    return topicPerson;
                }

                public void setTopicPerson(int topicPerson) {
                    this.topicPerson = topicPerson;
                }

                public List<?> getAlias() {
                    return alias;
                }

                public void setAlias(List<?> alias) {
                    this.alias = alias;
                }
            }
        }

        public static class HMusicBean {
            /**
             * name : null
             * id : 3594870244
             * size : 12905578
             * extension : mp3
             * sr : 44100
             * dfsId : 0
             * bitrate : 320000
             * playTime : 322573
             * volumeDelta : 0.0
             */

            private Object name;
            private long id;
            private int size;
            private String extension;
            private int sr;
            private int dfsId;
            private int bitrate;
            private int playTime;
            private double volumeDelta;

            public Object getName() {
                return name;
            }

            public void setName(Object name) {
                this.name = name;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public String getExtension() {
                return extension;
            }

            public void setExtension(String extension) {
                this.extension = extension;
            }

            public int getSr() {
                return sr;
            }

            public void setSr(int sr) {
                this.sr = sr;
            }

            public int getDfsId() {
                return dfsId;
            }

            public void setDfsId(int dfsId) {
                this.dfsId = dfsId;
            }

            public int getBitrate() {
                return bitrate;
            }

            public void setBitrate(int bitrate) {
                this.bitrate = bitrate;
            }

            public int getPlayTime() {
                return playTime;
            }

            public void setPlayTime(int playTime) {
                this.playTime = playTime;
            }

            public double getVolumeDelta() {
                return volumeDelta;
            }

            public void setVolumeDelta(double volumeDelta) {
                this.volumeDelta = volumeDelta;
            }
        }

        public static class MMusicBean {
            /**
             * name : null
             * id : 3594870245
             * size : 7743364
             * extension : mp3
             * sr : 44100
             * dfsId : 0
             * bitrate : 192000
             * playTime : 322573
             * volumeDelta : 0.0
             */

            private Object name;
            private long id;
            private int size;
            private String extension;
            private int sr;
            private int dfsId;
            private int bitrate;
            private int playTime;
            private double volumeDelta;

            public Object getName() {
                return name;
            }

            public void setName(Object name) {
                this.name = name;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public String getExtension() {
                return extension;
            }

            public void setExtension(String extension) {
                this.extension = extension;
            }

            public int getSr() {
                return sr;
            }

            public void setSr(int sr) {
                this.sr = sr;
            }

            public int getDfsId() {
                return dfsId;
            }

            public void setDfsId(int dfsId) {
                this.dfsId = dfsId;
            }

            public int getBitrate() {
                return bitrate;
            }

            public void setBitrate(int bitrate) {
                this.bitrate = bitrate;
            }

            public int getPlayTime() {
                return playTime;
            }

            public void setPlayTime(int playTime) {
                this.playTime = playTime;
            }

            public double getVolumeDelta() {
                return volumeDelta;
            }

            public void setVolumeDelta(double volumeDelta) {
                this.volumeDelta = volumeDelta;
            }
        }

        public static class LMusicBean {
            /**
             * name : null
             * id : 3594870246
             * size : 5162257
             * extension : mp3
             * sr : 44100
             * dfsId : 0
             * bitrate : 128000
             * playTime : 322573
             * volumeDelta : 0.0
             */

            private Object name;
            private long id;
            private int size;
            private String extension;
            private int sr;
            private int dfsId;
            private int bitrate;
            private int playTime;
            private double volumeDelta;

            public Object getName() {
                return name;
            }

            public void setName(Object name) {
                this.name = name;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public String getExtension() {
                return extension;
            }

            public void setExtension(String extension) {
                this.extension = extension;
            }

            public int getSr() {
                return sr;
            }

            public void setSr(int sr) {
                this.sr = sr;
            }

            public int getDfsId() {
                return dfsId;
            }

            public void setDfsId(int dfsId) {
                this.dfsId = dfsId;
            }

            public int getBitrate() {
                return bitrate;
            }

            public void setBitrate(int bitrate) {
                this.bitrate = bitrate;
            }

            public int getPlayTime() {
                return playTime;
            }

            public void setPlayTime(int playTime) {
                this.playTime = playTime;
            }

            public double getVolumeDelta() {
                return volumeDelta;
            }

            public void setVolumeDelta(double volumeDelta) {
                this.volumeDelta = volumeDelta;
            }
        }

        public static class BMusicBean {
            /**
             * name : null
             * id : 3594870246
             * size : 5162257
             * extension : mp3
             * sr : 44100
             * dfsId : 0
             * bitrate : 128000
             * playTime : 322573
             * volumeDelta : 0.0
             */

            private Object name;
            private long id;
            private int size;
            private String extension;
            private int sr;
            private int dfsId;
            private int bitrate;
            private int playTime;
            private double volumeDelta;

            public Object getName() {
                return name;
            }

            public void setName(Object name) {
                this.name = name;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public String getExtension() {
                return extension;
            }

            public void setExtension(String extension) {
                this.extension = extension;
            }

            public int getSr() {
                return sr;
            }

            public void setSr(int sr) {
                this.sr = sr;
            }

            public int getDfsId() {
                return dfsId;
            }

            public void setDfsId(int dfsId) {
                this.dfsId = dfsId;
            }

            public int getBitrate() {
                return bitrate;
            }

            public void setBitrate(int bitrate) {
                this.bitrate = bitrate;
            }

            public int getPlayTime() {
                return playTime;
            }

            public void setPlayTime(int playTime) {
                this.playTime = playTime;
            }

            public double getVolumeDelta() {
                return volumeDelta;
            }

            public void setVolumeDelta(double volumeDelta) {
                this.volumeDelta = volumeDelta;
            }
        }

        public static class ArtistsBeanX {
            /**
             * name : 张震岳
             * id : 6453
             * picId : 0
             * img1v1Id : 0
             * briefDesc :
             * picUrl : http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg
             * img1v1Url : http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg
             * albumSize : 0
             * alias : []
             * trans :
             * musicSize : 0
             * topicPerson : 0
             */

            private String name;
            private int id;
            private int picId;
            private int img1v1Id;
            private String briefDesc;
            private String picUrl;
            private String img1v1Url;
            private int albumSize;
            private String trans;
            private int musicSize;
            private int topicPerson;
            private List<?> alias;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPicId() {
                return picId;
            }

            public void setPicId(int picId) {
                this.picId = picId;
            }

            public int getImg1v1Id() {
                return img1v1Id;
            }

            public void setImg1v1Id(int img1v1Id) {
                this.img1v1Id = img1v1Id;
            }

            public String getBriefDesc() {
                return briefDesc;
            }

            public void setBriefDesc(String briefDesc) {
                this.briefDesc = briefDesc;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public String getImg1v1Url() {
                return img1v1Url;
            }

            public void setImg1v1Url(String img1v1Url) {
                this.img1v1Url = img1v1Url;
            }

            public int getAlbumSize() {
                return albumSize;
            }

            public void setAlbumSize(int albumSize) {
                this.albumSize = albumSize;
            }

            public String getTrans() {
                return trans;
            }

            public void setTrans(String trans) {
                this.trans = trans;
            }

            public int getMusicSize() {
                return musicSize;
            }

            public void setMusicSize(int musicSize) {
                this.musicSize = musicSize;
            }

            public int getTopicPerson() {
                return topicPerson;
            }

            public void setTopicPerson(int topicPerson) {
                this.topicPerson = topicPerson;
            }

            public List<?> getAlias() {
                return alias;
            }

            public void setAlias(List<?> alias) {
                this.alias = alias;
            }
        }
    }
}
